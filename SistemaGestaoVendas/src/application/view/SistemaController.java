package application.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SistemaController extends TelaInicialController{
	
    @FXML
    private ImageView ivLogo;
    private Image logo;
    
    @FXML
    private Label lblBemVindo;
    
	public void initialize() {
		lblBemVindo.setText("Bem vindo "+Sessao.Usuario);	
		logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
		ivLogo.setImage(logo);
	}
	
	public void Logout() {
		carregarTela("TelaInicial.fxml");
	}
	
	public void Sair() {
    	System.exit(0);
	}
	
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
	
	public void Clientes() {
		carregarTela("Clientes.fxml");
	}
	
	@FXML
	public void Produtos() {
		if(Sessao.tipoUsuario.equals("ESTOQUISTA") || Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("Produtos.fxml");
		}else {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
    		a.setHeaderText("Acesso negado");
			a.setContentText("Tela indisponível para vendedores");
			a.showAndWait();
			return;
		}
	}
	
	
	
}
