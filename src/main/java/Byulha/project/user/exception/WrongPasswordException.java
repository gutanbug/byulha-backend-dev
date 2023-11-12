package Byulha.project.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class WrongPasswordException extends LocalizedMessageException {

    public WrongPasswordException(){
        super(HttpStatus.BAD_REQUEST, "invalid.password");
    }
}
