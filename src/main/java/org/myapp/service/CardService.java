package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.myapp.dao.BoardDao;
import org.myapp.dao.CardDao;
import org.myapp.dao.BoardListDao;
import org.myapp.dto.board.CardCreateDto;
import org.myapp.dto.board.CardDto;
import org.myapp.dto.board.CardMoveDto;
import org.myapp.dto.board.CardUpdateDto;
import org.myapp.entity.Board;
import org.myapp.entity.BoardList;
import org.myapp.entity.Card;
import org.myapp.entity.CardLabel;
import org.myapp.entity.CardMember;
import org.myapp.entity.CardComment;
import org.myapp.entity.Label;
import org.myapp.entity.User;
import org.myapp.security.identity.CurrentUser;
import org.myapp.dao.CardLabelDao;
import org.myapp.dao.CardMemberDao;
import org.myapp.dao.CardCommentDao;
import org.myapp.dao.LabelDao;
import org.myapp.dao.UserDao;
import org.myapp.dto.board.CardCommentDto;
import org.myapp.dto.board.CardCommentCreateDto;
import org.myapp.websocket.BoardWebSocket;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class CardService {

    @Inject
    CardDao cardDao;

    @Inject
    BoardListDao boardListDao;

    @Inject
    BoardDao boardDao;

    @Inject
    CurrentUser currentUser;

    @Inject
    CardLabelDao cardLabelDao;

    @Inject
    CardMemberDao cardMemberDao;

    @Inject
    LabelDao labelDao;

    @Inject
    UserDao userDao;

    @Inject
    CardCommentDao cardCommentDao;

    @Inject
    BoardWebSocket boardWebSocket;

    // ── UC: Tạo Card mới trong một List ──────────────────
    @Transactional
    public CardDto createCard(Long listId, CardCreateDto dto) {
        BoardList list = boardListDao.findById(listId);
        if (list == null || list.archived) {
            throw new BadRequestException("List not found");
        }

        // Tính position: số card hiện tại * 1000 + 1000
        long nextPos = (long) cardDao
                .find("list.id = ?1 and archived = false", listId)
                .list().size() * 1000L + 1000L;

        Card card = new Card();
        card.title       = dto.title.trim();
        card.description = dto.description != null ? dto.description.trim() : "";
        card.list        = list;
        card.board       = list.board;
        card.createdBy   = currentUser.getUser();
        card.position    = nextPos;
        cardDao.persist(card);

        boardWebSocket.broadcast(list.board.id, "UPDATE_BOARD");
        return toDto(card);
    }

    // ── UC: Cập nhật thông tin Card ──────────────────────
    @Transactional
    public CardDto updateCard(Long cardId, CardUpdateDto dto) {
        Card card = cardDao.findById(cardId);
        if (card == null || card.archived) {
            throw new BadRequestException("Card not found");
        }

        if (dto.title != null && !dto.title.isBlank()) {
            card.title = dto.title.trim();
        }
        if (dto.description != null) {
            card.description = dto.description.trim();
        }
        if (dto.coverColor != null) {
            card.coverColor = dto.coverColor;
        }
        if (dto.dueDate != null) {
            if (dto.dueDate.isBlank()) {
                card.dueDate = null;
            } else {
                card.dueDate = LocalDate.parse(dto.dueDate);
            }
        }
        if (dto.startDate != null) {
            card.startDate = LocalDate.parse(dto.startDate);
        }

        boardWebSocket.broadcast(card.list.board.id, "UPDATE_BOARD");
        return toDto(card);
    }

    // ── UC: Kéo thả Card sang List khác ─────────────────
    @Transactional
    public void moveCard(Long cardId, CardMoveDto dto) {
        Card card = cardDao.findById(cardId);
        if (card == null || card.archived) {
            throw new BadRequestException("Card not found");
        }

        BoardList targetList = boardListDao.findById(dto.listId);
        if (targetList == null || targetList.archived) {
            throw new BadRequestException("Target list not found");
        }

        card.list     = targetList;
        card.position = dto.position.longValue();
        boardWebSocket.broadcast(targetList.board.id, "UPDATE_BOARD");
    }

    // ── UC: Xóa Card (archive) ───────────────────────────
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardDao.findById(cardId);
        if (card == null) {
            throw new BadRequestException("Card not found");
        }
        card.archived = true; // Soft delete
        boardWebSocket.broadcast(card.list.board.id, "UPDATE_BOARD");
    }

    // ── UC: Labels & Members ──────────────────────────────
    @Transactional
    public void addLabel(Long cardId, Long labelId) {
        Card card = cardDao.findById(cardId);
        if (card == null || card.archived) throw new BadRequestException("Card not found");

        Label label = labelDao.findById(labelId);
        if (label == null) throw new BadRequestException("Label not found");

        CardLabel existing = cardLabelDao.find("card.id = ?1 and label.id = ?2", cardId, labelId).firstResult();
        if (existing == null) {
            CardLabel cl = new CardLabel();
            cl.card = card;
            cl.label = label;
            cardLabelDao.persist(cl);
            boardWebSocket.broadcast(card.list.board.id, "UPDATE_BOARD");
        }
    }

    @Transactional
    public void removeLabel(Long cardId, Long labelId) {
        CardLabel existing = cardLabelDao.find("card.id = ?1 and label.id = ?2", cardId, labelId).firstResult();
        if (existing != null) {
            cardLabelDao.delete(existing);
            boardWebSocket.broadcast(existing.card.list.board.id, "UPDATE_BOARD");
        }
    }

    @Transactional
    public void addMember(Long cardId, Long userId) {
        Card card = cardDao.findById(cardId);
        if (card == null || card.archived) throw new BadRequestException("Card not found");

        User user = userDao.findById(userId);
        if (user == null) throw new BadRequestException("User not found");

        CardMember existing = cardMemberDao.find("card.id = ?1 and user.id = ?2", cardId, userId).firstResult();
        if (existing == null) {
            CardMember cm = new CardMember();
            cm.card = card;
            cm.user = user;
            cardMemberDao.persist(cm);
            boardWebSocket.broadcast(card.list.board.id, "UPDATE_BOARD");
        }
    }

    @Transactional
    public void removeMember(Long cardId, Long userId) {
        CardMember existing = cardMemberDao.find("card.id = ?1 and user.id = ?2", cardId, userId).firstResult();
        if (existing != null) {
            cardMemberDao.delete(existing);
            boardWebSocket.broadcast(existing.card.list.board.id, "UPDATE_BOARD");
        }
    }

    // ── UC: Comments ──────────────────────────────────────
    @Transactional
    public CardCommentDto addComment(Long cardId, CardCommentCreateDto dto) {
        Card card = cardDao.findById(cardId);
        if (card == null || card.archived) throw new BadRequestException("Card not found");

        CardComment comment = new CardComment();
        comment.card = card;
        comment.content = dto.content.trim();
        comment.author = currentUser.getUser();
        cardCommentDao.persist(comment);

        boardWebSocket.broadcast(card.list.board.id, "UPDATE_BOARD");
        return toCommentDto(comment);
    }

    public List<CardCommentDto> getComments(Long cardId) {
        return cardCommentDao.findByCardId(cardId).stream()
                .map(this::toCommentDto)
                .toList();
    }

    private CardCommentDto toCommentDto(CardComment c) {
        CardCommentDto dto = new CardCommentDto();
        dto.id = c.id;
        dto.content = c.content;
        dto.authorId = c.author.id;
        dto.authorName = c.author.fullName != null ? c.author.fullName : c.author.username;
        dto.createdAt = c.createdAt;
        return dto;
    }

    // ── Helper ───────────────────────────────────────────
    private CardDto toDto(Card card) {
        CardDto dto = new CardDto();
        dto.id          = card.id;
        dto.listId      = card.list.id;
        dto.title       = card.title;
        dto.description = card.description;
        dto.position    = card.position;
        dto.coverColor  = card.coverColor;
        dto.dueDate     = card.dueDate;
        dto.startDate   = card.startDate;
        dto.labelIds    = cardLabelDao.find("card.id", card.id).stream().map(cl -> cl.label.id).toList();
        dto.memberIds   = cardMemberDao.find("card.id", card.id).stream().map(cm -> cm.user.id).toList();
        return dto;
    }
}
