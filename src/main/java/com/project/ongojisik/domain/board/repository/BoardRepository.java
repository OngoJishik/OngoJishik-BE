package com.project.ongojisik.domain.board.repository;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.dto.BoardResponse;
import com.project.ongojisik.domain.board.dto.BoardSummaryResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(
            value = """
                    select new com.project.ongojisik.domain.board.dto.BoardSummaryResponse(
                        b.boardId,
                        b.title,
                        b.imageUrls,
                        b.category,
                        (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                        (select count(c) from Comment c where c.board.boardId = b.boardId),
                        exists (
                            select 1
                            from BoardLike liked
                            where liked.board.boardId = b.boardId
                              and liked.user.userId = :userId
                        ),
                        b.user.userId,
                        b.user.nickname,
                        b.createdAt
                    )
                    from Board b
            """,
            countQuery = "select count(b) from Board b"
    )
    Page<BoardSummaryResponse> findAllSummaryWithCounts(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.project.ongojisik.domain.board.dto.BoardSummaryResponse(
                        b.boardId,
                        b.title,
                        b.imageUrls,
                        b.category,
                        (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                        (select count(c) from Comment c where c.board.boardId = b.boardId),
                        exists (
                            select 1
                            from BoardLike liked
                            where liked.board.boardId = b.boardId
                              and liked.user.userId = :userId
                        ),
                        b.user.userId,
                        b.user.nickname,
                        b.createdAt
                    )
                    from Board b
                    where b.category = :category
                    """,
            countQuery = "select count(b) from Board b where b.category = :category"
    )
    Page<BoardSummaryResponse> findSummaryByCategoryWithCounts(
            @Param("userId") Long userId,
            @Param("category") BoardCategory category,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.project.ongojisik.domain.board.dto.BoardSummaryResponse(
                        b.boardId,
                        b.title,
                        b.imageUrls,
                        b.category,
                        (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                        (select count(c) from Comment c where c.board.boardId = b.boardId),
                        exists (
                            select 1
                            from BoardLike liked
                            where liked.board.boardId = b.boardId
                              and liked.user.userId = :userId
                        ),
                        b.user.userId,
                        b.user.nickname,
                        b.createdAt
                    )
                    from Board b
                    where lower(b.title) like lower(concat('%', :title, '%'))
                    """,
            countQuery = """
                    select count(b)
                    from Board b
                    where lower(b.title) like lower(concat('%', :title, '%'))
                    """
    )
    Page<BoardSummaryResponse> findSummaryByTitleWithCounts(
            @Param("userId") Long userId,
            @Param("title") String title,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.project.ongojisik.domain.board.dto.BoardSummaryResponse(
                        b.boardId,
                        b.title,
                        b.imageUrls,
                        b.category,
                        (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                        (select count(c) from Comment c where c.board.boardId = b.boardId),
                        exists (
                            select 1
                            from BoardLike liked
                            where liked.board.boardId = b.boardId
                              and liked.user.userId = :userId
                        ),
                        b.user.userId,
                        b.user.nickname,
                        b.createdAt
                    )
                    from Board b
                    where lower(b.title) like lower(concat('%', :title, '%'))
                      and b.category = :category
                    """,
            countQuery = """
                    select count(b)
                    from Board b
                    where lower(b.title) like lower(concat('%', :title, '%'))
                      and b.category = :category
                    """
    )
    Page<BoardSummaryResponse> findSummaryByTitleAndCategoryWithCounts(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("category") BoardCategory category,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.project.ongojisik.domain.board.dto.BoardSummaryResponse(
                        b.boardId,
                        b.title,
                        b.imageUrls,
                        b.category,
                        (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                        (select count(c) from Comment c where c.board.boardId = b.boardId),
                        exists (
                            select 1
                            from BoardLike liked
                            where liked.board.boardId = b.boardId
                              and liked.user.userId = :userId
                        ),
                        b.user.userId,
                        b.user.nickname,
                        b.createdAt
                    )
                    from Board b
                    where b.user.userId = :userId
                    """,
            countQuery = "select count(b) from Board b where b.user.userId = :userId"
    )
    Page<BoardSummaryResponse> findMySummaryWithCounts(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            select new com.project.ongojisik.domain.board.dto.BoardResponse(
                b.boardId,
                b.title,
                b.content,
                b.imageUrls,
                b.category,
                (select count(bl) from BoardLike bl where bl.board.boardId = b.boardId),
                (select count(c) from Comment c where c.board.boardId = b.boardId),
                exists (
                    select 1
                    from BoardLike liked
                    where liked.board.boardId = b.boardId
                      and liked.user.userId = :userId
                ),
                b.user.userId,
                b.user.nickname,
                b.createdAt,
                b.updatedAt
            )
            from Board b
            where b.boardId = :boardId
            """)
    Optional<BoardResponse> findResponseByIdWithCounts(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId
    );

}
