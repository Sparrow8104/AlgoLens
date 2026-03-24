package com.algolens.algo_lens.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name="friends",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"userHandle","friendHandle"})}
)
@Getter
@Setter
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userHandle;

    @Column(nullable = false)
    private String friendHandle;

    public Friend() {}

    public Friend(String userHandle, String friendHandle) {
        this.friendHandle = friendHandle;
        this.userHandle = userHandle;
    }
}

