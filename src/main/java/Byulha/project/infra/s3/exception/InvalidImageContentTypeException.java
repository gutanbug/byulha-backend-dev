package Byulha.project.infra.s3.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidImageContentTypeException extends LocalizedMessageException {
    public InvalidImageContentTypeException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "invalid.image-content-type");
    }
}
