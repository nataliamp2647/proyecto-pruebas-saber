package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.domain.user.AuthService;
import co.edu.unicauca.lisw2t5g02.domain.user.User;

/** MVC controller for authentication actions initiated by the login view. */
public final class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service=service; }
    public User login(String username,String password) { return service.authenticate(username,password); }
}
