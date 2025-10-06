package br.com.example.gerenciador.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

public class ClienteDTO {
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @NotNull(message = "Valor do empréstimo é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private Double valor;

    @NotNull(message = "Data do inicial é obrigatória")
    private LocalDate dataInicial;

    @NotNull(message = "Data de final é obrigatória")
    private LocalDate dataFinal;

    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCpf() {return cpf;}
    public void setCpf(String cpf) {this.cpf = cpf;}
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }
    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
