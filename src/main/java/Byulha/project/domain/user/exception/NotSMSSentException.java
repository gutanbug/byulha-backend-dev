package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSMSSentException extends LocalizedMessageException {

    public NotSMSSentException() {
        super(HttpStatus.BAD_REQUEST, "required.sms-sending");
    }
}
