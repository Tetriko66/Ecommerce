package cl.duoc.MicroServicioNotificaciones.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Intercepta excepciones lanzadas en cualquier controlador
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Maneja errores de negocio lanzados con ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        log.error("Error de negocio: {} - {}", ex.getStatusCode(), ex.getReason());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getReason());
        error.put("status", ex.getStatusCode().toString());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // Maneja errores de validación de campos del request (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Error de validación en los campos del request");
        Map<String, String> errores = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Maneja cualquier otro error inesperado del servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        log.error("Error interno del servidor: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("detalle", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
