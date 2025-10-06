package br.com.example.gerenciador.service;

import br.com.example.gerenciador.model.Usuario;
import br.com.example.gerenciador.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario addNew(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    public List<Usuario> findAll(){
        return repository.findAll();
    }

    public Usuario recuperarUsuario(Usuario usuario){
        return repository.findByUsernameOrEmail(usuario.getUsername(), usuario.getEmail());
    }
}