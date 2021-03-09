package an.evdokimov.discount.watcher.server.api.error;

import an.evdokimov.discount.watcher.server.api.error.dto.response.ServerErrorResponse;
import an.evdokimov.discount.watcher.server.api.error.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<ValidationErrorResponse> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(new ValidationErrorResponse(fieldError.getDefaultMessage(), fieldError.getField()))
        );
        return errors;
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerErrorResponse handleValidation(ServerException exception) {
        return ServerErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }
}
