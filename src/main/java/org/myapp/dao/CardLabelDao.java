package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.CardLabel;

@ApplicationScoped
public class CardLabelDao implements PanacheRepository<CardLabel> {

    public List<CardLabel> findByBoardId(Long boardId) {
        return find("card.board.id = ?1", boardId).list();
    }
}
