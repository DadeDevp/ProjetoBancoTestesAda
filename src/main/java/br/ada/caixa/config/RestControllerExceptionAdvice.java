package br.ada.caixa.config;

import br.ada.caixa.dto.error.ErrorResponseDto;
import br.ada.caixa.exceptions.ValidacaoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponseDto>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ErrorResponseDto> erros = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> {
                    String fieldError = ((FieldError) objectError).getField();
                    String erro = objectError.getDefaultMessage();
                    return new ErrorResponseDto(fieldError, erro);
                })
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> handlerValidacaoException(ValidacaoException validacaoException) {
        log.info(validacaoException.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", validacaoException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler( Exception.class )
    public ResponseEntity<String> handlerException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
