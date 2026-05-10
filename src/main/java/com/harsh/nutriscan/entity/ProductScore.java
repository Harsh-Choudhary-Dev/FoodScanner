package com.harsh.nutriscan.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_scores")
public class ProductScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true
    )
    @JsonIgnore
    private Product product;

    @Column(name = "ingredient_score")
    private BigDecimal ingredientScore;

    @Column(name = "nutrition_score")
    private BigDecimal nutritionScore;

    @Column(name = "additive_score")
    private BigDecimal additiveScore;

    @Column(name = "processing_score")
    private BigDecimal processingScore;

    @Column(name = "allergen_score")
    private BigDecimal allergenScore;

    @Column(name = "overall_base_score")
    private BigDecimal overallBaseScore;

    @Column(name = "confidence_score")
    private BigDecimal confidenceScore;

    @Column(name = "score_version")
    private String scoreVersion;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
