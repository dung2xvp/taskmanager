package org.myapp.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.myapp.dto.board.CardCreateDto;
import org.myapp.dto.board.CardDto;
import org.myapp.dto.board.CardMoveDto;
import org.myapp.dto.board.CardUpdateDto;
import org.myapp.dto.board.CardCommentCreateDto;
import org.myapp.dto.board.CardCommentDto;
import org.myapp.service.CardService;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
public class CardController {

    @Inject
    CardService cardService;

    /**
     * Tạo Card mới trong một List
     * POST /lists/{listId}/cards
     */
    @POST
    @Path("/{listId}/cards")
    public Response createCard(@PathParam("listId") Long listId,
            @Valid CardCreateDto dto) {
        CardDto result = cardService.createCard(listId, dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Cập nhật thông tin Card
     * PUT /lists/cards/{cardId}
     */
    @PUT
    @Path("/cards/{cardId}")
    public CardDto updateCard(@PathParam("cardId") Long cardId,
            @Valid CardUpdateDto dto) {
        return cardService.updateCard(cardId, dto);
    }

    /**
     * Kéo thả Card sang List khác
     * PUT /lists/cards/{cardId}/move
     */
    @PUT
    @Path("/cards/{cardId}/move")
    public Response moveCard(@PathParam("cardId") Long cardId,
            @Valid CardMoveDto dto) {
        cardService.moveCard(cardId, dto);
        return Response.noContent().build();
    }

    /**
     * Xóa Card (soft delete)
     * DELETE /lists/cards/{cardId}
     */
    @DELETE
    @Path("/cards/{cardId}")
    public Response deleteCard(@PathParam("cardId") Long cardId) {
        cardService.deleteCard(cardId);
        return Response.noContent().build();
    }

    /**
     * Gắn nhãn cho Card
     */
    @POST
    @Path("/cards/{cardId}/labels/{labelId}")
    public Response addLabel(@PathParam("cardId") Long cardId, @PathParam("labelId") Long labelId) {
        cardService.addLabel(cardId, labelId);
        return Response.noContent().build();
    }

    /**
     * Gỡ nhãn khỏi Card
     */
    @DELETE
    @Path("/cards/{cardId}/labels/{labelId}")
    public Response removeLabel(@PathParam("cardId") Long cardId, @PathParam("labelId") Long labelId) {
        cardService.removeLabel(cardId, labelId);
        return Response.noContent().build();
    }

    /**
     * Phân công thành viên cho Card
     */
    @POST
    @Path("/cards/{cardId}/members/{userId}")
    public Response addMember(@PathParam("cardId") Long cardId, @PathParam("userId") Long userId) {
        cardService.addMember(cardId, userId);
        return Response.noContent().build();
    }

    /**
     * Gỡ thành viên khỏi Card
     */
    @DELETE
    @Path("/cards/{cardId}/members/{userId}")
    public Response removeMember(@PathParam("cardId") Long cardId, @PathParam("userId") Long userId) {
        cardService.removeMember(cardId, userId);
        return Response.noContent().build();
    }

    /**
     * Thêm Bình luận cho Card
     */
    @POST
    @Path("/cards/{cardId}/comments")
    public Response addComment(@PathParam("cardId") Long cardId, @Valid CardCommentCreateDto dto) {
        CardCommentDto result = cardService.addComment(cardId, dto);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Lấy danh sách Bình luận của Card
     */
    @GET
    @Path("/cards/{cardId}/comments")
    public java.util.List<CardCommentDto> getComments(@PathParam("cardId") Long cardId) {
        return cardService.getComments(cardId);
    }
}
