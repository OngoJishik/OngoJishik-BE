package com.project.ongojisik.domain.search.repository;

import com.project.ongojisik.domain.search.entity.SearchHistory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("""
            select sh
            from SearchHistory sh
            where sh.user.userId = :userId
            order by sh.createdAt desc, sh.searchId desc
            """)
    Page<SearchHistory> findByUserUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            select sh
            from SearchHistory sh
            where sh.searchId = :searchId
              and sh.user.userId = :userId
            """)
    Optional<SearchHistory> findBySearchIdAndUserUserId(
            @Param("searchId") Long searchId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
            delete from SearchHistory sh
            where sh.user.userId = :userId
            """)
    void deleteByUserUserId(@Param("userId") Long userId);
}
