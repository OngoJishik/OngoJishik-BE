package com.project.ongojisik.domain.boardlike.service;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.boardlike.dto.BoardLikeResponse;
import com.project.ongojisik.domain.boardlike.entity.BoardLike;
import com.project.ongojisik.domain.boardlike.repository.BoardLikeRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public BoardLikeResponse toggleBoardLike(Long userId, Long boardId) {
        User user = findCurrentUser(userId);
        Board board = findBoard(boardId);

        return boardLikeRepository.findByUserUserIdAndBoardBoardId(userId, boardId)
                .map(boardLike -> unlike(boardId, boardLike))
                .orElseGet(() -> like(user, board));
    }

    @Transactional(readOnly = true)
    public Long getBoardLikeCount(Long boardId) {
        findBoard(boardId);
        return boardLikeRepository.countByBoardBoardId(boardId);
    }

    private BoardLikeResponse like(User user, Board board) {
        BoardLike boardLike = BoardLike.create(user, board);
        boardLikeRepository.save(boardLike);
        Long likeCount = boardLikeRepository.countByBoardBoardId(board.getBoardId());
        return BoardLikeResponse.of(board.getBoardId(), true, likeCount);
    }

    private BoardLikeResponse unlike(Long boardId, BoardLike boardLike) {
        boardLikeRepository.delete(boardLike);
        Long likeCount = boardLikeRepository.countByBoardBoardId(boardId);
        return BoardLikeResponse.of(boardId, false, likeCount);
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
