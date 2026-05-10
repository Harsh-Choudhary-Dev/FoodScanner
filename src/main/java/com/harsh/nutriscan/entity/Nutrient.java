package com.harsh.nutriscan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(of = {"id", "name", "impactScore", "impactType"})
@Entity
@Table(name = "nutrients")

public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 50)
    private String unit;

    @Column(name = "impact_score", precision = 5, scale = 2)
    private BigDecimal impactScore;

    @Column(name = "impact_type", length = 50)
    private String impactType;

    @Column(columnDefinition = "TEXT")
    private String description;
}
