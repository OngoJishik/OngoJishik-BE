package com.project.ongojisik.domain.board.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardResponse createBoard(Long userId, BoardCreateRequest request) {
        User user = findCurrentUser(userId);
        Board board = Board.create(user, request.title(), request.content(), normalizeImageUrls(request.imageUrls()), request.category());
        Board savedBoard = boardRepository.save(board);
        return BoardResponse.from(savedBoard);
    }

    @Transactional(readOnly = true)
    public Page<BoardSummaryResponse> getBoardList(Long userId, BoardCategory category, Pageable pageable) {
        if (category == null) {
            return boardRepository.findAllSummaryWithCounts(userId, pageable);
        }

        return boardRepository.findSummaryByCategoryWithCounts(userId, category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardSummaryResponse> searchBoardsByTitle(Long userId, String title, BoardCategory category, Pageable pageable) {
        if (title == null || title.isBlank()) {
            throw new APIException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (category == null) {
            return boardRepository.findSummaryByTitleWithCounts(userId, title, pageable);
        }

        return boardRepository.findSummaryByTitleAndCategoryWithCounts(userId, title, category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardSummaryResponse> getMyBoardList(Long userId, Pageable pageable) {
        findCurrentUser(userId);
        return boardRepository.findMySummaryWithCounts(userId, pageable);
    }

    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long userId, Long boardId) {
        return boardRepository.findResponseByIdWithCounts(userId, boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    @Transactional
    public BoardResponse updateBoard(Long userId, Long boardId, BoardUpdateRequest request) {
        Board board = findBoard(boardId);
        validateBoardOwner(board, userId);
        board.update(request.title(), request.content(), normalizeImageUrls(request.imageUrls()), request.category());
        return boardRepository.findResponseByIdWithCounts(userId, boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = findBoard(boardId);
        validateBoardOwner(board, userId);
        boardRepository.delete(board);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    private User findCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }

    private List<String> normalizeImageUrls(List<String> imageUrls) {
        return imageUrls == null ? List.of() : List.copyOf(imageUrls);
    }

    private void validateBoardOwner(Board board, Long userId) {
        if (!board.getUser().getUserId().equals(userId)) {
            throw new APIException(ErrorCode.BOARD_FORBIDDEN);
        }
    }
}
