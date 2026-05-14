package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import org.myapp.dao.BoardDao;
import org.myapp.dao.BoardListDao;
import org.myapp.dto.board.BoardListCreateDto;
import org.myapp.dto.board.BoardListDto;
import org.myapp.entity.Board;
import org.myapp.entity.BoardList;

@ApplicationScoped
public class BoardListService {

    @Inject
    BoardListDao boardListDao;

    @Inject
    BoardDao boardDao;

    @Transactional
    public BoardListDto createList(Long boardId, BoardListCreateDto dto) {
        Board board = boardDao.findById(boardId);
        if (board == null || board.archived) {
            throw new BadRequestException("Board not found");
        }

        long nextPos = (long) boardListDao
                .find("board.id = ?1 and archived = false", boardId)
                .list().size() * 1000L + 1000L;

        BoardList list = new BoardList();
        list.board = board;
        list.name = dto.name.trim();
        list.position = nextPos;
        boardListDao.persist(list);

        return toDto(list);
    }

    @Transactional
    public BoardListDto renameList(Long boardId, Long listId, BoardListCreateDto dto) {
        BoardList list = boardListDao.findById(listId);
        if (list == null || list.archived || !list.board.id.equals(boardId)) {
            throw new BadRequestException("List not found in this board");
        }

        list.name = dto.name.trim();
        return toDto(list);
    }

    @Transactional
    public void deleteList(Long boardId, Long listId) {
        BoardList list = boardListDao.findById(listId);
        if (list == null || !list.board.id.equals(boardId)) {
            throw new BadRequestException("List not found in this board");
        }
        list.archived = true;
    }

    private BoardListDto toDto(BoardList list) {
        BoardListDto dto = new BoardListDto();
        dto.id = list.id;
        dto.name = list.name;
        dto.position = list.position;
        dto.cards = List.of();
        return dto;
    }
}
