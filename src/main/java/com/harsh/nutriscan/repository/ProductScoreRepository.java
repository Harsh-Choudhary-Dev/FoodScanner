package com.harsh.nutriscan.repository;


import com.harsh.nutriscan.entity.ProductScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductScoreRepository extends JpaRepository<ProductScore,Long> {

    Optional<ProductScore> findByProductId(Long productId);
}
