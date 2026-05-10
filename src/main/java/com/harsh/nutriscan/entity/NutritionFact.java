package com.harsh.nutriscan.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "product")
@Entity
@Table(name = "nutrition_facts")
public class NutritionFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @JsonIgnore
    private Product product;
    private String serving_size;
    private BigDecimal energy_kcal;
    private BigDecimal protein_g;
    private BigDecimal carbohydrates_g;
    private BigDecimal sugars_g;
    private BigDecimal added_sugars_g;
    private BigDecimal fat_g;
    private BigDecimal saturated_fat_g;
    private BigDecimal trans_fat_g;
    private BigDecimal monounsaturated_fat_g;
    private BigDecimal polyunsaturated_fat_g;
    private BigDecimal fiber_g;
    private BigDecimal sodium_mg;
    private BigDecimal cholesterol_mg;
    private BigDecimal calcium_mg;
    private BigDecimal iron_mg;
    private BigDecimal potassium_mg;
    private BigDecimal vitamin_a;
    private BigDecimal vitamin_c;
    private BigDecimal vitamin_d;
    private BigDecimal vitamin_b12;
    private BigDecimal caffeine_mg;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
