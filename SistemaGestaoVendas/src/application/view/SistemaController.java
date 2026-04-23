package application.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SistemaController extends TelaInicialController{
	
    @FXML
    private ImageView ivLogo;
    private Image logo;
    

    @FXML
    private Button btnPDV;
    
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
	
    @FXML
    public void MovEstoque() {
        carregarTela("MovEstoque.fxml");
    }
    
    @FXML
    public void PDV() {
    	try {
    		FXMLLoader loader = new FXMLLoader(
    				getClass().getResource("Vendas.fxml"));
    		Parent root = loader.load();
    		VendasController controller = loader.getController();
    		Stage stage= new Stage();
    		stage.setScene(new Scene (root));
    		stage.show();
        } catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
}
