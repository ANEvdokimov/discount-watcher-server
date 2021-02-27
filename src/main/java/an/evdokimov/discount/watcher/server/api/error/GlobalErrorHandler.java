package an.evdokimov.discount.watcher.server.api.error;

import an.evdokimov.discount.watcher.server.api.error.dto.response.ServerErrorDtoResponse;
import an.evdokimov.discount.watcher.server.api.error.dto.response.ValidationErrorDtoResponse;
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
    public List<ValidationErrorDtoResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<ValidationErrorDtoResponse> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(new ValidationErrorDtoResponse(fieldError.getDefaultMessage(), fieldError.getField()))
        );
        return errors;
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerErrorDtoResponse handleValidation(ServerException exception) {
        return ServerErrorDtoResponse.builder()
                .message(exception.getMessage())
                .build();
    }
}
