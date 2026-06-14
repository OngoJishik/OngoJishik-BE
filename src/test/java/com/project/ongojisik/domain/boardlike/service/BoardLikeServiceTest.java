package com.project.ongojisik.domain.boardlike.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.boardlike.dto.BoardLikeResponse;
import com.project.ongojisik.domain.boardlike.entity.BoardLike;
import com.project.ongojisik.domain.boardlike.repository.BoardLikeRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BoardLikeServiceTest {

    @Mock
    private BoardLikeRepository boardLikeRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    private BoardLikeService boardLikeService;

    @BeforeEach
    void setUp() {
        boardLikeService = new BoardLikeService(boardLikeRepository, boardRepository, userRepository);
    }

    @Test
    void toggleBoardLikeCreatesLikeWhenNotExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(boardLikeRepository.findByUserUserIdAndBoardBoardId(1L, 10L)).thenReturn(Optional.empty());
        when(boardLikeRepository.countByBoardBoardId(10L)).thenReturn(1L);

        BoardLikeResponse response = boardLikeService.toggleBoardLike(1L, 10L);

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1L);
        verify(boardLikeRepository).save(any(BoardLike.class));
    }

    @Test
    void toggleBoardLikeDeletesLikeWhenExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);
        BoardLike boardLike = BoardLike.create(user, board);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(boardLikeRepository.findByUserUserIdAndBoardBoardId(1L, 10L)).thenReturn(Optional.of(boardLike));
        when(boardLikeRepository.countByBoardBoardId(10L)).thenReturn(0L);

        BoardLikeResponse response = boardLikeService.toggleBoardLike(1L, 10L);

        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0L);
        verify(boardLikeRepository).delete(boardLike);
    }

    @Test
    void getBoardLikeCountReturnsCountWhenBoardExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(boardLikeRepository.countByBoardBoardId(10L)).thenReturn(3L);

        Long count = boardLikeService.getBoardLikeCount(10L);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void getBoardLikeCountThrowsWhenBoardNotFound() {
        when(boardRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardLikeService.getBoardLikeCount(10L))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOARD_NOT_FOUND);
    }

    private User createUser(Long userId) {
        User user = User.create("google-" + userId, "user" + userId + "@gmail.com", "테스터" + userId);
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }

    private Board createBoard(Long boardId, User user) {
        Board board = Board.create(user, "제목", "내용", null, BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", boardId);
        return board;
    }
}
