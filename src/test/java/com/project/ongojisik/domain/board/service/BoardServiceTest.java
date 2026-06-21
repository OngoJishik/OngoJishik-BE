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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        User user = createUser(1L, "테스터");
        Board savedBoard = Board.create(
                user,
                "제목",
                "내용",
                List.of("image.png"),
                BoardCategory.REVIEW,
                List.of("태그"),
                null
        );
        savedBoard.assignBoardId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        BoardResponse response = boardService.createBoard(
                1L,
                new BoardCreateRequest(
                        "제목",
                        "내용",
                        List.of("image.png"),
                        BoardCategory.REVIEW,
                        List.of("태그"),
                        null
                )
        );

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.category()).isEqualTo(BoardCategory.REVIEW);
        assertThat(response.imageUrls()).containsExactly("image.png");
        assertThat(response.likeCount()).isZero();
        assertThat(response.commentCount()).isZero();
        assertThat(response.isLiked()).isFalse();
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void createBoardStoresNumericRecipeId() {
        User user = createUser(1L, "테스터");
        ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
            Board board = invocation.getArgument(0);
            board.assignBoardId(10L);
            return board;
        });

        BoardResponse response = boardService.createBoard(
                1L,
                new BoardCreateRequest(
                        "제목",
                        "내용",
                        List.of(),
                        BoardCategory.RECIPE,
                        List.of(),
                        "123"
                )
        );

        verify(boardRepository).save(boardCaptor.capture());
        assertThat(boardCaptor.getValue().getRecipeId()).isEqualTo("123");
        assertThat(response.recipeId()).isEqualTo("123");
    }

    @Test
    void createBoardStoresCustomRecipeText() {
        User user = createUser(1L, "테스터");
        ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
            Board board = invocation.getArgument(0);
            board.assignBoardId(10L);
            return board;
        });

        BoardResponse response = boardService.createBoard(
                1L,
                new BoardCreateRequest(
                        "제목",
                        "내용",
                        List.of(),
                        BoardCategory.RECIPE,
                        List.of(),
                        " 김치찌개 "
                )
        );

        verify(boardRepository).save(boardCaptor.capture());
        assertThat(boardCaptor.getValue().getRecipeId()).isEqualTo("김치찌개");
        assertThat(response.recipeId()).isEqualTo("김치찌개");
    }

    @Test
    void createBoardStoresNullWhenRecipeIdIsBlank() {
        User user = createUser(1L, "테스터");
        ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
            Board board = invocation.getArgument(0);
            board.assignBoardId(10L);
            return board;
        });

        BoardResponse response = boardService.createBoard(
                1L,
                new BoardCreateRequest(
                        "제목",
                        "내용",
                        List.of(),
                        BoardCategory.RECIPE,
                        List.of(),
                        "   "
                )
        );

        verify(boardRepository).save(boardCaptor.capture());
        assertThat(boardCaptor.getValue().getRecipeId()).isNull();
        assertThat(response.recipeId()).isNull();
    }

    @Test
    void updateBoardStoresCustomRecipeText() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(10L, user, "제목", BoardCategory.REVIEW, "123");

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(boardRepository.findResponseByIdWithCounts(1L, 10L)).thenReturn(Optional.of(new BoardResponse(
                10L,
                "수정",
                "수정내용",
                List.of(),
                BoardCategory.RECIPE,
                List.of(),
                "된장찌개",
                0L,
                0L,
                false,
                1L,
                "테스터",
                board.getCreatedAt(),
                board.getUpdatedAt()
        )));

        BoardResponse response = boardService.updateBoard(
                1L,
                10L,
                new BoardUpdateRequest(
                        "수정",
                        "수정내용",
                        List.of(),
                        BoardCategory.RECIPE,
                        List.of(),
                        " 된장찌개 "
                )
        );

        assertThat(board.getRecipeId()).isEqualTo("된장찌개");
        assertThat(response.recipeId()).isEqualTo("된장찌개");
    }

    @Test
    void getBoardListReturnsPage() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(10L, user, "제목", BoardCategory.REVIEW, "김치찌개");
        Page<BoardSummaryResponse> boards = new PageImpl<>(
                List.of(BoardSummaryResponse.from(board)),
                PageRequest.of(0, 10),
                1
        );

        when(boardRepository.findAllSummaryWithCounts(1L, PageRequest.of(0, 10))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.getBoardList(1L, null, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).recipeId()).isEqualTo("김치찌개");
        assertThat(response.getContent().get(0).likeCount()).isZero();
        assertThat(response.getContent().get(0).commentCount()).isZero();
    }

    @Test
    void getBoardListReturnsFilteredPageWhenCategoryExists() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(10L, user, "제목", BoardCategory.RECIPE, "123");
        Page<BoardSummaryResponse> boards = new PageImpl<>(
                List.of(new BoardSummaryResponse(
                        10L,
                        "제목",
                        List.of(),
                        BoardCategory.RECIPE,
                        "123",
                        2L,
                        3L,
                        true,
                        1L,
                        "테스터",
                        board.getCreatedAt()
                )),
                PageRequest.of(0, 10),
                1
        );

        when(boardRepository.findSummaryByCategoryWithCounts(1L, BoardCategory.RECIPE, PageRequest.of(0, 10)))
                .thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.getBoardList(1L, BoardCategory.RECIPE, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).recipeId()).isEqualTo("123");
        assertThat(response.getContent().get(0).likeCount()).isEqualTo(2L);
        assertThat(response.getContent().get(0).commentCount()).isEqualTo(3L);
        assertThat(response.getContent().get(0).isLiked()).isTrue();
    }

    @Test
    void searchBoardsByTitleReturnsMatchedBoards() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(11L, user, "김치찌개 맛집", BoardCategory.REVIEW, "김치찌개");
        Page<BoardSummaryResponse> boards = new PageImpl<>(
                List.of(BoardSummaryResponse.from(board)),
                PageRequest.of(0, 10),
                1
        );

        when(boardRepository.findSummaryByTitleWithCounts(1L, "김치", PageRequest.of(0, 10))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.searchBoardsByTitle(1L, "김치", null, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).recipeId()).isEqualTo("김치찌개");
    }

    @Test
    void searchBoardsByTitleReturnsMatchedBoardsInCategory() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(11L, user, "김치찌개 레시피", BoardCategory.RECIPE, "123");
        Page<BoardSummaryResponse> boards = new PageImpl<>(
                List.of(new BoardSummaryResponse(
                        11L,
                        "김치찌개 레시피",
                        List.of(),
                        BoardCategory.RECIPE,
                        "123",
                        5L,
                        7L,
                        true,
                        1L,
                        "테스터",
                        board.getCreatedAt()
                )),
                PageRequest.of(0, 10),
                1
        );

        when(boardRepository.findSummaryByTitleAndCategoryWithCounts(1L, "김치", BoardCategory.RECIPE, PageRequest.of(0, 10)))
                .thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.searchBoardsByTitle(
                1L,
                "김치",
                BoardCategory.RECIPE,
                PageRequest.of(0, 10)
        );

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).recipeId()).isEqualTo("123");
        assertThat(response.getContent().get(0).likeCount()).isEqualTo(5L);
        assertThat(response.getContent().get(0).commentCount()).isEqualTo(7L);
        assertThat(response.getContent().get(0).isLiked()).isTrue();
    }

    @Test
    void getMyBoardListReturnsCurrentUserBoards() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(12L, user, "내 게시글", BoardCategory.REVIEW, "김치찌개");
        Page<BoardSummaryResponse> boards = new PageImpl<>(
                List.of(new BoardSummaryResponse(
                        12L,
                        "내 게시글",
                        List.of(),
                        BoardCategory.REVIEW,
                        "김치찌개",
                        1L,
                        2L,
                        false,
                        1L,
                        "테스터",
                        board.getCreatedAt()
                )),
                PageRequest.of(0, 10),
                1
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findMySummaryWithCounts(1L, PageRequest.of(0, 10))).thenReturn(boards);

        Page<BoardSummaryResponse> response = boardService.getMyBoardList(1L, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).authorId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).recipeId()).isEqualTo("김치찌개");
        assertThat(response.getContent().get(0).likeCount()).isEqualTo(1L);
        assertThat(response.getContent().get(0).commentCount()).isEqualTo(2L);
        assertThat(response.getContent().get(0).isLiked()).isFalse();
    }

    @Test
    void getBoardReturnsDetailWhenBoardExists() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(10L, user, "제목", BoardCategory.QNA, "123");

        when(boardRepository.findResponseByIdWithCounts(1L, 10L)).thenReturn(Optional.of(new BoardResponse(
                10L,
                "제목",
                "내용",
                List.of(),
                BoardCategory.QNA,
                List.of(),
                "123",
                4L,
                6L,
                true,
                1L,
                "테스터",
                board.getCreatedAt(),
                board.getUpdatedAt()
        )));

        BoardResponse response = boardService.getBoard(1L, 10L);

        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.category()).isEqualTo(BoardCategory.QNA);
        assertThat(response.recipeId()).isEqualTo("123");
        assertThat(response.likeCount()).isEqualTo(4L);
        assertThat(response.commentCount()).isEqualTo(6L);
        assertThat(response.isLiked()).isTrue();
    }

    @Test
    void updateBoardThrowsWhenCurrentUserIsNotOwner() {
        User owner = createUser(1L, "테스터");
        User other = createUser(2L, "다른유저");
        Board board = createBoard(10L, owner, "제목", BoardCategory.REVIEW, null);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.updateBoard(
                other.getUserId(),
                10L,
                new BoardUpdateRequest(
                        "수정",
                        "수정내용",
                        List.of(),
                        BoardCategory.RECIPE,
                        List.of(),
                        "김치찌개"
                )
        ))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOARD_FORBIDDEN);
    }

    @Test
    void deleteBoardDeletesWhenCurrentUserIsOwner() {
        User user = createUser(1L, "테스터");
        Board board = createBoard(10L, user, "제목", BoardCategory.REVIEW, null);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        boardService.deleteBoard(1L, 10L);

        verify(boardRepository).delete(board);
    }

    private User createUser(Long userId, String nickname) {
        User user = User.create("google-" + userId, "user" + userId + "@gmail.com", nickname);
        user.assignUserId(userId);
        return user;
    }

    private Board createBoard(Long boardId, User user, String title, BoardCategory category, String recipeId) {
        Board board = Board.create(user, title, "내용", List.of(), category, List.of(), recipeId);
        board.assignBoardId(boardId);
        return board;
    }
}
