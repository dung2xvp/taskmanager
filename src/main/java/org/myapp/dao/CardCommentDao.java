package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.myapp.entity.CardComment;

@ApplicationScoped
public class CardCommentDao implements PanacheRepository<CardComment> {
    public java.util.List<CardComment> findByCardId(Long cardId) {
        return find("card.id = ?1 order by createdAt desc", cardId).list();
    }
}
