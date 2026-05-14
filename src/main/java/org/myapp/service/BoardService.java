package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.myapp.dao.BoardDao;
import org.myapp.dao.BoardMemberDao;
import org.myapp.dto.board.BoardSummaryDto;
import org.myapp.entity.Board;
import org.myapp.entity.BoardMember;
import org.myapp.entity.BoardRole;
import org.myapp.security.identity.CurrentUser;

@ApplicationScoped
public class BoardService {

    @Inject
    BoardDao boardDao;

    @Inject
    BoardMemberDao boardMemberDao;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public BoardSummaryDto createBoard(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Board name is required");
        }

        Board board = new Board();
        board.name = name.trim();
        board.description = (description != null) ? description.trim() : "";
        board.owner = currentUser.getUser();
        boardDao.persist(board);

        BoardMember member = new BoardMember();
        member.board = board;
        member.user = currentUser.getUser();
        member.role = BoardRole.OWNER;
        boardMemberDao.persist(member);

        return toSummary(board);
    }

    public List<BoardSummaryDto> getMyBoards() {
        return boardDao.findByMemberId(currentUser.getUser().id)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardDao.findById(boardId);
        if (board == null) {
            throw new BadRequestException("Board not found");
        }
        boardDao.delete(board);
    }

    private BoardSummaryDto toSummary(Board board) {
        BoardSummaryDto dto = new BoardSummaryDto();
        dto.id = board.id;
        dto.name = board.name;
        dto.description = board.description;
        dto.ownerId = board.owner.id;
        dto.visibility = board.visibility;
        return dto;
    }
}
