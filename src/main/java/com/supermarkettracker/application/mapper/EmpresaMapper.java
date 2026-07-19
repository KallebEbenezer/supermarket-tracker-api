package com.supermarkettracker.application.mapper;
import com.supermarkettracker.application.dto.EmpresaDto;
import com.supermarkettracker.domain.model.Empresa;
public final class EmpresaMapper { private EmpresaMapper() { } public static EmpresaDto paraDto(Empresa e) { return new EmpresaDto(e.id().valor(), e.razaoSocial(), e.nomeFantasia(), e.cnpj() == null ? null : e.cnpj().valor(), e.status().name()); } }
