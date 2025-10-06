package br.com.example.gerenciador.dto;

public class DashboardResumoDTO {
    private int totalClientes;
    private double valorTotal;
    private int pendentes;
    private int emAtraso;
    private int quitados;

    public DashboardResumoDTO(int totalClientes, double valorTotal, int pendentes, int emAtraso, int quitados){
        this.totalClientes = totalClientes;
        this.valorTotal = valorTotal;
        this.pendentes = pendentes;
        this.emAtraso = emAtraso;
        this.quitados = quitados;
    }

    public int getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(int totalClientes) {
        this.totalClientes = totalClientes;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public int getPendentes() {
        return pendentes;
    }

    public void setPendentes(int pendentes) {
        this.pendentes = pendentes;
    }

    public int getEmAtraso() {
        return emAtraso;
    }

    public void setEmAtraso(int emAtraso) {
        this.emAtraso = emAtraso;
    }

    public int getQuitados() {
        return quitados;
    }

    public void setQuitados(int quitados) {
        this.quitados = quitados;
    }
}
