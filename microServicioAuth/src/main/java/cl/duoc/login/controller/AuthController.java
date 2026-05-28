package cl.duoc.login.controller;

import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Indica que esta clase es un controlador REST
@RestController

// Define la ruta base del controlador
@RequestMapping("/api/v1/auth")

// Genera constructor con atributos final
@RequiredArgsConstructor
public class AuthController {

    // Inyecta el servicio de autenticación
    private final AuthService authService;

    // Define endpoint POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<DtoAuthResponse> login(

            // Recibe el JSON del body y valida sus campos
            @Valid @RequestBody DtoAuthRequest request) {

        // Llama al servicio para validar credenciales y generar token
        DtoAuthResponse response = authService.login(request);

        // Retorna HTTP 200 OK con el token
        return ResponseEntity.ok(response);
    }
}