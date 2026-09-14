package com.supermarkettracker.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sumup_oauth_state")
public class SumupOAuthStateEntity {
    @Id
    @Column(name = "state_hash", length = 64)
    public String stateHash;

    @Column(name = "expires_at", nullable = false)
    public Instant expiraEm;

    public SumupOAuthStateEntity() { }
}
