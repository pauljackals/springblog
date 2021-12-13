package net.pauljackals.springblog.controller.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "error while exporting server state")
public class StateExportException extends RuntimeException {
}
