package Byulha.project.global.error;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ControllerAdvisor {

//    private final MessageSource messageSource;
//    private final ErrorLogService errorLogService;
//
//    @Value("${app.enable-test-controller}")
//    private boolean isEnabledTest;
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> localizedException(LocalizedMessageException e, Locale locale) {
//        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, e);
//        log.error("A problem has occurred in controller advice: [id={}]", dto.getTrackingId(), e);
//        return filter(e, ResponseEntity.status(e.getStatus()).body(dto));
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badParameter(HttpMessageNotReadableException e, Locale locale) {
//        return localizedException(new BadRequestException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badParameter(BindException e, Locale locale) {
//        return localizedException(new BindingFailedException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badParameter(MethodArgumentTypeMismatchException e, Locale locale) {
//        return localizedException(new BadRequestException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badParameter(MissingServletRequestParameterException e, Locale locale) {
//        return localizedException(new MissingRequiredParameterException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badMethod(HttpRequestMethodNotSupportedException e, Locale locale) {
//        return localizedException(new NotSupportedMethodException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> accessDenied(AccessDeniedException e, Locale locale) {
//        return localizedException(new NotGrantedException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> dataDuplicateException(DataIntegrityViolationException e, Locale locale) {
//        return localizedException(new DuplicateDataException(e), locale);
//    }
//
//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> unexpectedException(Exception e, Locale locale) {
//        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, LocalizedMessageException.of(e));
//        log.error("Unexpected exception has occurred in controller advice: [id={}]", dto.getTrackingId(), e);
//        return filter(e, ResponseEntity.internalServerError().body(dto));
//    }
//
//    private ResponseEntity<ErrorResponseDto> filter(Throwable t, ResponseEntity<ErrorResponseDto> entity) {
//        ErrorResponseDto dto = entity.getBody();
//        if (isEnabledTest && dto != null) {
//            errorLogService.logError(dto.getTrackingId(), t, dto);
//        }
//        return entity;
//    }
}
