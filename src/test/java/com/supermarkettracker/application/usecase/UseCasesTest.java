package com.supermarkettracker.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.supermarkettracker.application.command.*;
import com.supermarkettracker.application.query.*;
import com.supermarkettracker.domain.exception.*;
import com.supermarkettracker.domain.gateway.ProcessadorPagamentoGateway;
import com.supermarkettracker.domain.model.*;
import com.supermarkettracker.domain.model.enums.*;
import com.supermarkettracker.domain.model.valueobject.*;
import com.supermarkettracker.domain.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UseCasesTest {
    private static final UUID EMPRESA = UUID.randomUUID(), LOJA = UUID.randomUUID(), USUARIO = UUID.randomUUID();
    private static final UUID PRODUTO = UUID.randomUUID(), VENDA = UUID.randomUUID(), CAIXA = UUID.randomUUID();
    private static final BigDecimal DEZ = new BigDecimal("10");

    @Test void cadastrarEmpresa_salvaEDevolveDto() {
        EmpresaRepository repo = mock(EmpresaRepository.class);
        when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        var result = new CadastrarEmpresaUseCase(repo).executar(new CadastrarEmpresaCommand("Razao", "Fantasia", "12.345.678/0001-90"));
        assertThat(result.nomeFantasia()).isEqualTo("Fantasia");
        verify(repo).salvar(argThat(e -> e.status() == StatusEmpresa.ATIVA));
    }

    @Test void cadastrarLoja_salvaEDevolveDto() {
        LojaRepository repo = mock(LojaRepository.class); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        var result = new CadastrarLojaUseCase(repo).executar(new CadastrarLojaCommand(EMPRESA, "01", "Centro", null, "88"));
        assertThat(result.codigo()).isEqualTo("01"); verify(repo).salvar(any());
    }

    @Test void listarLojas_mapeiaResultadoDoRepositorio() {
        LojaRepository repo = mock(LojaRepository.class); when(repo.listarPorEmpresa(any())).thenReturn(List.of(loja()));
        assertThat(new ListarLojasUseCase(repo).executar(new ListarLojasQuery(EMPRESA))).singleElement().extracting("nome").isEqualTo("Loja");
    }

    @Test void cadastrarProduto_salvaQuandoCodigoAindaNaoExiste() {
        ProdutoRepository repo = mock(ProdutoRepository.class); EmpresaRepository empresas = mock(EmpresaRepository.class); when(empresas.buscarPorId(any())).thenReturn(Optional.of(mock(Empresa.class))); when(repo.buscarPorCodigoBarras(any(), any())).thenReturn(Optional.empty()); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        var result = new CadastrarProdutoUseCase(repo, empresas).executar(produtoCommand());
        assertThat(result.nome()).isEqualTo("Produto"); verify(repo).salvar(argThat(p -> p.estoqueAtual().equals(Quantidade.zero()) && p.unidadeMedida().equals("UN")));
    }

    @Test void cadastrarProduto_rejeitaCodigoDuplicado() {
        ProdutoRepository repo = mock(ProdutoRepository.class); EmpresaRepository empresas = mock(EmpresaRepository.class); when(empresas.buscarPorId(any())).thenReturn(Optional.of(mock(Empresa.class))); when(repo.buscarPorCodigoBarras(any(), any())).thenReturn(Optional.of(produto()));
        assertThatThrownBy(() -> new CadastrarProdutoUseCase(repo, empresas).executar(produtoCommand())).isInstanceOf(ConflitoDeDominioException.class);
        verify(repo, never()).salvar(any());
    }

    @Test void cadastrarProduto_rejeitaEmpresaInexistenteAntesDeSalvar() {
        ProdutoRepository produtos = mock(ProdutoRepository.class); EmpresaRepository empresas = mock(EmpresaRepository.class);
        when(empresas.buscarPorId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new CadastrarProdutoUseCase(produtos, empresas).executar(produtoCommand()))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessage("Empresa nao encontrada");
        verify(produtos, never()).salvar(any());
    }

    @Test void buscarProduto_devolveDtoOuNaoEncontrado() {
        ProdutoRepository repo = mock(ProdutoRepository.class); when(repo.buscarPorId(any())).thenReturn(Optional.of(produto()));
        assertThat(new BuscarProdutoUseCase(repo).executar(new BuscarProdutoQuery(PRODUTO)).nome()).isEqualTo("Produto");
        when(repo.buscarPorId(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new BuscarProdutoUseCase(repo).executar(new BuscarProdutoQuery(PRODUTO))).isInstanceOf(EntidadeNaoEncontradaException.class);
    }

    @Test void cadastrarUsuario_verificaEmailAntesDeSalvar() {
        UsuarioRepository repo = mock(UsuarioRepository.class); when(repo.buscarPorEmail(any())).thenReturn(Optional.empty()); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new CadastrarUsuarioUseCase(repo).executar(new CadastrarUsuarioCommand(USUARIO, "Ana", "ANA@EXAMPLE.COM", null)).email().valor()).isEqualTo("ana@example.com");
        when(repo.buscarPorEmail(any())).thenReturn(Optional.of(usuario()));
        assertThatThrownBy(() -> new CadastrarUsuarioUseCase(repo).executar(new CadastrarUsuarioCommand(USUARIO, "Ana", "ana@example.com", null))).isInstanceOf(ConflitoDeDominioException.class);
    }

    @Test void cadastrarCliente_salvaDadosOpcionais() {
        ClienteRepository repo = mock(ClienteRepository.class); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        Cliente c = new CadastrarClienteUseCase(repo).executar(new CadastrarClienteCommand(EMPRESA, "Ana", null, null, null, null));
        assertThat(c.status()).isEqualTo(StatusAtivo.ATIVO); verify(repo).salvar(any());
    }

    @Test void cadastrarCaixa_validaESalvaAtivo() {
        CaixaRepository repo = mock(CaixaRepository.class); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new CadastrarCaixaUseCase(repo).executar(new CadastrarCaixaCommand(LOJA, "C1", "Caixa 1")).status()).isEqualTo(StatusAtivo.ATIVO);
        assertThatThrownBy(() -> new CadastrarCaixaUseCase(repo).executar(new CadastrarCaixaCommand(null, "C1", "Caixa"))).isInstanceOf(RegraDeDominioException.class);
    }

    @Test void abrirSessao_criaSessaoQuandoNaoExisteAberta() {
        CaixaRepository repo = mock(CaixaRepository.class); when(repo.buscarSessaoAberta(any())).thenReturn(Optional.empty()); when(repo.salvarSessao(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new AbrirSessaoCaixaUseCase(repo).executar(new AbrirSessaoCaixaCommand(CAIXA, USUARIO, DEZ, "inicio")).status()).isEqualTo(StatusSessaoCaixa.ABERTO);
        when(repo.buscarSessaoAberta(any())).thenReturn(Optional.of(sessaoAberta()));
        assertThatThrownBy(() -> new AbrirSessaoCaixaUseCase(repo).executar(new AbrirSessaoCaixaCommand(CAIXA, USUARIO, DEZ, null))).isInstanceOf(ConflitoDeDominioException.class);
    }

    @Test void fecharSessao_fechaSessaoAberta() {
        CaixaRepository repo = mock(CaixaRepository.class); when(repo.buscarSessaoAberta(any())).thenReturn(Optional.of(sessaoAberta())); when(repo.salvarSessao(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new FecharSessaoCaixaUseCase(repo).executar(new FecharSessaoCaixaCommand(CAIXA, USUARIO, DEZ, "fim")).status()).isEqualTo(StatusSessaoCaixa.FECHADO);
        when(repo.buscarSessaoAberta(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new FecharSessaoCaixaUseCase(repo).executar(new FecharSessaoCaixaCommand(CAIXA, USUARIO, DEZ, null))).isInstanceOf(EntidadeNaoEncontradaException.class);
    }

    @Test void iniciarVenda_calculaTotal() {
        VendaRepository repo = mock(VendaRepository.class); when(repo.salvar(any())).thenAnswer(i -> i.getArgument(0));
        var dto = new IniciarVendaUseCase(repo).executar(new IniciarVendaCommand(EMPRESA, LOJA, null, USUARIO, null, 1, DEZ, BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.ONE, null));
        assertThat(dto.total()).isEqualByComparingTo("11.00");
    }

    @Test void adicionarItemVenda_calculaSubtotalECusto() {
        VendaRepository repo = mock(VendaRepository.class); when(repo.salvarItem(any())).thenAnswer(i -> i.getArgument(0));
        ItemVenda item = new AdicionarItemVendaUseCase(repo).executar(new AdicionarItemVendaCommand(VENDA, PRODUTO, 1, "P", null, "UN", BigDecimal.valueOf(2), DEZ, BigDecimal.valueOf(4), BigDecimal.ONE, BigDecimal.valueOf(2)));
        assertThat(item.subtotal().valor()).isEqualByComparingTo("21"); assertThat(item.custoTotal().valor()).isEqualByComparingTo("8");
    }

    @Test void buscarVenda_devolveDtoOuNaoEncontrado() {
        VendaRepository repo = mock(VendaRepository.class); when(repo.buscarPorId(any())).thenReturn(Optional.of(venda(StatusVenda.ABERTA)));
        assertThat(new BuscarVendaUseCase(repo).executar(new BuscarVendaQuery(VENDA)).numero()).isEqualTo(1);
        when(repo.buscarPorId(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new BuscarVendaUseCase(repo).executar(new BuscarVendaQuery(VENDA))).isInstanceOf(EntidadeNaoEncontradaException.class);
    }

    @Test void consultarDashboard_aplicaLimitePadraoEValidaFaixa() {
        DashboardConsultaRepository repo = mock(DashboardConsultaRepository.class); Dashboard dashboard = mock(Dashboard.class); when(repo.consultar(any(), any(), anyInt())).thenReturn(dashboard);
        assertThat(new ConsultarDashboardUseCase(repo).executar(new ConsultarDashboardQuery(EMPRESA, null, 0))).isSameAs(dashboard);
        verify(repo).consultar(any(), isNull(), eq(10));
        assertThatThrownBy(() -> new ConsultarDashboardUseCase(repo).executar(new ConsultarDashboardQuery(EMPRESA, null, 101))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void registrarMovimentacao_baixaEstoqueEImpedeSaldoNegativo() {
        ProdutoRepository produtos = mock(ProdutoRepository.class); MovimentacaoEstoqueRepository movs = mock(MovimentacaoEstoqueRepository.class); when(produtos.buscarPorId(any())).thenReturn(Optional.of(produto())); when(movs.salvar(any())).thenAnswer(i -> i.getArgument(0));
        MovimentacaoEstoque mov = new RegistrarMovimentacaoEstoqueUseCase(produtos, movs).executar(new RegistrarMovimentacaoEstoqueCommand(EMPRESA, LOJA, PRODUTO, USUARIO, null, null, TipoMovimentacaoEstoque.SAIDA, BigDecimal.valueOf(3), null, null, null));
        assertThat(mov.estoquePosterior().valor()).isEqualByComparingTo("7");
        assertThatThrownBy(() -> new RegistrarMovimentacaoEstoqueUseCase(produtos, movs).executar(new RegistrarMovimentacaoEstoqueCommand(EMPRESA, LOJA, PRODUTO, USUARIO, null, null, TipoMovimentacaoEstoque.SAIDA, BigDecimal.valueOf(11), null, null, null))).isInstanceOf(RegraDeDominioException.class);
    }

    @Test void registrarPagamento_processaPixESalvaPagamento() {
        PagamentoRepository pagamentos = mock(PagamentoRepository.class); VendaRepository vendas = mock(VendaRepository.class); ProcessadorPagamentoGateway gateway = mock(ProcessadorPagamentoGateway.class);
        when(vendas.buscarPorId(any())).thenReturn(Optional.of(venda(StatusVenda.ABERTA))); when(gateway.processarPix(any(), any())).thenReturn(new ProcessadorPagamentoGateway.ResultadoPagamento("pix-1", "APROVADO", null)); when(pagamentos.salvar(any())).thenAnswer(i -> i.getArgument(0));
        assertThat(new RegistrarPagamentoUseCase(pagamentos, vendas, gateway).executar(new RegistrarPagamentoCommand(VENDA, null, TipoPagamento.PIX, DEZ, null, (short) 1, null)).referencia()).isEqualTo("pix-1");
        verify(gateway).processarPix(any(), any());
    }

    private static CadastrarProdutoCommand produtoCommand() { return new CadastrarProdutoCommand(EMPRESA, null, "789", null, "Produto", null, null, DEZ, BigDecimal.valueOf(15), BigDecimal.ONE, false); }
    private static Produto produto() { Instant agora = Instant.now(); return new Produto(new Identificador(PRODUTO), new Identificador(EMPRESA), null, "789", null, "Produto", null, null, "UN", new Dinheiro(DEZ), new Dinheiro(DEZ), new Quantidade(DEZ), Quantidade.zero(), false, Produto.StatusProduto.ATIVO, agora, agora); }
    private static Loja loja() { Instant agora = Instant.now(); return new Loja(Identificador.novo(), new Identificador(EMPRESA), "1", "Loja", null, null, null, StatusAtivo.ATIVO, agora, agora); }
    private static Usuario usuario() { Instant agora = Instant.now(); return new Usuario(Identificador.novo(), new Identificador(USUARIO), "Ana", new Email("ana@example.com"), null, StatusAtivo.ATIVO, agora, agora); }
    private static SessaoCaixa sessaoAberta() { return new SessaoCaixa(Identificador.novo(), new Identificador(CAIXA), new Identificador(USUARIO), null, Instant.now(), null, new Dinheiro(DEZ), null, StatusSessaoCaixa.ABERTO, null); }
    private static Venda venda(StatusVenda status) { Instant agora = Instant.now(); return new Venda(new Identificador(VENDA), new Identificador(EMPRESA), new Identificador(LOJA), null, new Identificador(USUARIO), null, 1, new Dinheiro(DEZ), Dinheiro.zero(), Dinheiro.zero(), new Dinheiro(DEZ), Dinheiro.zero(), Dinheiro.zero(), Dinheiro.zero(), new Quantidade(BigDecimal.ONE), status, null, null, null, null, agora, agora); }
}
