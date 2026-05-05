package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.myapp.dao.BoardDao;
import org.myapp.dao.BoardListDao;
import org.myapp.dao.BoardMemberDao;
import org.myapp.dao.CardDao;
import org.myapp.dao.CardLabelDao;
import org.myapp.dao.CardMemberDao;
import org.myapp.dao.LabelDao;
import org.myapp.dto.board.BoardListDto;
import org.myapp.dto.board.BoardMemberDto;
import org.myapp.dto.board.BoardStateDto;
import org.myapp.dto.board.BoardSummaryDto;
import org.myapp.dto.board.CardDto;
import org.myapp.dto.board.LabelDto;
import org.myapp.entity.Board;
import org.myapp.entity.BoardList;
import org.myapp.entity.Card;
import org.myapp.entity.CardLabel;
import org.myapp.entity.CardMember;

@ApplicationScoped
public class BoardQueryService {

    private final BoardDao boardDao;
    private final BoardMemberDao boardMemberDao;
    private final BoardListDao boardListDao;
    private final CardDao cardDao;
    private final CardMemberDao cardMemberDao;
    private final CardLabelDao cardLabelDao;
    private final LabelDao labelDao;

    public BoardQueryService(
            BoardDao boardDao,
            BoardMemberDao boardMemberDao,
            BoardListDao boardListDao,
            CardDao cardDao,
            CardMemberDao cardMemberDao,
            CardLabelDao cardLabelDao,
            LabelDao labelDao
    ) {
        this.boardDao = boardDao;
        this.boardMemberDao = boardMemberDao;
        this.boardListDao = boardListDao;
        this.cardDao = cardDao;
        this.cardMemberDao = cardMemberDao;
        this.cardLabelDao = cardLabelDao;
        this.labelDao = labelDao;
    }

    public BoardStateDto getBoardState(Long boardId) {
        Board board = boardDao.findById(boardId);
        if (board == null || board.archived) {
            throw new NotFoundException("Board not found");
        }

        List<BoardMemberDto> members = boardMemberDao.findByBoardId(boardId).stream()
                .map(member -> new BoardMemberDto(
                        member.user.id,
                        member.user.username,
                        member.user.fullName,
                        member.role
                ))
                .toList();

        List<LabelDto> labels = labelDao.findByBoardId(boardId).stream()
                .map(label -> new LabelDto(label.id, label.name, label.color))
                .toList();

        List<BoardList> lists = boardListDao.findByBoardId(boardId);
        List<Card> cards = cardDao.findActiveByBoardId(boardId);
        List<CardMember> cardMembers = cardMemberDao.findByBoardId(boardId);
        List<CardLabel> cardLabels = cardLabelDao.findByBoardId(boardId);

        Map<Long, List<Long>> memberIdsByCard = groupMemberIdsByCard(cardMembers);
        Map<Long, List<Long>> labelIdsByCard = groupLabelIdsByCard(cardLabels);
        Map<Long, List<CardDto>> cardsByList = groupCardsByList(cards, memberIdsByCard, labelIdsByCard);

        List<BoardListDto> listDtos = lists.stream()
                .map(list -> new BoardListDto(
                        list.id,
                        list.name,
                        list.position,
                        cardsByList.getOrDefault(list.id, List.of())
                ))
                .toList();

        BoardSummaryDto boardSummary = new BoardSummaryDto(
                board.id,
                board.name,
                board.description,
                board.owner.id,
                board.visibility
        );

        return new BoardStateDto(boardSummary, members, labels, listDtos);
    }

    private Map<Long, List<Long>> groupMemberIdsByCard(List<CardMember> cardMembers) {
        Map<Long, List<Long>> result = new HashMap<>();
        for (CardMember cardMember : cardMembers) {
            result.computeIfAbsent(cardMember.card.id, ignored -> new ArrayList<>()).add(cardMember.user.id);
        }
        return result;
    }

    private Map<Long, List<Long>> groupLabelIdsByCard(List<CardLabel> cardLabels) {
        Map<Long, List<Long>> result = new HashMap<>();
        for (CardLabel cardLabel : cardLabels) {
            result.computeIfAbsent(cardLabel.card.id, ignored -> new ArrayList<>()).add(cardLabel.label.id);
        }
        return result;
    }

    private Map<Long, List<CardDto>> groupCardsByList(
            List<Card> cards,
            Map<Long, List<Long>> memberIdsByCard,
            Map<Long, List<Long>> labelIdsByCard
    ) {
        Map<Long, List<CardDto>> result = new HashMap<>();
        for (Card card : cards) {
            if (card.list == null || card.list.id == null) {
                throw new WebApplicationException("Card has invalid list reference", Response.Status.CONFLICT);
            }

            CardDto cardDto = new CardDto(
                    card.id,
                    card.list.id,
                    card.title,
                    card.description,
                    card.position,
                    card.startDate,
                    card.dueDate,
                    card.coverColor,
                    memberIdsByCard.getOrDefault(card.id, List.of()),
                    labelIdsByCard.getOrDefault(card.id, List.of())
            );
            result.computeIfAbsent(card.list.id, ignored -> new ArrayList<>()).add(cardDto);
        }
        return result;
    }
}
