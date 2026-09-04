package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.usecase.ProcessarCartaoNfcUseCase;
import com.supermarkettracker.domain.model.enums.ModalidadeCartao;
import com.supermarkettracker.infrastructure.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pagamento Cartão NFC")
@RestController
@RequestMapping("/api/v1/pagamentos/cartao")
public class CartaoNfcController {

    private final ProcessarCartaoNfcUseCase processarCartao;

    public CartaoNfcController(ProcessarCartaoNfcUseCase processarCartao) {
        this.processarCartao = processarCartao;
    }

    @Operation(summary = "Processar pagamento por cartão via NFC")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProcessarCartaoNfcUseCase.ResultadoCartao> processar(
            @RequestBody CartaoNfcRequest request) {
        var resultado = processarCartao.executar(
                request.vendaId(),
                request.contaBancariaId(),
                request.valor(),
                request.modalidade(),
                request.parcelas(),
                request.referenciaNfc());
        return ApiResponse.of(resultado);
    }

    public record CartaoNfcRequest(
            @NotNull UUID vendaId,
            UUID contaBancariaId,
            @NotNull @DecimalMin("0.01") BigDecimal valor,
            @NotNull ModalidadeCartao modalidade,
            short parcelas,
            String referenciaNfc
    ) { }
}
