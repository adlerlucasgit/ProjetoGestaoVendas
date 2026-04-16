package application.model;

import java.text.NumberFormat;
import java.util.Locale;

public class VendaItemModel {

    private int id;
    private int vendaId;
    private int produtoId;
    private String produto;
    private int quantidade;
    private double preco;

    public VendaItemModel(int id, int vendaId, int produtoId, String produto, int quantidade, double preco) {
        this.id = id;
        this.vendaId = vendaId;
        this.produtoId = produtoId;
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public int getId() { return id; }

    public int getVendaId() { return vendaId; }

    public int getProdutoId() { return produtoId; }

    public String getProduto() { return produto; }

    public int getQuantidade() { return quantidade; }

    public double getPreco() { return preco; }

    public double getSubtotal() {
        return quantidade * preco;
    }

    public String getPrecoFormatado() {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(preco);
    }

    public String getSubtotalFormatado() {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(getSubtotal());
    }
}