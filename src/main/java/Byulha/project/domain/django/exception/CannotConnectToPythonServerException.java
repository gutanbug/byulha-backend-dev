package Byulha.project.domain.django.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotConnectToPythonServerException extends LocalizedMessageException {

    public CannotConnectToPythonServerException () {
        super(HttpStatus.BAD_REQUEST, "failed.connection");
    }
}
