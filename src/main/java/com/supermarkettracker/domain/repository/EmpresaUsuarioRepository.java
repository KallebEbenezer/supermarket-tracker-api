package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.EmpresaUsuario;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.List;

public interface EmpresaUsuarioRepository {
    EmpresaUsuario salvar(EmpresaUsuario eu);

    /** Lista os vínculos empresa-usuário ativos de um usuário, mais recente primeiro. */
    List<EmpresaUsuario> buscarPorUsuario(Identificador usuarioId);
}
