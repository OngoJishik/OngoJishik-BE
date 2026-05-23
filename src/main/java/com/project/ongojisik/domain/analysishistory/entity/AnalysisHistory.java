package com.project.ongojisik.domain.analysishistory.entity;

import com.project.ongojisik.domain.analysis.entity.Analysis;
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
@Table(name = "Untitled5")
public class AnalysisHistory {

    @Id
    @Column(name = "historyId", nullable = false)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "anylisisId", nullable = false)
    private Analysis analysis;

    @Column(name = "hisotryContent")
    private String historyContent;

    @Column(name = "originalContent")
    private String originalContent;

    @Column(name = "Field")
    private String field;
}
