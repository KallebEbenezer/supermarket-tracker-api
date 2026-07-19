-- Hibernate mapeia String para varchar. A conversão remove o preenchimento de CHAR
-- e mantém os CNPJs já existentes sem alterar o contrato de 14 dígitos.
alter table empresa alter column cnpj type varchar(14) using btrim(cnpj);
alter table loja alter column cnpj type varchar(14) using btrim(cnpj);
