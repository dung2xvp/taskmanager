package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.BoardList;

@ApplicationScoped
public class BoardListDao implements PanacheRepository<BoardList> {

    public List<BoardList> findByBoardId(Long boardId) {
        return find("board.id = ?1 and archived = false order by position", boardId).list();
    }
}
