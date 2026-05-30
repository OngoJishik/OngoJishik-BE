package com.project.ongojisik.domain.board.service;

import com.project.ongojisik.domain.board.dto.BoardCreateRequest;
import com.project.ongojisik.domain.board.dto.BoardResponse;
import com.project.ongojisik.domain.board.dto.BoardSummaryResponse;
import com.project.ongojisik.domain.board.dto.BoardUpdateRequest;
import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request) {
        User user = findCurrentUser();
        Board board = Board.create(user, request.title(), request.content(), request.imageUrl());
        Board savedBoard = boardRepository.save(board);
        return BoardResponse.from(savedBoard);
    }

    @Transactional(readOnly = true)
    public Page<BoardSummaryResponse> getBoardList(Pageable pageable) {
        return boardRepository.findAll(pageable).map(BoardSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<BoardSummaryResponse> searchBoardsByTitle(String title, Pageable pageable) {
        if (title == null || title.isBlank()) {
            throw new APIException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return boardRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(BoardSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        return BoardResponse.from(findBoard(boardId));
    }

    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardUpdateRequest request) {
        Board board = findBoard(boardId);
        validateBoardOwner(board);
        board.update(request.title(), request.content(), request.imageUrl());
        return BoardResponse.from(board);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = findBoard(boardId);
        validateBoardOwner(board);
        boardRepository.delete(board);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    private User findCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateBoardOwner(Board board) {
        Long currentUserId = getCurrentUserId();
        if (!board.getUser().getUserId().equals(currentUserId)) {
            throw new APIException(ErrorCode.BOARD_FORBIDDEN);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new APIException(ErrorCode.UNAUTHORIZED);
        }

        try {
            return Long.valueOf(String.valueOf(authentication.getPrincipal()));
        } catch (NumberFormatException exception) {
            throw new APIException(ErrorCode.UNAUTHORIZED);
        }
    }
}
