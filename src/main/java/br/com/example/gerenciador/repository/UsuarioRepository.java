package br.com.example.gerenciador.repository;

import br.com.example.gerenciador.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public Usuario findByUsernameOrEmail(String username, String email);
}
