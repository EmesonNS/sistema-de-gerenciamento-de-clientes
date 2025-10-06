package br.com.example.gerenciador.dto;

public class UsuarioResponse {
    private Integer id;
    private String username;
    private String email;

    public UsuarioResponse() { }

    public UsuarioResponse(Integer id, String username, String email) {
        this.id = id; this.username = username; this.email = email;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
