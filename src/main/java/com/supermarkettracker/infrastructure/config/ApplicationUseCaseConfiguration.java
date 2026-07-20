package com.supermarkettracker.infrastructure.config;

import com.supermarkettracker.application.service.TokenService;
import com.supermarkettracker.application.usecase.*;
import com.supermarkettracker.domain.gateway.CartaoGateway;
import com.supermarkettracker.domain.gateway.PixGateway;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.repository.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class ApplicationUseCaseConfiguration {
    @Bean RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, TokenService tokenService) {
        return new RegistrarUsuarioUseCase(usuarios, credenciais, passwordEncoder, tokenService);
    }
    @Bean AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, TokenService tokenService) {
        return new AutenticarUsuarioUseCase(usuarios, credenciais, passwordEncoder, tokenService);
    }
    @Bean RenovarTokenUseCase renovarTokenUseCase(CredencialRepository credenciais, TokenService tokenService) {
        return new RenovarTokenUseCase(credenciais, tokenService);
    }
    @Bean BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase(UsuarioRepository usuarios, CredencialRepository credenciais) {
        return new BuscarUsuarioAtualUseCase(usuarios, credenciais);
    }
    @Bean SolicitarResetSenhaUseCase solicitarResetSenhaUseCase(CredencialRepository credenciais) {
        return new SolicitarResetSenhaUseCase(credenciais);
    }
    @Bean RedefinirSenhaUseCase redefinirSenhaUseCase(CredencialRepository credenciais, PasswordEncoder passwordEncoder) {
        return new RedefinirSenhaUseCase(credenciais, passwordEncoder);
    }
    @Bean CadastrarEmpresaUseCase cadastrarEmpresaUseCase(EmpresaRepository repository) { return new CadastrarEmpresaUseCase(repository); }
    @Bean CadastrarLojaUseCase cadastrarLojaUseCase(LojaRepository repository) { return new CadastrarLojaUseCase(repository); }
    @Bean ListarLojasUseCase listarLojasUseCase(LojaRepository repository) { return new ListarLojasUseCase(repository); }
    @Bean CadastrarProdutoUseCase cadastrarProdutoUseCase(ProdutoRepository produtos, EmpresaRepository empresas) { return new CadastrarProdutoUseCase(produtos, empresas); }
    @Bean BuscarProdutoUseCase buscarProdutoUseCase(ProdutoRepository repository) { return new BuscarProdutoUseCase(repository); }
    @Bean ListarProdutosUseCase listarProdutosUseCase(ProdutoRepository repository) { return new ListarProdutosUseCase(repository); }
    @Bean CadastrarUsuarioUseCase cadastrarUsuarioUseCase(UsuarioRepository repository) { return new CadastrarUsuarioUseCase(repository); }
    @Bean ListarUsuariosUseCase listarUsuariosUseCase(UsuarioRepository repository) { return new ListarUsuariosUseCase(repository); }
    @Bean CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepository repository) { return new CadastrarClienteUseCase(repository); }
    @Bean ListarClientesUseCase listarClientesUseCase(ClienteRepository repository) { return new ListarClientesUseCase(repository); }
    @Bean CadastrarCaixaUseCase cadastrarCaixaUseCase(CaixaRepository repository) { return new CadastrarCaixaUseCase(repository); }
    @Bean AbrirSessaoCaixaUseCase abrirSessaoCaixaUseCase(CaixaRepository repository) { return new AbrirSessaoCaixaUseCase(repository); }
    @Bean FecharSessaoCaixaUseCase fecharSessaoCaixaUseCase(CaixaRepository repository) { return new FecharSessaoCaixaUseCase(repository); }
    @Bean IniciarVendaUseCase iniciarVendaUseCase(VendaRepository repository) { return new IniciarVendaUseCase(repository); }
    @Bean BuscarVendaUseCase buscarVendaUseCase(VendaRepository repository) { return new BuscarVendaUseCase(repository); }
    @Bean ListarVendasUseCase listarVendasUseCase(VendaRepository repository) { return new ListarVendasUseCase(repository); }
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
    @Bean ListarMovimentacoesEstoqueUseCase listarMovimentacoesEstoqueUseCase(MovimentacaoEstoqueRepository movimentacoes) {
        return new ListarMovimentacoesEstoqueUseCase(movimentacoes);
    }
}
