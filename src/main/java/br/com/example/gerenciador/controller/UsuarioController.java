package br.com.example.gerenciador.controller;

import br.com.example.gerenciador.dto.UsuarioRequest;
import br.com.example.gerenciador.dto.UsuarioResponse;
import br.com.example.gerenciador.model.Usuario;
import br.com.example.gerenciador.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest req) {
        Usuario u = new Usuario();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setSenha(req.getSenha());
        Usuario salvo = service.addNew(u);
        if (salvo == null) return ResponseEntity.badRequest().build();
        URI uri = URI.create("/api/v1/usuarios/" + salvo.getId());
        return ResponseEntity.created(uri).body(new UsuarioResponse(salvo.getId(), salvo.getUsername(), salvo.getEmail()));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(){
        List<UsuarioResponse> list = service.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
