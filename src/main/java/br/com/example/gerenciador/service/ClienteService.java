package br.com.example.gerenciador.service;

import br.com.example.gerenciador.dto.DashboardResumoDTO;
import br.com.example.gerenciador.model.Cliente;
import br.com.example.gerenciador.model.StatusCliente;
import br.com.example.gerenciador.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    );
    private static final long MAX_SIZE = 5L * 1024L * 1024L; // 5MB

    public Cliente save(Cliente cliente) {
        this.atualizarStatus(cliente);
        return repository.save(cliente);
    }

    public Page<Cliente> findAll(Pageable pageable){
        Page<Cliente> clientes = repository.findAll(pageable);
        clientes.forEach(this::atualizarStatus);
        return clientes;
    }

    public Cliente findById(int id){
        return repository.findById(id).orElse(null);
    }

    public void delete(int id){
        repository.deleteById(id);
    }

    public List<Cliente> search(String termo){
        return repository.findByNomeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrEnderecoContainingIgnoreCase(
                termo, termo, termo
        );
    }

    public Page<Cliente> findByStatusIn(List<String> status, Pageable pageable){
        Page<Cliente> clientes = repository.findByStatusIn(status, pageable);
        clientes.forEach(this::atualizarStatus);
        return clientes;
    }

    public DashboardResumoDTO getResumo(){
        List<Cliente> clientes = this.findAll();

        int total = clientes.size();
        double valorTotal = clientes.stream()
                .mapToDouble(Cliente::getValor)
                .sum();

        int pendentes = (int) clientes.stream()
                .filter(c -> c.getStatus() == StatusCliente.PENDENTE)
                .count();

        int emAtraso = (int) clientes.stream()
                .filter(c -> c.getStatus() == StatusCliente.ATRASADO)
                .count();

        int quitados = (int) clientes.stream()
                .filter(c -> c.getStatus() == StatusCliente.PAGO)
                .count();

        return new DashboardResumoDTO(total, valorTotal, pendentes, emAtraso, quitados);
    }

    @Transactional
    public Cliente marcarComoPago(int id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setStatus(StatusCliente.PAGO);
        repository.save(cliente);
        return cliente;
    }

    public void salvarFoto(int id, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()){
            throw new IllegalArgumentException("Arquivo vazio.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())){
            throw new IllegalArgumentException("Tipo de arquivo não permitido.");
        }
        if (file.getSize() > MAX_SIZE){
            throw new IllegalArgumentException("Arquivo excede o tamanho máximo de 5MB.");
        }

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        cliente.setFoto(file.getBytes());
        cliente.setFotoContentType(file.getContentType());

        repository.save(cliente);
    }

    public byte[] buscarFoto(int id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return cliente.getFoto();
    }

    public String buscarFotoContentType(int id){
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return cliente.getFotoContentType();
    }

    public void removerFoto(int id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        cliente.setFoto(null);
        cliente.setFotoContentType(null);
        repository.save(cliente);
    }

    private void atualizarStatus(Cliente cliente){
        if(cliente.getStatus() == StatusCliente.PAGO){
            return;
        }

        if(cliente.getDataFinal() != null){
            if(cliente.getDataFinal().isBefore(LocalDate.now())){
                cliente.setStatus(StatusCliente.ATRASADO);
            }else {
                cliente.setStatus(StatusCliente.PENDENTE);
            }
        }
        repository.save(cliente);
    }

    private List<Cliente> findAll(){
        List<Cliente> clientes = repository.findAll();
        clientes.forEach(this::atualizarStatus);
        return clientes;
    }
}
