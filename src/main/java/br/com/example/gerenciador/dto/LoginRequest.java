package br.com.example.gerenciador.dto;

import jakarta.validation.constraints.*;

public class LoginRequest {
    @NotBlank(message = "Informe username ou e-mail")
    private String usernameOrEmail;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String v) { this.usernameOrEmail = v; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
