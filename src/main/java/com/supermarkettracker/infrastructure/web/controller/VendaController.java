package com.supermarkettracker.infrastructure.web.controller;

import com.supermarkettracker.application.dto.*;
import com.supermarkettracker.application.mapper.PagamentoMapper;
import com.supermarkettracker.application.query.BuscarVendaQuery;
import com.supermarkettracker.application.query.ListarVendasQuery;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.infrastructure.web.dto.*;
import com.supermarkettracker.infrastructure.web.request.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Vendas")
@RestController @RequestMapping("/api/v1/vendas")
public class VendaController {
    private final BuscarVendaUseCase buscarVenda; private final FinalizarVendaUseCase finalizarVenda;
    private final ListarVendasUseCase listarVendas;
    private final PagamentoRepository pagamentoRepository;
    public VendaController(BuscarVendaUseCase buscarVenda, FinalizarVendaUseCase finalizarVenda,
            ListarVendasUseCase listarVendas, PagamentoRepository pagamentoRepository) { this.buscarVenda = buscarVenda; this.finalizarVenda = finalizarVenda; this.listarVendas = listarVendas; this.pagamentoRepository = pagamentoRepository; }
    @PostMapping("/finalizar") @ResponseStatus(HttpStatus.CREATED) public ApiResponse<VendaDto> finalizar(@Valid @RequestBody FinalizarVendaRequest request) { return ApiResponse.of(finalizarVenda.executar(request.toCommand())); }
    @GetMapping public ApiResponse<List<VendaDto>> listar(@RequestParam UUID empresaId, @RequestParam(required = false) UUID lojaId, @RequestParam(defaultValue = "50") int limite) { return ApiResponse.of(listarVendas.executar(new ListarVendasQuery(empresaId, lojaId, limite))); }
    @GetMapping("/{id}") public ApiResponse<VendaDto> buscar(@PathVariable UUID id) { return ApiResponse.of(buscarVenda.executar(new BuscarVendaQuery(id))); }
    @GetMapping("/{id}/pagamentos") public ApiResponse<List<PagamentoResponse>> listarPagamentos(@PathVariable UUID id) {
        var pagamentos = pagamentoRepository.listarPorVenda(new Identificador(id));
        var response = pagamentos.stream().map(p -> PagamentoResponse.of(p)).collect(Collectors.toList());
        return ApiResponse.of(response);
    }
    @GetMapping("/{id}/pix") public ApiResponse<PixDetailsResponse> buscarPixDetails(@PathVariable UUID id) {
        var pixOpt = pagamentoRepository.buscarPixPorVendaId(new Identificador(id));
        if (pixOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pagamento PIX nao encontrado para esta venda");
        }
        PagamentoPix pix = pixOpt.get();
        String status = pix.statusGateway() != null ? pix.statusGateway() : "PENDENTE";
        return ApiResponse.of(new PixDetailsResponse(pix.qrCode(), pix.copiaCola(), status, pix.expiracao()));
    }
}
