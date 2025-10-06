package br.com.example.gerenciador.controller;

import br.com.example.gerenciador.dto.ClienteDTO;
import br.com.example.gerenciador.dto.DashboardResumoDTO;
import br.com.example.gerenciador.model.Cliente;
import br.com.example.gerenciador.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteDTO> criar(@Valid @RequestBody ClienteDTO dto) {
        Cliente salvo = service.save(fromDTO(dto));
        URI uri = URI.create("/api/v1/clientes/" + salvo.getId());
        return ResponseEntity.created(uri).body(toDTO(salvo));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> listar(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ClienteDTO> list = service.findAll(pageable).map(this::toDTO);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscar(@PathVariable int id) {
        Cliente c = service.findById(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(c));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClienteDTO>> search(@RequestParam("q") String termo){
        List<ClienteDTO> list = service.search(termo)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<ClienteDTO>> filtrarPorStatus(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> status){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Cliente> clientes;

        if (status == null || status.isEmpty()){
            clientes = service.findAll(pageable);
        }else {
            clientes = service.findByStatusIn(status, pageable);
        }

        Page<ClienteDTO> dtos = clientes.map(this::toDTO);

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable int id, @Valid @RequestBody ClienteDTO dto) {
        Cliente existente = service.findById(id);
        if (existente == null) return ResponseEntity.notFound().build();
        existente.setNome(dto.getNome());
        existente.setTelefone(dto.getTelefone());
        existente.setEndereco(dto.getEndereco());
        existente.setCpf(dto.getCpf());
        existente.setValor(dto.getValor());
        existente.setDataInicial(dto.getDataInicial());
        existente.setDataFinal(dto.getDataFinal());
        Cliente salvo = service.save(existente);
        return ResponseEntity.ok(toDTO(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        Cliente existente = service.findById(id);
        if (existente == null) return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> resumo(){
        return ResponseEntity.ok(service.getResumo());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<ClienteDTO> marcarComoPago(@PathVariable int id){
        ClienteDTO dto = toDTO(service.marcarComoPago(id));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<?> uploadFoto(@PathVariable int id, @RequestParam("file")MultipartFile file){
        try {
            service.salvarFoto(id, file);
            return ResponseEntity.ok("Foto salva com sucesso");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar foto");
        }
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> getFoto(@PathVariable int id){
        byte[] foto = service.buscarFoto(id);
        if (foto == null || foto.length == 0){
            return ResponseEntity.notFound().build();
        }
        String contentType = service.buscarFotoContentType(id);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            if (contentType != null) mediaType = MediaType.parseMediaType(contentType);
        }catch (Exception e){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(foto);
    }

    @DeleteMapping("/{id}/foto")
    public ResponseEntity<?> deleteFoto(@PathVariable int id){
        service.removerFoto(id);
        return ResponseEntity.ok().build();
    }

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setTelefone(c.getTelefone());
        dto.setEndereco(c.getEndereco());
        dto.setCpf(c.getCpf());
        dto.setValor(c.getValor());
        dto.setDataInicial(c.getDataInicial());
        dto.setDataFinal(c.getDataFinal());
        dto.setStatus(c.getStatus().toString());

        return dto;
    }

    private Cliente fromDTO(ClienteDTO dto) {
        Cliente c = new Cliente();
        if (dto.getId() != null) c.setId(dto.getId());
        c.setNome(dto.getNome());
        c.setTelefone(dto.getTelefone());
        c.setEndereco(dto.getEndereco());
        c.setCpf(dto.getCpf());
        c.setValor(dto.getValor());
        c.setDataInicial(dto.getDataInicial());
        c.setDataFinal(dto.getDataFinal());
        return c;
    }
}
