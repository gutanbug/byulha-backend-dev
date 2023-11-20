package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotSupportedMethodException extends LocalizedMessageException {

    public NotSupportedMethodException(Throwable e){
        super(e, HttpStatus.BAD_REQUEST, "notsupport.http-method");
    }
}
