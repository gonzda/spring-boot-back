package com.backend_spring.spring_back_test.Controller;

import com.backend_spring.spring_back_test.Models.User;
import com.backend_spring.spring_back_test.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // DTOs internos para validación
    public static class RegisterRequest {
        @NotBlank(message = "El campo nombre es requerido")
        @Size(min = 3, max = 30, message = "El campo nombre debe tener entre 3 y 30 caracteres")
        public String name;

        @Size(min = 3, max = 30, message = "El campo apellido debe tener entre 3 y 30 caracteres")
        public String lastName;

        @NotBlank(message = "El campo correo es requerido")
        @Email(message = "El campo correo debe ser un correo valido")
        @Size(max = 60, message = "El campo correo debe tener maximo 60 caracteres")
        public String email;

        @NotBlank(message = "El campo contraseña es requerido")
        @Size(min = 8, max = 60, message = "El campo contraseña debe tener entre 8 y 60 caracteres")
        public String password;
    }

    public static class LoginRequest {
        @NotBlank(message = "El campo correo es requerido")
        @Email(message = "El campo correo debe ser un correo valido")
        public String email;

        @NotBlank(message = "El campo contraseña es requerido")
        public String password;

        public Boolean rememberMe = false;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            Map<String, Object> error = new HashMap<>();
            error.put("email", new String[] { "El correo ya esta registrado" });
            return ResponseEntity.unprocessableEntity().body(Map.of("errors", error));
        }
        User user = new User();
        user.setName(request.name);
        user.setLastName(request.lastName);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setEstado("A");
        user.setRoles("USER");
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("msg", "Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password, userOpt.get().getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("msg", "Credenciales inválidas, por favor vuelva a intentarlo."));
        }
        User user = userOpt.get();
        if (!"A".equals(user.getEstado())) {
            return ResponseEntity.status(401)
                    .body(Map.of("msg", "La cuenta esta desactivada, por favor contacte al soporte"));
        }

        String apiToken = java.util.UUID.randomUUID().toString().replace("-", "") +
                java.util.Base64.getEncoder().encodeToString(user.getEmail().getBytes());

        return ResponseEntity.ok(Map.of(
                "msg", "Te has logeado como " + user.getRoles(),
                "access_token", apiToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Acabaste de cerrar sesion"));
    }
}
