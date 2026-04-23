package application.model;

public class ProdutoModel {
	private int id;
	private String nome;
	private String codBarras;
	private String descricao;
	private double precoCusto;
	private double custoVenda;
	private int estoque;
	private int estoqueMin;
	private String categoria;

	public ProdutoModel(int id, String nome, String codBarras, String descricao, double precoCusto, double custoVenda,
			int estoque, int estoqueMin, String categoria) {
		this.id = id;
		this.nome = nome;
		this.codBarras = codBarras;
		this.descricao = descricao;
		this.precoCusto = precoCusto;
		this.custoVenda = custoVenda;
		this.estoque = estoque;
		this.estoqueMin = estoqueMin;
		this.categoria = categoria;
	}

	public int getId() {
		return id;
	}
	
	public String getIdFormatado() {
		return String.format("%06d", id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getPrecoCusto() {
		return precoCusto;
	}

	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}

	public double getCustoVenda() {
		return custoVenda;
	}

	public void setCustoVenda(double custoVenda) {
		this.custoVenda = custoVenda;
	}

	public int getEstoque() {
		return estoque;
	}

	public void setEstoque(int estoque) {
		this.estoque = estoque;
	}

	public int getEstoqueMin() {
		return estoqueMin;
	}

	public void setEstoqueMin(int estoqueMin) {
		this.estoqueMin = estoqueMin;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
}
