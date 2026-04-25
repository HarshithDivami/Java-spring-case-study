package com.harshith.assigment.domain.team.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams",
        indexes = @Index(name = "idx_teams_name", columnList = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "short_name", length = 10)
    private String shortName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "home_ground", length = 200)
    private String homeGround;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TeamPlayer> players = new ArrayList<>();
}
