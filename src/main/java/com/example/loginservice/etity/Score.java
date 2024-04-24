package com.example.loginservice.etity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "score")
@AllArgsConstructor
@NoArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id", nullable = false)
    private Long scoreId;

    @Column(name = "score")
    private Float score;

    @Column(name = "grant_id", nullable = false)
    private String grantId;

    @Column(name = "granted_id", nullable = false)
    private String grantedId;

    @ManyToOne
    @JoinColumn(name = "grant_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User grantUser;

    @ManyToOne
    @JoinColumn(name = "granted_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User grantedUser;

}
