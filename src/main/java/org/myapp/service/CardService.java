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
import org.myapp.security.identity.CurrentUser;

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
    }

    // ── UC: Xóa Card (archive) ───────────────────────────
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardDao.findById(cardId);
        if (card == null) {
            throw new BadRequestException("Card not found");
        }
        card.archived = true; // Soft delete
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
        dto.labelIds    = List.of();
        dto.memberIds   = List.of();
        return dto;
    }
}
