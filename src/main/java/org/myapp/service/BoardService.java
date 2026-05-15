package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.myapp.dao.BoardDao;
import org.myapp.dao.BoardMemberDao;
import org.myapp.dao.LabelDao;
import org.myapp.dto.board.BoardSummaryDto;
import org.myapp.dto.board.LabelCreateDto;
import org.myapp.dto.board.LabelDto;
import org.myapp.entity.Board;
import org.myapp.entity.BoardMember;
import org.myapp.entity.BoardRole;
import org.myapp.entity.Label;
import org.myapp.entity.User;
import org.myapp.dao.UserDao;
import org.myapp.security.identity.CurrentUser;
import org.myapp.websocket.BoardWebSocket;

@ApplicationScoped
public class BoardService {

    @Inject
    BoardDao boardDao;

    @Inject
    BoardMemberDao boardMemberDao;

    @Inject
    LabelDao labelDao;

    @Inject
    UserDao userDao;

    @Inject
    BoardWebSocket boardWebSocket;

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
        if (board.owner.id.equals(currentUser.getUser().id)) {
            boardDao.delete(board);
        } else {
            throw new BadRequestException("Not owner");
        }
    }

    @Transactional
    public LabelDto createLabel(Long boardId, LabelCreateDto dto) {
        Board board = boardDao.findById(boardId);
        if (board == null || board.archived) throw new BadRequestException("Board not found");

        Label label = new Label();
        label.board = board;
        label.name = dto.name;
        label.color = dto.color;
        labelDao.persist(label);

        LabelDto result = new LabelDto();
        result.id = label.id;
        result.name = label.name;
        result.color = label.color;
        return result;
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

    @Transactional
    public void addMember(Long boardId, String username) {
        Board board = boardDao.findById(boardId);
        if (board == null || board.archived) throw new BadRequestException("Board not found");

        User user = userDao.findByUsername(username).orElseThrow(() -> new BadRequestException("User not found: " + username));

        boolean isMember = boardMemberDao.find("board.id = ?1 and user.id = ?2", boardId, user.id).count() > 0;
        if (!isMember) {
            BoardMember bm = new BoardMember();
            bm.board = board;
            bm.user = user;
            bm.role = BoardRole.MEMBER;
            boardMemberDao.persist(bm);
            boardWebSocket.broadcast(boardId, "UPDATE_BOARD");
        }
    }
}
