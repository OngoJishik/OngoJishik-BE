package com.project.ongojisik.domain.comment.service;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.repository.BoardRepository;
import com.project.ongojisik.domain.comment.dto.CommentRequest;
import com.project.ongojisik.domain.comment.dto.CommentResponse;
import com.project.ongojisik.domain.comment.dto.MyCommentResponse;
import com.project.ongojisik.domain.comment.entity.Comment;
import com.project.ongojisik.domain.comment.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long userId, Long boardId, CommentRequest request) {
        User user = findCurrentUser(userId);
        Board board = findBoard(boardId);
        Comment comment = Comment.create(board, user, request.commentContent());
        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentList(Long boardId, Pageable pageable) {
        findBoard(boardId);
        return commentRepository.findByBoardBoardId(boardId, pageable)
                .map(CommentResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<MyCommentResponse> getMyCommentList(Long userId, Pageable pageable) {
        findCurrentUser(userId);
        return commentRepository.findByUserUserId(userId, pageable)
                .map(MyCommentResponse::from);
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentRequest request) {
        Comment comment = findComment(commentId);
        validateCommentOwner(comment, userId);
        comment.update(request.commentContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = findComment(commentId);
        validateCommentOwner(comment, userId);
        commentRepository.delete(comment);
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new APIException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new APIException(ErrorCode.BOARD_NOT_FOUND));
    }

    private User findCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new APIException(ErrorCode.COMMENT_FORBIDDEN);
        }
    }
}
