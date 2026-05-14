package org.myapp.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.myapp.dto.board.BoardListCreateDto;
import org.myapp.dto.board.BoardListDto;
import org.myapp.service.BoardListService;

@Path("/boards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
public class BoardListController {

    @Inject
    BoardListService boardListService;

    /**
     * Tạo cột danh sách công việc mới trong board
     * POST /boards/{boardId}/lists
     */
    @POST
    @Path("/{boardId}/lists")
    public Response createList(@PathParam("boardId") Long boardId,
                               @Valid BoardListCreateDto dto) {
        BoardListDto result = boardListService.createList(boardId, dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Đổi tên cột
     * PUT /boards/{boardId}/lists/{listId}
     */
    @PUT
    @Path("/{boardId}/lists/{listId}")
    public BoardListDto renameList(@PathParam("boardId") Long boardId,
                                   @PathParam("listId") Long listId,
                                   @Valid BoardListCreateDto dto) {
        return boardListService.renameList(boardId, listId, dto);
    }

    /**
     * Xóa cột (archive)
     * DELETE /boards/{boardId}/lists/{listId}
     */
    @DELETE
    @Path("/{boardId}/lists/{listId}")
    public Response deleteList(@PathParam("boardId") Long boardId,
                               @PathParam("listId") Long listId) {
        boardListService.deleteList(boardId, listId);
        return Response.noContent().build();
    }
}
