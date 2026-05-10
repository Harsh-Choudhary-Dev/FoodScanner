package com.harsh.nutriscan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    @JsonIgnore
    private Product product;

    @Column(
            name = "image_url",
            columnDefinition = "TEXT",
            nullable = false
    )
    private String imageUrl;

    @Column(name = "image_type")
    private String imageType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
