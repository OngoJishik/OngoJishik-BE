package com.example.ongojisikbe.domain.analysis.entity;

import com.example.ongojisikbe.domain.bookmark.entity.Bookmark;
import com.example.ongojisikbe.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "분석")
public class Analysis {

    @Id
    @Column(name = "analysisId", nullable = false)
    private Long analysisId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bookmarkId", nullable = false)
    private Bookmark bookmark;

    @Column(name = "analysisPicture")
    private String analysisPicture;

    @Column(name = "category")
    private String category;

    @Column(name = "foodName")
    private String foodName;

    @Column(name = "foodContent")
    private String foodContent;

    @Column(name = "recipe")
    private String recipe;

    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "Field")
    private String field;
}
