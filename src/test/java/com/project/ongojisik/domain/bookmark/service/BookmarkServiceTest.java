package com.project.ongojisik.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.bookmark.dto.BookmarkResponse;
import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
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
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        bookmarkService = new BookmarkService(bookmarkRepository, boardRepository, userRepository);
    }

    @Test
    void addBookmarkCreatesBookmarkWhenNotExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);
        Bookmark bookmark = Bookmark.create(user, board);
        ReflectionTestUtils.setField(bookmark, "bookmarkId", 100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(bookmarkRepository.existsByUserUserIdAndBoardBoardId(1L, 10L)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        BookmarkResponse response = bookmarkService.addBookmark(1L, 10L);

        assertThat(response.bookmarkId()).isEqualTo(100L);
        assertThat(response.boardId()).isEqualTo(10L);
        assertThat(response.authorId()).isEqualTo(1L);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void addBookmarkThrowsWhenAlreadyExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(bookmarkRepository.existsByUserUserIdAndBoardBoardId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> bookmarkService.addBookmark(1L, 10L))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOOKMARK_ALREADY_EXISTS);
    }

    @Test
    void deleteBookmarkDeletesWhenExists() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);
        Bookmark bookmark = Bookmark.create(user, board);
        ReflectionTestUtils.setField(bookmark, "bookmarkId", 100L);

        when(bookmarkRepository.findByUserUserIdAndBoardBoardId(1L, 10L)).thenReturn(Optional.of(bookmark));

        bookmarkService.deleteBookmark(1L, 10L);

        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    void getBookmarkListReturnsPage() {
        User user = createUser(1L);
        Board board = createBoard(10L, user);
        Bookmark bookmark = Bookmark.create(user, board);
        ReflectionTestUtils.setField(bookmark, "bookmarkId", 100L);

        Page<Bookmark> bookmarks = new PageImpl<>(java.util.List.of(bookmark), PageRequest.of(0, 10), 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookmarkRepository.findByUserUserId(1L, PageRequest.of(0, 10))).thenReturn(bookmarks);

        Page<BookmarkResponse> response = bookmarkService.getBookmarkList(1L, PageRequest.of(0, 10));

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getContent().get(0).boardId()).isEqualTo(10L);
    }

    private User createUser(Long userId) {
        User user = User.create("google-123", "user@gmail.com", "테스터");
        ReflectionTestUtils.setField(user, "userId", userId);
        return user;
    }

    private Board createBoard(Long boardId, User user) {
        Board board = Board.create(user, "제목", "내용", java.util.List.of("image.png"), BoardCategory.REVIEW);
        ReflectionTestUtils.setField(board, "boardId", boardId);
        return board;
    }
}
