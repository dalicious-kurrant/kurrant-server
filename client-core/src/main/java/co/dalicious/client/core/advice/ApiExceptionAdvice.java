package co.dalicious.client.core.advice;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import co.dalicious.client.core.dto.response.ErrorItemResponseDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final ApiException e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder()
                        .code(e.getError().getCode())
                        .message(e.getError().getMessage())
                        .build();

        return ResponseEntity.status(e.getError().getStatus()).body(List.of(errors));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final RuntimeException e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.UNCAUGHT_EXCEPTION.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.UNCAUGHT_EXCEPTION.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final MethodArgumentTypeMismatchException e) {

        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.UNPROCESSABLE_ENTITY.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.UNPROCESSABLE_ENTITY.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final UsernameNotFoundException e) {

        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.NOT_FOUND.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.NOT_FOUND.getStatus()).body(List.of(errors));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final MethodArgumentNotValidException e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.UNPROCESSABLE_ENTITY.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.UNPROCESSABLE_ENTITY.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final AccessDeniedException e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.FORBIDDEN.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.FORBIDDEN.getStatus()).body(List.of(errors));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final Exception e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder().code(ExceptionEnum.INTERNAL_SERVER_ERROR.getCode()).build();

        return ResponseEntity.status(ExceptionEnum.INTERNAL_SERVER_ERROR.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final IllegalArgumentException e) {
        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder()
                        .code(ExceptionEnum.BAD_REQUEST.getCode())
                        .message(e.getMessage())
                        .build();

        return ResponseEntity.status(ExceptionEnum.BAD_REQUEST.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final DataIntegrityViolationException e) {
        e.printStackTrace();

        // get the cause of the exception
        Throwable cause = e.getRootCause();
        if (cause == null) {
            cause = e.getCause();
        }

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder()
                        .code(ExceptionEnum.BAD_REQUEST.getCode())
                        .message(cause.getMessage())
                        .build();

        return ResponseEntity.status(ExceptionEnum.BAD_REQUEST.getStatus())
                .body(List.of(errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<List<ErrorItemResponseDto>> exceptionHandler(HttpServletRequest request,
                                                                       final MaxUploadSizeExceededException e) {

        e.printStackTrace();

        ErrorItemResponseDto errors =
                ErrorItemResponseDto.builder()
                        .code(ExceptionEnum.BAD_REQUEST.getCode())
                        .message(e.getMessage())
                        .build();

        return ResponseEntity.status(ExceptionEnum.BAD_REQUEST.getStatus())
                .body(List.of(errors));
    }

}
