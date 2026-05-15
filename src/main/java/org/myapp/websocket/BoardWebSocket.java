package org.myapp.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/board/{boardId}")
@ApplicationScoped
public class BoardWebSocket {

    // Lữu trữ danh sách session đang kết nối theo từng boardId
    private final Map<Long, Set<Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("boardId") Long boardId) {
        sessions.computeIfAbsent(boardId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("boardId") Long boardId) {
        Set<Session> boardSessions = sessions.get(boardId);
        if (boardSessions != null) {
            boardSessions.remove(session);
            if (boardSessions.isEmpty()) {
                sessions.remove(boardId);
            }
        }
    }

    @OnError
    public void onError(Session session, @PathParam("boardId") Long boardId, Throwable throwable) {
        onClose(session, boardId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("boardId") Long boardId) {
        // Client có thể gửi message, nhưng chủ yếu là Server push xuống
    }

    public void broadcast(Long boardId, String message) {
        Set<Session> boardSessions = sessions.get(boardId);
        if (boardSessions != null) {
            boardSessions.forEach(s -> {
                if (s.isOpen()) {
                    s.getAsyncRemote().sendText(message);
                }
            });
        }
    }
}
