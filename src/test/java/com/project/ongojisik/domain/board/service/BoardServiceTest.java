  package com.project.ongojisik.domain.board.service;

  import static org.assertj.core.api.Assertions.assertThat;
  import static org.assertj.core.api.Assertions.assertThatThrownBy;
  import static org.mockito.ArgumentMatchers.any;
  import static org.mockito.Mockito.verify;
  import static org.mockito.Mockito.when;

  import com.project.ongojisik.domain.board.dto.BoardCreateRequest;
  import com.project.ongojisik.domain.board.dto.BoardResponse;
  import
  com.project.ongojisik.domain.board.dto.BoardSummaryResponse;
  import com.project.ongojisik.domain.board.dto.BoardUpdateRequest;
  import com.project.ongojisik.domain.board.entity.Board;
  import com.project.ongojisik.domain.board.entity.BoardCategory;
  import
  com.project.ongojisik.domain.board.repository.BoardRepository;
  import com.project.ongojisik.domain.user.entity.User;
  import
  com.project.ongojisik.domain.user.repository.UserRepository;
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

  @ExtendWith(MockitoExtension.class)
  class BoardServiceTest {

      @Mock
      private BoardRepository boardRepository;

      @Mock
      private UserRepository userRepository;

      private BoardService boardService;

      @BeforeEach
      void setUp() {
          boardService = new BoardService(boardRepository,
          userRepository);
      }

      @Test
      void createBoardCreatesBoardForCurrentUser() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);

          Board savedBoard = Board.create(user, "제목", "내용",
          java.util.List.of("image.png"), BoardCategory.REVIEW);
          savedBoard.assignBoardId(10L);


          when(userRepository.findById(1L)).thenReturn(Optional.of(u
          ser));

          when(boardRepository.save(any(Board.class))).thenReturn(sa
          vedBoard);

          BoardResponse response = boardService.createBoard(
                  1L,
                  new BoardCreateRequest("제목", "내용",
                  java.util.List.of("image.png"),
                  BoardCategory.REVIEW)
          );

          assertThat(response.boardId()).isEqualTo(10L);
          assertThat(response.title()).isEqualTo("제목");

          assertThat(response.category()).isEqualTo(BoardCategory.RE
          VIEW);

          assertThat(response.imageUrls()).containsExactly("image.pn
          g");
          assertThat(response.likeCount()).isZero();
          assertThat(response.commentCount()).isZero();
          assertThat(response.isLiked()).isFalse();
          verify(boardRepository).save(any(Board.class));
      }

      @Test
      void getBoardListReturnsPage() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "제목", "내용",
          java.util.List.of(), BoardCategory.REVIEW);
          board.assignBoardId(10L);

          Page<BoardSummaryResponse> boards = new PageImpl<>(

                  java.util.List.of(BoardSummaryResponse.from(board)
                  ),
                  PageRequest.of(0, 10),
                  1
          );
          when(boardRepository.findAllSummaryWithCounts(1L,
          PageRequest.of(0, 10))).thenReturn(boards);

          Page<BoardSummaryResponse> response =
          boardService.getBoardList(1L, null, PageRequest.of(0,
          10));

          assertThat(response.getTotalElements()).isEqualTo(1L);

          assertThat(response.getContent().get(0).likeCount()).isZer
          o();

          assertThat(response.getContent().get(0).commentCount()).is
          Zero();
      }

      @Test
      void getBoardListReturnsFilteredPageWhenCategoryExists() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "제목", "내용",
          java.util.List.of(), BoardCategory.RECIPE);
          board.assignBoardId(10L);

          Page<BoardSummaryResponse> boards = new PageImpl<>(
                  java.util.List.of(new BoardSummaryResponse(
                          10L,
                          "제목",
                          java.util.List.of(),
                          BoardCategory.RECIPE,
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
          when(boardRepository.findSummaryByCategoryWithCounts(1L,
          BoardCategory.RECIPE, PageRequest.of(0, 10)))
                  .thenReturn(boards);

          Page<BoardSummaryResponse> response =
          boardService.getBoardList(1L, BoardCategory.RECIPE,
          PageRequest.of(0, 10));

          assertThat(response.getTotalElements()).isEqualTo(1L);

          assertThat(response.getContent().get(0).likeCount()).isEqu
          alTo(2L);

          assertThat(response.getContent().get(0).commentCount()).is
          EqualTo(3L);

          assertThat(response.getContent().get(0).isLiked()).isTrue(
          );
      }

      @Test
      void searchBoardsByTitleReturnsMatchedBoards() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "김치찌개 맛집", "내용",
          java.util.List.of(), BoardCategory.REVIEW);
          board.assignBoardId(11L);

          Page<BoardSummaryResponse> boards = new PageImpl<>(

                  java.util.List.of(BoardSummaryResponse.from(board)
                  ),
                  PageRequest.of(0, 10),
                  1
          );
          when(boardRepository.findSummaryByTitleWithCounts(1L, "김
          치", PageRequest.of(0, 10)))
                  .thenReturn(boards);

          Page<BoardSummaryResponse> response =
          boardService.searchBoardsByTitle(1L, "김치", null,
          PageRequest.of(0, 10));

          assertThat(response.getTotalElements()).isEqualTo(1L);
      }

      @Test
      void searchBoardsByTitleReturnsMatchedBoardsInCategory() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "김치찌개 레시피", "내
          용", java.util.List.of(), BoardCategory.RECIPE);
          board.assignBoardId(11L);

          Page<BoardSummaryResponse> boards = new PageImpl<>(
                  java.util.List.of(new BoardSummaryResponse(
                          11L,
                          "김치찌개 레시피",
                          java.util.List.of(),
                          BoardCategory.RECIPE,
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

          when(boardRepository.findSummaryByTitleAndCategoryWithCoun
          ts(1L, "김치", BoardCategory.RECIPE, PageRequest.of(0,
          10)))
                  .thenReturn(boards);

          Page<BoardSummaryResponse> response =
          boardService.searchBoardsByTitle(1L, "김치",
          BoardCategory.RECIPE, PageRequest.of(0, 10));

          assertThat(response.getTotalElements()).isEqualTo(1L);

          assertThat(response.getContent().get(0).likeCount()).isEqu
          alTo(5L);

          assertThat(response.getContent().get(0).commentCount()).is
          EqualTo(7L);

          assertThat(response.getContent().get(0).isLiked()).isTrue(
          );
      }

      @Test
      void getMyBoardListReturnsCurrentUserBoards() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "내 게시글", "내용",
          java.util.List.of(), BoardCategory.REVIEW);
          board.assignBoardId(12L);

          Page<BoardSummaryResponse> boards = new PageImpl<>(
                  java.util.List.of(new BoardSummaryResponse(
                          12L,
                          "내 게시글",
                          java.util.List.of(),
                          BoardCategory.REVIEW,
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

          when(userRepository.findById(1L)).thenReturn(Optional.of(u
          ser));
          when(boardRepository.findMySummaryWithCounts(1L,
          PageRequest.of(0, 10))).thenReturn(boards);

          Page<BoardSummaryResponse> response =
          boardService.getMyBoardList(1L, PageRequest.of(0, 10));

          assertThat(response.getTotalElements()).isEqualTo(1L);

          assertThat(response.getContent().get(0).authorId()).isEqua
          lTo(1L);

          assertThat(response.getContent().get(0).likeCount()).isEqu
          alTo(1L);

          assertThat(response.getContent().get(0).commentCount()).is
          EqualTo(2L);

          assertThat(response.getContent().get(0).isLiked()).isFalse
          ();
      }

      @Test
      void getBoardReturnsDetailWhenBoardExists() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "제목", "내용",
          java.util.List.of(), BoardCategory.QNA);
          board.assignBoardId(10L);

          when(boardRepository.findResponseByIdWithCounts(1L,
          10L)).thenReturn(Optional.of(new BoardResponse(
                  10L,
                  "제목",
                  "내용",
                  java.util.List.of(),
                  BoardCategory.QNA,
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

          assertThat(response.category()).isEqualTo(BoardCategory.QN
          A);
          assertThat(response.likeCount()).isEqualTo(4L);
          assertThat(response.commentCount()).isEqualTo(6L);
          assertThat(response.isLiked()).isTrue();
      }

      @Test
      void updateBoardThrowsWhenCurrentUserIsNotOwner() {
          User owner = User.create("google-123", "user@gmail.com",
          "테스터");
          owner.assignUserId(1L);
          User other = User.create("google-456", "other@gmail.com",
          "다른유저");
          other.assignUserId(2L);
          Board board = Board.create(owner, "제목", "내용",
          java.util.List.of(), BoardCategory.REVIEW);
          board.assignBoardId(10L);


          when(boardRepository.findById(10L)).thenReturn(Optional.of
          (board));

          assertThatThrownBy(() -> boardService.updateBoard(
                  2L,
                  10L,
                  new BoardUpdateRequest("수정", "수정내용",
                  java.util.List.of(), BoardCategory.RECIPE)
          ))
                  .isInstanceOf(APIException.class)
                  .extracting("errorCode")
                  .isEqualTo(ErrorCode.BOARD_FORBIDDEN);
      }

      @Test
      void deleteBoardDeletesWhenCurrentUserIsOwner() {
          User user = User.create("google-123", "user@gmail.com",
          "테스터");
          user.assignUserId(1L);
          Board board = Board.create(user, "제목", "내용",
          java.util.List.of(), BoardCategory.REVIEW);
          board.assignBoardId(10L);


          when(boardRepository.findById(10L)).thenReturn(Optional.of
          (board));

          boardService.deleteBoard(1L, 10L);

          verify(boardRepository).delete(board);
      }
  }