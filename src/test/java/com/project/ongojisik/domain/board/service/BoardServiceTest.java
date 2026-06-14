package com.project.ongojisik.domain.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.board.dto.BoardCreateRequest;
import com.project.ongojisik.domain.board.dto.BoardResponse;
import com.project.ongojisik.domain.board.dto.BoardSummaryResponse;
import com.project.ongojisik.domain.board.dto.BoardUpdateRequest;
import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.repository.BoardRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    void createBoardCreatesBoardForCurrentUser() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board savedBoard = Board.create(user, "제목", "내용", "image.png", BoardCategory.REVIEW);
        ReflectionTestUtils.setField(savedBoard, "boardId", 10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        BoardResponse response = boardService.createBoard(1L, new BoardCreateRequest("제목", "내용", "image.png", BoardCategory.REVIEW));

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.category()).isEqualTo(BoardCategory.REVIEW);
        assertThat(response.likeCount()).isZero();
        assertThat(response.commentCount()).isZero();
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void getBoardListReturnsPage() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null, BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        Page<BoardSummaryResponse> boards = new PageImpl<>(
                java.util.List.of(BoardSummaryResponse.from(board)),
                PageRequest.of(0, 10),
                1
        );
        when(boardRepository.findAllSummaryWithCounts(any(Pageable.class))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.getBoardList(null, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).likeCount()).isZero();
        assertThat(response.getContent().get(0).commentCount()).isZero();
    }

    @Test
    void getBoardListReturnsFilteredPageWhenCategoryExists() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null, BoardCategory.RECIPE);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        Page<BoardSummaryResponse> boards = new PageImpl<>(
                java.util.List.of(new BoardSummaryResponse(
                        10L,
                        "제목",
                        null,
                        BoardCategory.RECIPE,
                        2L,
                        3L,
                        1L,
                        "테스터",
                        board.getCreatedAt()
                )),
                PageRequest.of(0, 10),
                1
        );
        when(boardRepository.findSummaryByCategoryWithCounts(BoardCategory.RECIPE, PageRequest.of(0, 10))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.getBoardList(BoardCategory.RECIPE, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).likeCount()).isEqualTo(2L);
        assertThat(response.getContent().get(0).commentCount()).isEqualTo(3L);
    }

    @Test
    void searchBoardsByTitleReturnsMatchedBoards() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "김치찌개 맛집", "내용", null, BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", 11L);

        Page<BoardSummaryResponse> boards = new PageImpl<>(
                java.util.List.of(BoardSummaryResponse.from(board)),
                PageRequest.of(0, 10),
                1
        );
        when(boardRepository.findSummaryByTitleWithCounts("김치", PageRequest.of(0, 10))).thenReturn(boards);

        Page<?> response = boardService.searchBoardsByTitle("김치", null, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void searchBoardsByTitleReturnsMatchedBoardsInCategory() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "김치찌개 레시피", "내용", null, BoardCategory.RECIPE);
        ReflectionTestUtils.setField(board, "boardId", 11L);

        Page<BoardSummaryResponse> boards = new PageImpl<>(
                java.util.List.of(new BoardSummaryResponse(
                        11L,
                        "김치찌개 레시피",
                        null,
                        BoardCategory.RECIPE,
                        5L,
                        7L,
                        1L,
                        "테스터",
                        board.getCreatedAt()
                )),
                PageRequest.of(0, 10),
                1
        );
        when(boardRepository.findSummaryByTitleAndCategoryWithCounts("김치", BoardCategory.RECIPE, PageRequest.of(0, 10))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.searchBoardsByTitle("김치", BoardCategory.RECIPE, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).likeCount()).isEqualTo(5L);
        assertThat(response.getContent().get(0).commentCount()).isEqualTo(7L);
    }

    @Test
    void getBoardReturnsDetailWhenBoardExists() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null, BoardCategory.QNA);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        when(boardRepository.findResponseByIdWithCounts(10L)).thenReturn(Optional.of(new BoardResponse(
                10L,
                "제목",
                "내용",
                null,
                BoardCategory.QNA,
                4L,
                6L,
                1L,
                "테스터",
                board.getCreatedAt(),
                board.getUpdatedAt()
        )));

        BoardResponse response = boardService.getBoard(10L);

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.category()).isEqualTo(BoardCategory.QNA);
        assertThat(response.likeCount()).isEqualTo(4L);
        assertThat(response.commentCount()).isEqualTo(6L);
    }

    @Test
    void updateBoardThrowsWhenCurrentUserIsNotOwner() {
        User owner = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(owner, "userId", 1L);
        User other = User.create("google-456", "other@gmail.com", "다른유저");
        ReflectionTestUtils.setField(other, "userId", 2L);
        Board board = Board.create(owner, "제목", "내용", null, BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.updateBoard(2L, 10L, new BoardUpdateRequest("수정", "수정내용", null, BoardCategory.RECIPE)))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOARD_FORBIDDEN);
    }

    @Test
    void deleteBoardDeletesWhenCurrentUserIsOwner() {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", 1L);
        Board board = Board.create(user, "제목", "내용", null, BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", 10L);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        boardService.deleteBoard(1L, 10L);

        verify(boardRepository).delete(board);
    }
}
