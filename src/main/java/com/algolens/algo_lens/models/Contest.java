package com.algolens.algo_lens.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "upcoming_contests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer codeforcesId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long startTimeSeconds;

    private String type;

    private String phase;
}
