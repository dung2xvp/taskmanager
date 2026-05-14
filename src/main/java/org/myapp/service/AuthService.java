package org.myapp.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.myapp.dao.UserDao;
import org.myapp.dto.user.AuthResponseDto;
import org.myapp.dto.user.LoginRequestDto;
import org.myapp.dto.user.UserCreateRequestDto;
import org.myapp.entity.SystemRole;
import org.myapp.entity.User;
import org.myapp.security.jwt.JwtService;

@ApplicationScoped
public class AuthService {

    private final UserDao userDao;
    private final JwtService jwtService;

    public AuthService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDto register(UserCreateRequestDto dto) {
        if (userDao.existsByUsername(dto.username)) {
            throw new BadRequestException("Username already taken");
        }
        if (userDao.existsByEmail(dto.email)) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.username = dto.username;
        user.email = dto.email;
        user.fullName = dto.fullName;
        user.passwordHash = BcryptUtil.bcryptHash(dto.password);
        user.systemRole = SystemRole.USER;
        user.active = true;

        userDao.persist(user);

        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token, user.id, user.username, user.fullName, user.systemRole);
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userDao.findByUsername(dto.username)
                .orElseThrow(() -> new NotAuthorizedException("Invalid username or password"));

        if (!user.active) {
            throw new NotAuthorizedException("Account is locked");
        }

        if (!BcryptUtil.matches(dto.password, user.passwordHash)) {
            throw new NotAuthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token, user.id, user.username, user.fullName, user.systemRole);
    }
}
