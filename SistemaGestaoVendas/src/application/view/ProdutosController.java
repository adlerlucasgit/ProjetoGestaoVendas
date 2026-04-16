package application.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class ProdutosController extends TelaInicialController{
	@FXML
	public void Logout() {
		carregarTela("TelaInicial.fxml");
	}
	
	@FXML
	public void Sair() {
		System.exit(0);
	}
	
	@FXML
	public void Usuarios() {
		if(Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("NovoUsuario.fxml");
		}else {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
    		a.setHeaderText("Acesso negado");
			a.setContentText("Tela indisponível para vendedores e estoquistas");
			a.showAndWait();
			return;
		}
	}
	
	@FXML
	public void Clientes() {
		carregarTela("Clientes.fxml");
	}
	
	@FXML
	public void Produtos() {
		if(Sessao.tipoUsuario.equals("ESTOQUISTA") || Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("Produtos.fxml");
		}else {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setContentText("Tela indisponível para vendedores");
			a.showAndWait();
			return;
		}
	}
	
    @FXML
    public void Voltar() {
        carregarTela("Sistema.fxml");
    }
	
	public void initialize() {
		
	}
}
