package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.myapp.entity.Board;

@ApplicationScoped
public class BoardDao implements PanacheRepository<Board> {
}
