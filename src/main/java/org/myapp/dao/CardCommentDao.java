package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.myapp.entity.CardComment;

@ApplicationScoped
public class CardCommentDao implements PanacheRepository<CardComment> {
}
