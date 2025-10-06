package br.com.example.gerenciador.repository;

import br.com.example.gerenciador.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    public List<Cliente> findByNomeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrEnderecoContainingIgnoreCase(
            String nome, String telefone, String endereco
    );

    public Page<Cliente> findByStatusIn(List<String> status, Pageable pageable);
}
