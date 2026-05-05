package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.Activity;

@ApplicationScoped
public class ActivityDao implements PanacheRepository<Activity> {

    public List<Activity> findRecentByBoardId(Long boardId, int limit) {
        return find("board.id = ?1 order by createdAt desc", boardId).page(0, limit).list();
    }
}
