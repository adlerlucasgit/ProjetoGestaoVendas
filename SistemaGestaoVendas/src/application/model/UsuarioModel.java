package application.model;

public class UsuarioModel {
	private int id;
	private String nome;
	private String login;
	private String senha;
	private String tipo;

	public UsuarioModel(int id, String nome, String login, String senha, String tipo){
		this.id = id;
		this.nome = nome;
		this.login = login;
		this.senha = senha;
		this.tipo = tipo;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getIdFormatado() {
		return String.format("%06d", id);
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getLogin() {
		return this.login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getSenha() {
		return this.senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
