package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class WrongSMSCodeException extends LocalizedMessageException {

    public WrongSMSCodeException(){
        super(HttpStatus.FORBIDDEN, "required.sms-authorization");
    }
}
