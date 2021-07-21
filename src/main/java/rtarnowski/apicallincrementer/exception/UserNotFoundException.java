package rtarnowski.apicallincrementer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User with entered login does not exist")
public class UserNotFoundException extends RuntimeException {
}
