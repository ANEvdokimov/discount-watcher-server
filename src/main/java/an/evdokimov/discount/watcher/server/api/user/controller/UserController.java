package an.evdokimov.discount.watcher.server.api.user.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.session.dto.response.LogInDtoResponse;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterDtoRequest;
import an.evdokimov.discount.watcher.server.api.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public LogInDtoResponse register(@RequestBody @Valid RegisterDtoRequest request) throws ServerException {
        return userService.register(request);
    }
}
