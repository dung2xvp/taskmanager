package org.myapp.security.identity;

import jakarta.enterprise.context.RequestScoped;
import org.myapp.entity.User;

/**
 * Lưu thông tin User đang đăng nhập trong scope của 1 HTTP request.
 * Được set bởi AuthFilter sau khi verify JWT thành công.
 */
@RequestScoped
public class CurrentUser {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return user != null ? user.id : null;
    }

    public boolean isAdmin() {
        return user != null && user.systemRole.name().equals("ADMIN");
    }

    public boolean isAuthenticated() {
        return user != null;
    }
}
