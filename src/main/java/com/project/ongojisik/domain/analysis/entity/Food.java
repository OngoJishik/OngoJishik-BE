package com.project.ongojisik.domain.analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "food")
public class Food {

    @Id
    @Column(name = "food_id", nullable = false, length = 20)
    private String foodId;

    @Column(name = "category")
    private String category;

    @Column(name = "food_picture")
    private String foodPicture;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "food_feature")
    private String foodFeatures;

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "recipe", columnDefinition = "TEXT")
    private String recipe;

    @Column(name = "history", columnDefinition = "TEXT")
    private String history;

    @Column(name = "doc_nm")
    private String docName;

    @Column(name = "author")
    private String author;

    @Column(name = "published_year")
    private String publishedYear;

    @Column(name = "trans_txt", columnDefinition = "TEXT")
    private String transTxt;

    @Column(name = "org_food_url", columnDefinition = "TEXT")
    private String orgFoodUrl;
}
