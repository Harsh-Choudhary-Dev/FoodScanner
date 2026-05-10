package com.harsh.nutriscan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Table(name = "products")
@Setter
@Getter
@ToString(of = {"id", "productName", "ingredientsText","nutritionFact"})
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name", nullable = false)
    private String productName;
    private String brand;
    private String category;
    private String barcode;
    private String source;
    @Column(name = "ingredients_text", columnDefinition = "LONGTEXT")
    private String ingredientsText;
    @Column(name = "product_url", columnDefinition = "TEXT")
    private String productUrl;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> productImage;
    @OneToOne(mappedBy = "product")
    private NutritionFact nutritionFact;
    private BigDecimal price;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToMany
    @JoinTable(name = "product_ingredients", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients;
}
