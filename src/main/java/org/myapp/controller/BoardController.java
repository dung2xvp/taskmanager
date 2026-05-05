package org.myapp.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.myapp.dto.board.BoardStateDto;
import org.myapp.service.BoardQueryService;

@Path("/boards")
@Produces(MediaType.APPLICATION_JSON)
public class BoardController {

    private final BoardQueryService boardQueryService;

    public BoardController(BoardQueryService boardQueryService) {
        this.boardQueryService = boardQueryService;
    }

    @GET
    @Path("/{id}/board-state")
    public BoardStateDto getBoardState(@PathParam("id") Long boardId) {
        return boardQueryService.getBoardState(boardId);
    }
}
