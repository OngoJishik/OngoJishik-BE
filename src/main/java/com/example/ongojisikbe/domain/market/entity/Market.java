package com.example.ongojisikbe.domain.market.entity;

import com.example.ongojisikbe.domain.analysis.entity.Analysis;
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
@Table(name = "시장")
public class Market {

    @Id
    @Column(name = "marketId", nullable = false)
    private Long marketId;

    @ManyToOne
    @JoinColumn(name = "analysisId", nullable = false)
    private Analysis analysis;
}
