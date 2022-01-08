package es.sralloza.lottery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class ServerError extends RuntimeException {
    public ServerError() {
        super();
    }

    public ServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerError(String message) {
        super(message);
    }

    public ServerError(Throwable cause) {
        super(cause);
    }
}
