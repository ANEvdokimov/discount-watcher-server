package an.evdokimov.discount.watcher.server.api.user.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.LoginRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LoginResponse;
import an.evdokimov.discount.watcher.server.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registration of new user.
     *
     * @param request information about registered user.
     * @return A token for authentication.
     * @throws ServerException any errors during user registration.
     */
    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse register(@RequestBody @Valid RegisterRequest request) throws ServerException {
        log.debug("registration user {}", request.getLogin());

        return userService.register(request);
    }

    /**
     * A user authentication.
     *
     * @param request A user's login and password.
     * @return A token for authentication.
     * @throws ServerException any errors during authentication.
     */
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) throws ServerException {
        log.debug("login user {}", request.getLogin());

        return userService.login(request);
    }

    @GetMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        log.info("hello!");

        return "{\"hello\": \"hello!\"}";
    }
}
