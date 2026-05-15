package org.myapp.controller;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.myapp.dao.UserDao;
import org.myapp.dto.auth.AdminUserDto;
import org.myapp.entity.SystemRole;
import org.myapp.entity.User;
import org.myapp.security.identity.CurrentUser;

import java.util.List;
import java.util.stream.Collectors;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminController {

    @Inject
    UserDao userDao;

    @Inject
    org.myapp.dao.BoardDao boardDao;

    @Inject
    CurrentUser currentUser;

    private void checkAdmin() {
        User user = currentUser.getUser();
        if (user == null || user.systemRole != SystemRole.ADMIN) {
            throw new ForbiddenException("Chỉ Admin mới có quyền truy cập!");
        }
    }

    @GET
    @Path("/stats")
    public org.myapp.dto.auth.AdminStatsDto getStats() {
        checkAdmin();
        org.myapp.dto.auth.AdminStatsDto dto = new org.myapp.dto.auth.AdminStatsDto();
        dto.totalUsers = userDao.count();
        dto.totalBoards = boardDao.count("archived = false");
        dto.activeUsers = userDao.count("active = true");
        return dto;
    }

    @GET
    @Path("/boards")
    public List<org.myapp.dto.auth.AdminBoardDto> getAllBoards() {
        checkAdmin();
        return boardDao.list("archived = false").stream().map(b -> {
            org.myapp.dto.auth.AdminBoardDto dto = new org.myapp.dto.auth.AdminBoardDto();
            dto.id = b.id;
            dto.name = b.name;
            dto.ownerName = b.owner != null ? b.owner.fullName : "N/A";
            dto.visibility = b.visibility != null ? b.visibility.name() : "PRIVATE";
            dto.createdAt = b.createdAt;
            return dto;
        }).collect(Collectors.toList());
    }

    @GET
    @Path("/users")
    public List<AdminUserDto> getAllUsers() {
        checkAdmin();
        return userDao.listAll().stream().map(u -> {
            AdminUserDto dto = new AdminUserDto();
            dto.id = u.id;
            dto.username = u.username;
            dto.email = u.email;
            dto.fullName = u.fullName;
            dto.active = u.active;
            dto.systemRole = u.systemRole.name();
            dto.createdAt = u.createdAt;
            return dto;
        }).collect(Collectors.toList());
    }

    @PUT
    @Path("/users/{id}/toggle-active")
    @Transactional
    public Response toggleActive(@PathParam("id") Long id) {
        checkAdmin();
        User u = userDao.findById(id);
        if (u == null) throw new BadRequestException("Không tìm thấy người dùng");
        
        // Không cho tự khóa chính mình
        if (u.id.equals(currentUser.getUser().id)) {
            throw new BadRequestException("Bạn không thể tự khóa chính mình!");
        }

        u.active = !u.active;
        userDao.persist(u);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/boards/{id}")
    @Transactional
    public Response deleteBoard(@PathParam("id") Long id) {
        checkAdmin();
        org.myapp.entity.Board b = boardDao.findById(id);
        if (b == null) throw new BadRequestException("Không tìm thấy bảng");
        
        b.archived = true; // Soft delete
        boardDao.persist(b);
        return Response.noContent().build();
    }
}
