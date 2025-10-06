package br.com.example.gerenciador.controller;

import br.com.example.gerenciador.dto.LoginRequest;
import br.com.example.gerenciador.dto.JWTResponse;
import br.com.example.gerenciador.model.Usuario;
import br.com.example.gerenciador.security.JWTTokenUtil;
import br.com.example.gerenciador.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest req) {
        Usuario probe = new Usuario();
        if (req.getUsernameOrEmail().contains("@")) {
            probe.setEmail(req.getUsernameOrEmail());
        } else {
            probe.setUsername(req.getUsernameOrEmail());
        }
        Usuario user = service.recuperarUsuario(probe);

        if (user != null && passwordEncoder.matches(req.getSenha(), user.getSenha())) {
            String accessToken = jwtTokenUtil.generateAccessToken(user.getUsername());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());
            return ResponseEntity.ok(new JWTResponse(accessToken, refreshToken));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTResponse> refresh(@RequestBody Map<String, String> body){
        String refreshToken = body.get("refreshToken");

        if (jwtTokenUtil.validateToken(refreshToken)){
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtTokenUtil.generateAccessToken(username);
            return ResponseEntity.ok(new JWTResponse(newAccessToken, refreshToken));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
    }
}
