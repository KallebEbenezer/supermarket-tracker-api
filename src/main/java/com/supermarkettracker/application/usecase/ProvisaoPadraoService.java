package com.supermarkettracker.application.usecase;

import com.supermarkettracker.domain.model.Empresa;
import com.supermarkettracker.domain.model.EmpresaUsuario;
import com.supermarkettracker.domain.model.Loja;
import com.supermarkettracker.domain.model.Usuario;
import com.supermarkettracker.domain.model.enums.PapelUsuario;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.enums.StatusEmpresa;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.EmpresaRepository;
import com.supermarkettracker.domain.repository.EmpresaUsuarioRepository;
import com.supermarkettracker.domain.repository.LojaRepository;
import java.time.Instant;
import java.util.List;

/**
 * Garante que um usuário possua uma empresa + loja padrão.
 *
 * <p>Após o registro (ou login) o app precisa de {@code empresaId} e {@code lojaId} para
 * funcionar (todas as rotas são multi-tenant). Para eliminar a etapa manual de criar
 * empresa (CNPJ) e loja (código), este serviço provisiona uma empresa + loja padrão
 * automaticamente caso o usuário ainda não tenha nenhuma.
 */
public final class ProvisaoPadraoService {
    private static final String NOME_PADRAO = "Minha Empresa";
    private static final String LOJA_PADRAO = "Loja Principal";

    private final EmpresaRepository empresas;
    private final EmpresaUsuarioRepository empresaUsuarios;
    private final LojaRepository lojas;

    public ProvisaoPadraoService(EmpresaRepository empresas, EmpresaUsuarioRepository empresaUsuarios,
                                 LojaRepository lojas) {
        this.empresas = empresas;
        this.empresaUsuarios = empresaUsuarios;
        this.lojas = lojas;
    }

    /** Retorna os vínculos ativos de empresa do usuário, mais recente primeiro. */
    public List<EmpresaUsuario> empresasDoUsuario(Usuario usuario) {
        return empresaUsuarios.buscarPorUsuario(usuario.id());
    }

    /**
     * Cria (se necessário) uma empresa + loja padrão para o usuário e devolve
     * o par {@code (empresaId, lojaId)}. Reutiliza a empresa mais recente do usuário
     * quando ele já possui vínculo.
     */
    public Provisionamento provisionar(Usuario usuario) {
        Instant agora = Instant.now();

        List<EmpresaUsuario> vinculos = empresaUsuarios.buscarPorUsuario(usuario.id());
        if (!vinculos.isEmpty()) {
            Identificador empresaId = vinculos.get(0).empresaId();
            Identificador lojaId = primeiraLoja(empresaId);
            return new Provisionamento(empresaId, lojaId);
        }

        Identificador empresaId = Identificador.novo();
        String nome = (usuario.nome() == null || usuario.nome().isBlank()) ? NOME_PADRAO : usuario.nome().trim();
        empresas.salvar(new Empresa(empresaId, nome, nome, null, StatusEmpresa.ATIVA, agora, agora));
        empresaUsuarios.salvar(new EmpresaUsuario(Identificador.novo(), empresaId, usuario.id(),
                PapelUsuario.PROPRIETARIO, StatusAtivo.ATIVO, agora, agora));

        Identificador lojaId = Identificador.novo();
        lojas.salvar(new Loja(lojaId, empresaId, codigoLoja(), LOJA_PADRAO, null, null, null,
                StatusAtivo.ATIVO, agora, agora));

        return new Provisionamento(empresaId, lojaId);
    }

    private Identificador primeiraLoja(Identificador empresaId) {
        List<Loja> lojasEmpresa = lojas.listarPorEmpresa(empresaId);
        if (lojasEmpresa.isEmpty()) {
            Instant agora = Instant.now();
            Identificador lojaId = Identificador.novo();
            lojas.salvar(new Loja(lojaId, empresaId, codigoLoja(), LOJA_PADRAO, null, null, null,
                    StatusAtivo.ATIVO, agora, agora));
            return lojaId;
        }
        return lojasEmpresa.get(0).id();
    }

    private static String codigoLoja() {
        return "LOJA-" + Identificador.novo().valor().toString().substring(0, 8).toUpperCase();
    }

    /** Resultado da provisão: ids da empresa e loja garantidas. */
    public record Provisionamento(Identificador empresaId, Identificador lojaId) {
    }
}
