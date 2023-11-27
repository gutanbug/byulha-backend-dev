package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSMSAuthorizedException extends LocalizedMessageException {

    public NotSMSAuthorizedException() {
        super(HttpStatus.FORBIDDEN, "required.sms-authorization");
    }
}
