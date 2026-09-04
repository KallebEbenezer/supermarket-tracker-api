package com.supermarkettracker.application.usecase;

import com.supermarkettracker.application.validator.ValidacaoCommand;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.PagamentoCartao;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.enums.TipoPagamento;
import com.supermarkettracker.domain.model.valueobject.Dinheiro;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.infrastructure.websocket.PaymentWebSocketPublisher;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessarCartaoNfcUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProcessarCartaoNfcUseCase.class);

    private final PagamentoRepository pagamentos;
    private final CartaoGateway cartaoGateway;
    private final PaymentWebSocketPublisher eventPublisher;

    public ProcessarCartaoNfcUseCase(PagamentoRepository pagamentos, CartaoGateway cartaoGateway,
            PaymentWebSocketPublisher eventPublisher) {
        this.pagamentos = pagamentos;
        this.cartaoGateway = cartaoGateway;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ResultadoCartao executar(UUID vendaId, UUID contaBancariaId, BigDecimal valor,
                                    ModalidadeCartao modalidade, short parcelas, String referenciaNfc) {
        ValidacaoCommand.obrigatorio(vendaId, "Venda");
        ValidacaoCommand.positivo(valor, "Valor do pagamento");
        ValidacaoCommand.obrigatorio(modalidade, "Modalidade do cartao");
        if (parcelas < 1) throw new RegraDeDominioException("Parcelas deve ser ao menos 1");
        if (modalidade == ModalidadeCartao.DEBITO && parcelas != 1) {
            throw new RegraDeDominioException("Pagamento no debito deve possuir uma parcela");
        }

        Identificador pagamentoId = Identificador.novo();
        Instant agora = Instant.now();

        Pagamento pagamento = new Pagamento(pagamentoId, new Identificador(vendaId),
                contaBancariaId != null ? new Identificador(contaBancariaId) : null,
                TipoPagamento.CARTAO, new Dinheiro(valor), StatusPagamento.PENDENTE, null,
                referenciaNfc, agora, agora);

        try {
            CartaoGateway.TransacaoCartao transacao = cartaoGateway.processar(
                    pagamentoId, new Dinheiro(valor), modalidade, parcelas, referenciaNfc);

            if ("APROVADO".equalsIgnoreCase(transacao.status())) {
                pagamentos.salvarCartao(
                        new Pagamento(pagamentoId, new Identificador(vendaId),
                                contaBancariaId != null ? new Identificador(contaBancariaId) : null,
                                TipoPagamento.CARTAO, new Dinheiro(valor), StatusPagamento.APROVADO, agora,
                                transacao.transacaoId(), agora, agora),
                        new PagamentoCartao(pagamentoId, modalidade, parcelas, null, null,
                                transacao.codigoAutorizacao(), null, transacao.gateway(),
                                transacao.transacaoId(), transacao.status()));

                eventPublisher.publish(vendaId.toString(), "CARTAO", "APROVADO");
                log.info("Pagamento cartão aprovado: venda={}, valor={}, modalidade={}", vendaId, valor, modalidade);

                return new ResultadoCartao(true, "Pagamento aprovado", transacao.transacaoId(),
                        transacao.codigoAutorizacao());
            } else {
                pagamentos.atualizarStatus(pagamentoId, StatusPagamento.RECUSADO, null);
                eventPublisher.publish(vendaId.toString(), "CARTAO", "RECUSADO");
                log.info("Pagamento cartão rejeitado: venda={}, motivo={}", vendaId, transacao.status());

                return new ResultadoCartao(false, "Pagamento rejeitado pela adquirente", null, null);
            }
        } catch (Exception e) {
            pagamentos.atualizarStatus(pagamentoId, StatusPagamento.RECUSADO, null);
            eventPublisher.publish(vendaId.toString(), "CARTAO", "ERRO");
            log.error("Erro ao processar pagamento cartão: venda={}", vendaId, e);

            return new ResultadoCartao(false, "Erro ao processar pagamento: " + e.getMessage(), null, null);
        }
    }

    public record ResultadoCartao(boolean aprovado, String mensagem, String transacaoId,
                                  String codigoAutorizacao) { }
}
