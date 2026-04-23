package application.model;

public class ClienteModel {
	private int id;
	private String nome;
	private String cpf_cnpj;
	private String email;
	private String status;

	public ClienteModel(int id, String nome, String cpf_cnpj, String email, String status){
		this.id = id;
		this.nome = nome;
		this.cpf_cnpj = cpf_cnpj;
		this.email = email;
		this.status = status;
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
	
	public String getCpfCnpj() {
		return this.cpf_cnpj;
	}
	public void setCpfCnpj(String cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}
	
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
	    return nome + " (" + cpf_cnpj + ")";
	}
}
