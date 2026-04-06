package com.algolens.algo_lens.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="email_verification_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String token;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;


}
