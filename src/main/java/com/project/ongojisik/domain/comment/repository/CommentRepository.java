package com.project.ongojisik.domain.comment.repository;

import com.project.ongojisik.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select c
            from Comment c
            where c.board.boardId = :boardId
            order by c.createdAt desc, c.commentId desc
            """)
    Page<Comment> findByBoardBoardId(
            @Param("boardId") Long boardId,
            Pageable pageable
    );

    @Query(
            value = """
                    select c
                    from Comment c
                    join fetch c.board
                    where c.user.userId = :userId
                    order by c.createdAt desc, c.commentId desc
                    """,
            countQuery = """
                    select count(c)
                    from Comment c
                    where c.user.userId = :userId
                    """
    )
    Page<Comment> findByUserUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
