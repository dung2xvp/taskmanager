package org.myapp.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.myapp.entity.Board;

import java.util.List;

@ApplicationScoped
public class BoardDao implements PanacheRepository<Board> {

    /**
     * Lấy danh sách Board mà user là thành viên (bao gồm cả owner).
     */
    public List<Board> findByMemberId(Long userId) {
        return getEntityManager()
                .createQuery(
                    "SELECT b FROM Board b " +
                    "JOIN BoardMember bm ON bm.board = b " +
                    "WHERE bm.user.id = :userId AND b.archived = false " +
                    "ORDER BY b.id DESC",
                    Board.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}

