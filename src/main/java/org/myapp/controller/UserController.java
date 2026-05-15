package org.myapp.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.myapp.dto.auth.UserProfileDto;
import org.myapp.dto.auth.UserUpdateDto;
import org.myapp.service.UserService;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @GET
    @Path("/me")
    public UserProfileDto getMyProfile() {
        return userService.getMyProfile();
    }

    @PUT
    @Path("/me")
    public UserProfileDto updateProfile(@Valid UserUpdateDto dto) {
        return userService.updateProfile(dto);
    }
}
