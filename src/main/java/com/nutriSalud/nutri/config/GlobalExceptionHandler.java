package com.nutriSalud.nutri.config;

import com.nutriSalud.nutri.dto.ApiResponse;
import com.nutriSalud.nutri.exceptions.EdadNoPermitidaException;
import com.nutriSalud.nutri.exceptions.ResourceConflictException;
import com.nutriSalud.nutri.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> data = new HashMap<>();
        data.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("recurso", ex.getResourceName());
        data.put(ex.getFieldName(), ex.getFieldValue());
        data.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleResourceConflict(ResourceConflictException ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("recurso", ex.getResourceName());
        data.put("motivo", ex.getReason());
        data.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(EdadNoPermitidaException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleEdadNoPermitida(EdadNoPermitidaException ex) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("recurso", ex.getRecurso());
        data.put("campo", ex.getCampo());
        data.put("edadCalculada", ex.getValorRechazado());
        data.put("edadMaximaPermitida", 17);
        data.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleCuerpoNoLegible(HttpMessageNotReadableException ex) {
        Map<String, String> data = new HashMap<>();
        data.put("mensaje", "El cuerpo de la solicitud es inválido o tiene formato incorrecto (JSON esperado)");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> data = new LinkedHashMap<>();
        String nombre = ex.getName();
        String tipo = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido";
        data.put("parametro", nombre);
        data.put("tipoEsperado", tipo);
        data.put("mensaje", String.format("El parámetro '%s' tiene un valor inválido para el tipo %s", nombre, tipo));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(data));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenerico(Exception ex) {
        String mensaje = "Error interno del servidor";
        if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
            mensaje = ex.getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(mensaje, 500));
    }
}
