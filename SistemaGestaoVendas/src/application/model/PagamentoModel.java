package application.model;

public class PagamentoModel {

    private String tipo;
    private double valor;

    public PagamentoModel(String tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }
}