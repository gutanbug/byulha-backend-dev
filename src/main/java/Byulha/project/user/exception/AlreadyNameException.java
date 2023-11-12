package Byulha.project.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyNameException extends LocalizedMessageException {

    public AlreadyNameException(){
        super(HttpStatus.BAD_REQUEST, "already.name");
    }
}
