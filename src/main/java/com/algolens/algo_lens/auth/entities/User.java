package com.algolens.algo_lens.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="codeforces_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

//    @NotBlank(message = "Username field can't be blank")
//    @Column(nullable = false, unique = true)
//    private String username;

    @NotBlank(message = "Name field can't be blank")
    private String name;

    @NotBlank(message = "Email field can't be blank")
    @Column(nullable = false,unique = true)
    @Email
    private String email;

    @NotBlank(message = "The password field can't be blank")
    @Size(min=5,message = "The password must have at least 5 characters")
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    @Column(nullable = false)
    private boolean enabled=true;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
