package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.Card;

@ApplicationScoped
public class CardDao implements PanacheRepository<Card> {

    public List<Card> findActiveByBoardId(Long boardId) {
        return find("board.id = ?1 and archived = false order by list.id, position", boardId).list();
    }
}
