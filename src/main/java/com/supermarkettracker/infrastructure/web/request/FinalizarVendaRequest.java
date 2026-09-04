package com.supermarkettracker.infrastructure.web.request;

import com.supermarkettracker.application.command.FinalizarVendaCommand;
import com.supermarkettracker.application.command.ItemVendaCheckoutCommand;
import com.supermarkettracker.application.command.PagamentoCheckoutCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FinalizarVendaRequest(@NotNull UUID empresaId, UUID lojaId, UUID sessaoCaixaId,
                                    @NotNull UUID usuarioId, UUID clienteId, Long numero,
                                    @NotNull @DecimalMin("0.00") BigDecimal desconto,
                                    @NotNull @DecimalMin("0.00") BigDecimal acrescimo, String observacao,
                                    @NotEmpty List<@Valid ItemVendaRequest> itens,
                                    @NotEmpty List<@Valid PagamentoRequest> pagamentos) {
    public FinalizarVendaCommand toCommand() {
        return new FinalizarVendaCommand(empresaId, lojaId, sessaoCaixaId, usuarioId, clienteId, numero, desconto,
                acrescimo, observacao, itens.stream().map(this::itemCommand).toList(),
                pagamentos.stream().map(this::pagamentoCommand).toList());
    }

    private ItemVendaCheckoutCommand itemCommand(ItemVendaRequest item) {
        return new ItemVendaCheckoutCommand(item.produtoId(), item.numero(), item.produtoNome(), item.codigoBarras(),
                item.unidadeMedida(), item.quantidade(), item.precoUnitario(), item.precoCompraUnitario(),
                item.desconto(), item.acrescimo());
    }

    private PagamentoCheckoutCommand pagamentoCommand(PagamentoRequest pagamento) {
        return new PagamentoCheckoutCommand(pagamento.contaBancariaId(), pagamento.tipo(), pagamento.valor(),
                pagamento.modalidadeCartao(), pagamento.parcelas() == null ? 0 : pagamento.parcelas(),
                pagamento.referencia(), pagamento.tokenCartao());
    }
}
