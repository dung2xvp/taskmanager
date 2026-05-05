package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.CardMember;

@ApplicationScoped
public class CardMemberDao implements PanacheRepository<CardMember> {

    public List<CardMember> findByBoardId(Long boardId) {
        return find("card.board.id = ?1", boardId).list();
    }
}
