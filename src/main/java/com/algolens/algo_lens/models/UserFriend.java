package com.algolens.algo_lens.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name="friends",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userHandle","friendHandle"})}
)
@Getter
@Setter
@NoArgsConstructor
public class UserFriend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_handle",nullable = false)
    private String userHandle;

    @Column(name = "friend_handle", nullable = false)
    private String friendHandle;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    public UserFriend(String userHandle, String friendHandle) {
        this.friendHandle = friendHandle;
        this.userHandle = userHandle;
    }
}

