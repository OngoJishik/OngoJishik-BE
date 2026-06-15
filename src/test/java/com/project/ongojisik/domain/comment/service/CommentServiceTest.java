package com.project.ongojisik.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.comment.dto.CommentRequest;
import com.project.ongojisik.domain.comment.dto.CommentResponse;
import com.project.ongojisik.domain.comment.dto.MyCommentResponse;
import com.project.ongojisik.domain.comment.entity.Comment;
import com.project.ongojisik.domain.comment.repository.CommentRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, boardRepository, userRepository);
    }

    @Test
    void createCommentCreatesCommentForBoard() {
        User user = createUser(1L);
        Board board = createBoard(10L, user, "게시글 제목");
        Comment comment = createComment(100L, board, user, "댓글 내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse response = commentService.createComment(1L, 10L, new CommentRequest("댓글 내용"));

        assertThat(response.commentId()).isEqualTo(100L);
        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.authorId()).isEqualTo(1L);
        assertThat(response.commentContent()).isEqualTo("댓글 내용");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void getCommentListReturnsBoardComments() {
        User user = createUser(1L);
        Board board = createBoard(10L, user, "게시글 제목");
        Comment comment = createComment(100L, board, user, "댓글 내용");
        Page<Comment> comments = new PageImpl<>(java.util.List.of(comment), PageRequest.of(0, 10), 1);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(commentRepository.findByBoardBoardId(10L, PageRequest.of(0, 10))).thenReturn(comments);

        Page<CommentResponse> response = commentService.getCommentList(10L, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).commentId()).isEqualTo(100L);
    }

    @Test
    void getMyCommentListReturnsBoardTitle() {
        User user = createUser(1L);
        Board board = createBoard(10L, user, "게시글 제목");
        Comment comment = createComment(100L, board, user, "댓글 내용");
        Page<Comment> comments = new PageImpl<>(java.util.List.of(comment), PageRequest.of(0, 10), 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findByUserUserId(1L, PageRequest.of(0, 10))).thenReturn(comments);

        Page<MyCommentResponse> response = commentService.getMyCommentList(1L, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).boardId()).isEqualTo(10L);
        assertThat(response.getContent().get(0).boardTitle()).isEqualTo("게시글 제목");
    }

    @Test
    void updateCommentUpdatesWhenCurrentUserIsOwner() {
        User user = createUser(1L);
        Board board = createBoard(10L, user, "게시글 제목");
        Comment comment = createComment(100L, board, user, "댓글 내용");

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.updateComment(1L, 100L, new CommentRequest("수정 댓글"));

        assertThat(response.commentContent()).isEqualTo("수정 댓글");
    }

    @Test
    void updateCommentThrowsWhenCurrentUserIsNotOwner() {
        User owner = createUser(1L);
        User other = createUser(2L);
        Board board = createBoard(10L, owner, "게시글 제목");
        Comment comment = createComment(100L, board, owner, "댓글 내용");

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(2L, 100L, new CommentRequest("수정 댓글")))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COMMENT_FORBIDDEN);
    }

    @Test
    void deleteCommentDeletesWhenCurrentUserIsOwner() {
        User user = createUser(1L);
        Board board = createBoard(10L, user, "게시글 제목");
        Comment comment = createComment(100L, board, user, "댓글 내용");

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 100L);

        verify(commentRepository).delete(comment);
    }

    private User createUser(Long userId) {
        User user = User.create("google-" + userId, "user" + userId + "@gmail.com", "테스터" + userId);
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }

    private Board createBoard(Long boardId, User user, String title) {
        Board board = Board.create(user, title, "내용", java.util.List.of(), BoardCategory.REVIEW);
        Board board = Board.create(user, title, "내용", null);
        ReflectionTestUtils.setField(board, "boardId", boardId);
        return board;
    }

    private Comment createComment(Long commentId, Board board, User user, String content) {
        Comment comment = Comment.create(board, user, content);
        ReflectionTestUtils.setField(comment, "commentId", commentId);
        return comment;
    }
}
