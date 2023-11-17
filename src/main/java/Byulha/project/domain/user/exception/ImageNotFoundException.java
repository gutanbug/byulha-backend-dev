package Byulha.project.domain.user.exception;

import Byulha.project.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ImageNotFoundException extends LocalizedMessageException {

      public ImageNotFoundException(){
          super(HttpStatus.NOT_FOUND, "notfound.image");
      }
}
