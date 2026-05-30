package com.project.ongojisik.domain.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.board.dto.BoardCreateRequest;
import com.project.ongojisik.domain.board.dto.BoardResponse;
import com.project.ongojisik.domain.board.dto.BoardUpdateRequest;
import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    private BoardService boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardService(boardRepository, userRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBoardCreatesBoardForCurrentUser() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board savedBoard = Board.create(user, "제목", "내용", "image.png");
        ReflectionTestUtils.setField(savedBoard, "boardId", 10L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList())
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        BoardResponse response = boardService.createBoard(new BoardCreateRequest("제목", "내용", "image.png"));

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("제목");
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void getBoardListReturnsPage() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        Page<Board> boards = new PageImpl<>(java.util.List.of(board), PageRequest.of(0, 10), 1);
        when(boardRepository.findAll(any(Pageable.class))).thenReturn(boards);

        Page<?> response = boardService.getBoardList(PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void searchBoardsByTitleReturnsMatchedBoards() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "김치찌개 맛집", "내용", null);
        ReflectionTestUtils.setField(board, "boardId", 11L);

        Page<Board> boards = new PageImpl<>(java.util.List.of(board), PageRequest.of(0, 10), 1);
        when(boardRepository.findByTitleContainingIgnoreCase("김치", PageRequest.of(0, 10))).thenReturn(boards);

        Page<?> response = boardService.searchBoardsByTitle("김치", PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void getBoardReturnsDetailWhenBoardExists() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        BoardResponse response = boardService.getBoard(10L);

        assertThat(response.boardId()).isEqualTo(10L);
    }

    @Test
    void updateBoardThrowsWhenCurrentUserIsNotOwner() {
        User owner = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(owner, "userId", 1L);
        User other = User.create("google-456", "other@gmail.com", "다른유저");
        ReflectionTestUtils.setField(other, "userId", 2L);
        Board board = Board.create(owner, "제목", "내용", null);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("2", null, Collections.emptyList())
        );
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.updateBoard(10L, new BoardUpdateRequest("수정", "수정내용", null)))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOARD_FORBIDDEN);
    }

    @Test
    void deleteBoardDeletesWhenCurrentUserIsOwner() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList())
        );
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        boardService.deleteBoard(10L);

        verify(boardRepository).delete(board);
    }
}
