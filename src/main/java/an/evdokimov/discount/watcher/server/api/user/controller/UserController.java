package an.evdokimov.discount.watcher.server.api.user.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LogInResponse;
import an.evdokimov.discount.watcher.server.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
    }

    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public LogInResponse register(@RequestBody @Valid RegisterRequest request) throws ServerException {
        return userService.register(request);
    }

    @GetMapping(path = "hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String temp() {
        return "{\"hello\": \"hello!\"}";
    }
}
