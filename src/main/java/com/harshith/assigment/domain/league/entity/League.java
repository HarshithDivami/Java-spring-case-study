package com.harshith.assigment.domain.league.entity;

import com.harshith.assigment.common.audit.AuditableEntity;
import com.harshith.assigment.common.enums.SportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "leagues",
        indexes = @Index(name = "idx_leagues_name", columnList = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class League extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false, length = 50)
    @Builder.Default
    private SportType sportType = SportType.CRICKET;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LeagueSeason> seasons = new ArrayList<>();
}
