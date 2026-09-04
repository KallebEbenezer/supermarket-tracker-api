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
    @Bean ProvisaoPadraoService provisaoPadraoService(EmpresaRepository empresas, EmpresaUsuarioRepository empresaUsuarios,
            LojaRepository lojas) {
        return new ProvisaoPadraoService(empresas, empresaUsuarios, lojas);
    }
    @Bean RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, TokenService tokenService, ProvisaoPadraoService provisaoPadrao) {
        return new RegistrarUsuarioUseCase(usuarios, credenciais, passwordEncoder, tokenService, provisaoPadrao);
    }
    @Bean AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepository usuarios, CredencialRepository credenciais,
            PasswordEncoder passwordEncoder, TokenService tokenService, ProvisaoPadraoService provisaoPadrao) {
        return new AutenticarUsuarioUseCase(usuarios, credenciais, passwordEncoder, tokenService, provisaoPadrao);
    }
    @Bean RenovarTokenUseCase renovarTokenUseCase(CredencialRepository credenciais, TokenService tokenService) {
        return new RenovarTokenUseCase(credenciais, tokenService);
    }
    @Bean BuscarUsuarioAtualUseCase buscarUsuarioAtualUseCase(UsuarioRepository usuarios, CredencialRepository credenciais) {
        return new BuscarUsuarioAtualUseCase(usuarios, credenciais);
    }
    @Bean SolicitarResetSenhaUseCase solicitarResetSenhaUseCase(CredencialRepository credenciais,
            com.supermarkettracker.domain.gateway.EmailGateway emailGateway) {
        return new SolicitarResetSenhaUseCase(credenciais, emailGateway);
    }
    @Bean RedefinirSenhaUseCase redefinirSenhaUseCase(CredencialRepository credenciais, PasswordEncoder passwordEncoder) {
        return new RedefinirSenhaUseCase(credenciais, passwordEncoder);
    }
    @Bean CadastrarEmpresaUseCase cadastrarEmpresaUseCase(EmpresaRepository repository, EmpresaUsuarioRepository euRepository) { return new CadastrarEmpresaUseCase(repository, euRepository); }
    @Bean ListarEmpresasUseCase listarEmpresasUseCase(EmpresaRepository repository) { return new ListarEmpresasUseCase(repository); }
    @Bean CadastrarLojaUseCase cadastrarLojaUseCase(LojaRepository repository) { return new CadastrarLojaUseCase(repository); }
    @Bean ListarLojasUseCase listarLojasUseCase(LojaRepository repository) { return new ListarLojasUseCase(repository); }
    @Bean BuscarLojaUseCase buscarLojaUseCase(LojaRepository repository) { return new BuscarLojaUseCase(repository); }
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
    @Bean ListarCaixasUseCase listarCaixasUseCase(CaixaRepository repository) { return new ListarCaixasUseCase(repository); }
    @Bean BuscarSessaoAtualUseCase buscarSessaoAtualUseCase(CaixaRepository repository) { return new BuscarSessaoAtualUseCase(repository); }
    @Bean IniciarVendaUseCase iniciarVendaUseCase(VendaRepository repository) { return new IniciarVendaUseCase(repository); }
    @Bean BuscarVendaUseCase buscarVendaUseCase(VendaRepository repository) { return new BuscarVendaUseCase(repository); }
    @Bean ListarVendasUseCase listarVendasUseCase(VendaRepository repository) { return new ListarVendasUseCase(repository); }
    @Bean ConsultarDashboardUseCase consultarDashboardUseCase(DashboardConsultaRepository repository) { return new ConsultarDashboardUseCase(repository); }
    @Bean AdicionarItemVendaUseCase adicionarItemVendaUseCase(VendaRepository vendas) { return new AdicionarItemVendaUseCase(vendas); }
    @Bean RegistrarPagamentoUseCase registrarPagamentoUseCase(PagamentoRepository pagamentos, VendaRepository vendas,
            ProcessadorPagamentoGateway processador) { return new RegistrarPagamentoUseCase(pagamentos, vendas, processador); }
    @Bean FinalizarVendaUseCase finalizarVendaUseCase(VendaRepository vendas, ProdutoRepository produtos,
            PagamentoRepository pagamentos, MovimentacaoEstoqueRepository movimentacoes, DashboardRepository dashboard,
            ContaBancariaRepository contasBancarias,
            PixGateway pixGateway, CartaoGateway cartaoGateway) { return new FinalizarVendaUseCase(vendas, produtos,
                    pagamentos, movimentacoes, dashboard, contasBancarias, pixGateway, cartaoGateway); }
    @Bean RegistrarMovimentacaoEstoqueUseCase registrarMovimentacaoEstoqueUseCase(ProdutoRepository produtos,
            MovimentacaoEstoqueRepository movimentacoes) { return new RegistrarMovimentacaoEstoqueUseCase(produtos, movimentacoes); }
    @Bean ListarMovimentacoesEstoqueUseCase listarMovimentacoesEstoqueUseCase(MovimentacaoEstoqueRepository movimentacoes) {
        return new ListarMovimentacoesEstoqueUseCase(movimentacoes);
    }
    @Bean ExcluirLojaUseCase excluirLojaUseCase(LojaRepository repository) { return new ExcluirLojaUseCase(repository); }
    @Bean ExcluirProdutoUseCase excluirProdutoUseCase(ProdutoRepository repository) { return new ExcluirProdutoUseCase(repository); }
    @Bean ExcluirClienteUseCase excluirClienteUseCase(ClienteRepository repository) { return new ExcluirClienteUseCase(repository); }
    @Bean ExcluirUsuarioUseCase excluirUsuarioUseCase(UsuarioRepository repository) { return new ExcluirUsuarioUseCase(repository); }
    @Bean ExcluirCaixaUseCase excluirCaixaUseCase(CaixaRepository repository) { return new ExcluirCaixaUseCase(repository); }
    @Bean CadastrarContaBancariaUseCase cadastrarContaBancariaUseCase(ContaBancariaRepository repository) { return new CadastrarContaBancariaUseCase(repository); }
    @Bean ListarContasBancariasUseCase listarContasBancariasUseCase(ContaBancariaRepository repository) { return new ListarContasBancariasUseCase(repository); }
    @Bean BuscarContaBancariaUseCase buscarContaBancariaUseCase(ContaBancariaRepository repository) { return new BuscarContaBancariaUseCase(repository); }
    @Bean ExcluirContaBancariaUseCase excluirContaBancariaUseCase(ContaBancariaRepository repository) { return new ExcluirContaBancariaUseCase(repository); }
}
