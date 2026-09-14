create table sumup_oauth_credential (
    id smallint primary key check (id = 1),
    refresh_token_criptografado text not null,
    updated_at timestamptz not null default now()
);

create table sumup_oauth_state (
    state_hash varchar(64) primary key,
    expires_at timestamptz not null
);

create index ix_sumup_oauth_state_expira on sumup_oauth_state (expires_at);
