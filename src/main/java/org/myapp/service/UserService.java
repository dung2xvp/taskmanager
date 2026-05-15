package org.myapp.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.myapp.dao.UserDao;
import org.myapp.dto.auth.UserProfileDto;
import org.myapp.dto.auth.UserUpdateDto;
import org.myapp.entity.User;
import org.myapp.security.identity.CurrentUser;

@ApplicationScoped
public class UserService {

    @Inject
    UserDao userDao;

    @Inject
    CurrentUser currentUser;

    public UserProfileDto getMyProfile() {
        User user = currentUser.getUser();
        return toDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(UserUpdateDto dto) {
        User user = currentUser.getUser();
        user.fullName = dto.fullName.trim();
        userDao.persist(user);
        return toDto(user);
    }

    private UserProfileDto toDto(User u) {
        UserProfileDto dto = new UserProfileDto();
        dto.id = u.id;
        dto.username = u.username;
        dto.email = u.email;
        dto.fullName = u.fullName;
        dto.createdAt = u.createdAt;
        return dto;
    }
}
