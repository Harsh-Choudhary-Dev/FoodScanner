package com.harsh.nutriscan.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString(of = {"impactScore"})
@Table(name = "ingredients")
@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String category;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "impact_score")
    private BigDecimal impactScore;
}
