package net.pauljackals.springblog.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "error while importing server state")
public class StateImportException extends RuntimeException {
}
