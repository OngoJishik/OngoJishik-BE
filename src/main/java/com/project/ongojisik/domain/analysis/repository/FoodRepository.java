package com.project.ongojisik.domain.analysis.repository;

import com.project.ongojisik.domain.analysis.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, String> {
}
