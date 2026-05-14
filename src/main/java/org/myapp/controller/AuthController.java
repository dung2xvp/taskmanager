package org.myapp.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.myapp.dto.user.AuthResponseDto;
import org.myapp.dto.user.LoginRequestDto;
import org.myapp.dto.user.UserCreateRequestDto;
import org.myapp.entity.User;
import org.myapp.security.identity.CurrentUser;
import org.myapp.service.AuthService;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private final AuthService authService;

    @Inject
    CurrentUser currentUser;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * UC01 - Đăng ký tài khoản mới
     * POST /auth/register
     */
    @POST
    @Path("/register")
    public Response register(@Valid UserCreateRequestDto dto) {
        AuthResponseDto response = authService.register(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    /**
     * UC02 - Đăng nhập
     * POST /auth/login
     */
    @POST
    @Path("/login")
    public AuthResponseDto login(LoginRequestDto dto) {
        return authService.login(dto);
    }

    /**
     * Kiểm tra token còn hợp lệ không, trả về thông tin user hiện tại
     * GET /auth/me  (yêu cầu token hợp lệ)
     */
    @GET
    @Path("/me")
    @SecurityRequirement(name = "SecurityScheme")
    public Response me() {
        User user = currentUser.getUser();
        return Response.ok(new AuthResponseDto(
                null,
                user.id,
                user.username,
                user.fullName,
                user.systemRole
        )).build();
    }
}
