package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class PerfumeCategoryNotFoundException extends LocalizedMessageException {

    public PerfumeCategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.perfume-category");
    }
}
