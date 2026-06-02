package com.project.ongojisik.domain.bookmark.service;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.bookmark.dto.BookmarkResponse;
import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookmarkResponse addBookmark(Long userId, Long boardId) {
        User user = findCurrentUser(userId);
        Board board = findBoard(boardId);

        if (bookmarkRepository.existsByUserUserIdAndBoardBoardId(userId, boardId)) {
            throw new APIException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = Bookmark.create(user, board);
        return BookmarkResponse.from(bookmarkRepository.save(bookmark));
    }

    @Transactional
    public void deleteBookmark(Long userId, Long boardId) {
        Bookmark bookmark = findBookmark(userId, boardId);
        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getBookmarkList(Long userId, Pageable pageable) {
        findCurrentUser(userId);
        return bookmarkRepository.findByUserUserId(userId, pageable)
                .map(BookmarkResponse::from);
    }

    private Bookmark findBookmark(Long userId, Long boardId) {
        return bookmarkRepository.findByUserUserIdAndBoardBoardId(userId, boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOOKMARK_NOT_FOUND));
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    private User findCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }
}
