package com.supermarkettracker.infrastructure.config;

import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfiguration {
    @Bean CadastrarEmpresaUseCase cadastrarEmpresaUseCase(EmpresaRepository repository) { return new CadastrarEmpresaUseCase(repository); }
    @Bean CadastrarLojaUseCase cadastrarLojaUseCase(LojaRepository repository) { return new CadastrarLojaUseCase(repository); }
    @Bean ListarLojasUseCase listarLojasUseCase(LojaRepository repository) { return new ListarLojasUseCase(repository); }
    @Bean CadastrarProdutoUseCase cadastrarProdutoUseCase(ProdutoRepository produtos, EmpresaRepository empresas) { return new CadastrarProdutoUseCase(produtos, empresas); }
    @Bean BuscarProdutoUseCase buscarProdutoUseCase(ProdutoRepository repository) { return new BuscarProdutoUseCase(repository); }
    @Bean CadastrarUsuarioUseCase cadastrarUsuarioUseCase(UsuarioRepository repository) { return new CadastrarUsuarioUseCase(repository); }
    @Bean CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepository repository) { return new CadastrarClienteUseCase(repository); }
    @Bean CadastrarCaixaUseCase cadastrarCaixaUseCase(CaixaRepository repository) { return new CadastrarCaixaUseCase(repository); }
    @Bean AbrirSessaoCaixaUseCase abrirSessaoCaixaUseCase(CaixaRepository repository) { return new AbrirSessaoCaixaUseCase(repository); }
    @Bean FecharSessaoCaixaUseCase fecharSessaoCaixaUseCase(CaixaRepository repository) { return new FecharSessaoCaixaUseCase(repository); }
    @Bean IniciarVendaUseCase iniciarVendaUseCase(VendaRepository repository) { return new IniciarVendaUseCase(repository); }
    @Bean BuscarVendaUseCase buscarVendaUseCase(VendaRepository repository) { return new BuscarVendaUseCase(repository); }
    @Bean ConsultarDashboardUseCase consultarDashboardUseCase(DashboardConsultaRepository repository) { return new ConsultarDashboardUseCase(repository); }
    @Bean AdicionarItemVendaUseCase adicionarItemVendaUseCase(VendaRepository vendas) { return new AdicionarItemVendaUseCase(vendas); }
    @Bean RegistrarPagamentoUseCase registrarPagamentoUseCase(PagamentoRepository pagamentos, VendaRepository vendas,
            ProcessadorPagamentoGateway processador) { return new RegistrarPagamentoUseCase(pagamentos, vendas, processador); }
    @Bean FinalizarVendaUseCase finalizarVendaUseCase(VendaRepository vendas, ProdutoRepository produtos,
            PagamentoRepository pagamentos, MovimentacaoEstoqueRepository movimentacoes, DashboardRepository dashboard,
            PixGateway pixGateway, CartaoGateway cartaoGateway) { return new FinalizarVendaUseCase(vendas, produtos,
                    pagamentos, movimentacoes, dashboard, pixGateway, cartaoGateway); }
    @Bean RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase(ProdutoRepository produtos,
            MovimentacaoEstoqueRepository movimentacoes) { return new RegistrarMovimentacaoEstoqueUseCase(produtos, movimentacoes); }
}
