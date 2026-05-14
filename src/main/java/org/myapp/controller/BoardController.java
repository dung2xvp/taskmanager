package org.myapp.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.myapp.dto.board.BoardCreateDto;
import org.myapp.dto.board.BoardStateDto;
import org.myapp.dto.board.BoardSummaryDto;
import org.myapp.service.BoardQueryService;
import org.myapp.service.BoardService;

@Path("/boards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
public class BoardController {

    @Inject
    BoardService boardService;

    @Inject
    BoardQueryService boardQueryService;

    /**
     * UC05 — Tạo Board mới
     * POST /boards
     */
    @POST
    public Response createBoard(@Valid BoardCreateDto dto) {
        BoardSummaryDto result = boardService.createBoard(dto.name, dto.description);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * UC06 — Danh sách Board của user hiện tại
     * GET /boards/my
     */
    @GET
    @Path("/my")
    public List<BoardSummaryDto> getMyBoards() {
        return boardService.getMyBoards();
    }

    /**
     * UC07 — Xem chi tiết Board (toàn bộ Lists + Cards)
     * GET /boards/{id}/board-state
     */
    @GET
    @Path("/{id}/board-state")
    public BoardStateDto getBoardState(@PathParam("id") Long id) {
        return boardQueryService.getBoardState(id);
    }

    /**
     * UC09 — Xóa Board
     * DELETE /boards/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBoard(@PathParam("id") Long id) {
        boardService.deleteBoard(id);
        return Response.noContent().build();
    }
}
