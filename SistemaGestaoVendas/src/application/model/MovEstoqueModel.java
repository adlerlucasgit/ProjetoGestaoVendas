package application.model;

public class MovEstoqueModel {
	private int id;
	private int idProd;
	private int idUser;
	private String nomeProd;
	private String data;
	private int quantidade;
	private String tipo;
	
	public MovEstoqueModel(int id, int idProd, int idUser, String nomeProd, String data, int quantidade, String tipo) {
		this.id = id;
		this.idProd = idProd;
		this.idUser = idUser;
		this.nomeProd = nomeProd;
		this.data = data;
		this.quantidade = quantidade;
		this.tipo = tipo;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getIdFormatado() {
	    return String.format("%06d", this.id);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getIdProd() {
		return this.idProd;
	}
	
	public String getIdFormatadoProd() {
	    return String.format("%06d", this.idProd);
	}
	
	public void setIdProd(int idProd) {
		this.idProd = idProd;
	}
	
	public String getIdUser() {
	    return String.format("%06d", this.idUser);
	}
	
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	public String getNomeProd() {
		return this.nomeProd;
	}
	
	public void setNomeProd(String nomeProd) {
		this.nomeProd = nomeProd;
	}
	
	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public int getQuantidade() {
		return this.quantidade;
	}
	
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getTipo() {
		return this.tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	

}