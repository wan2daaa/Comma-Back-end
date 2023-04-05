package com.team.comma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "T_REFRESH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID", nullable = false)
    private Long refreshTokenId;

    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;

    @Column(name = "KEY_EMAIL", nullable = false)
    private String keyEmail;

}