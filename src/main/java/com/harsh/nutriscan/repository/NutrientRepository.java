package com.harsh.nutriscan.repository;

import com.harsh.nutriscan.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutrientRepository extends JpaRepository<Nutrient,Long> {
    List<Nutrient> findByNameIn(List<String> keys);
}
