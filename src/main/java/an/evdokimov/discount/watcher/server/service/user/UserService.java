package an.evdokimov.discount.watcher.server.service.user;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.user.dto.request.LoginRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LoginResponse;

public interface UserService {
    LoginResponse register(RegisterRequest request) throws ServerException;

    LoginResponse login(LoginRequest request) throws ServerException;
}
