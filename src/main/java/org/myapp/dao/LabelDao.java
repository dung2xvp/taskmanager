package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.Label;

@ApplicationScoped
public class LabelDao implements PanacheRepository<Label> {

    public List<Label> findByBoardId(Long boardId) {
        return find("board.id = ?1 order by name", boardId).list();
    }
}
