package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.myapp.entity.BoardMember;

@ApplicationScoped
public class BoardMemberDao implements PanacheRepository<BoardMember> {

    public List<BoardMember> findByBoardId(Long boardId) {
        return find("board.id = ?1", boardId).list();
    }
}
