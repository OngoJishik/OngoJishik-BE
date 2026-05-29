package com.project.ongojisik.domain.analysis.entity;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import com.project.ongojisik.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "analyses")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bookmark_id", nullable = false)
    private Bookmark bookmark;

    @Column(name = "analysis_picture")
    private String analysisPicture;

    @Column(name = "category")
    private String category;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "food_content")
    private String foodContent;

    @Column(name = "recipe")
    private String recipe;

    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "memo")
    private String memo;
}
