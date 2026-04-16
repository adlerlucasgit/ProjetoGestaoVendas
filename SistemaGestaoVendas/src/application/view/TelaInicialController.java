package application.view;

import application.dao.UsuarioDAO;
import application.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TelaInicialController {

    @FXML
    private Button btnEntrar;
	
    @FXML
    private ImageView ivLogo;
    
    @FXML
    private AnchorPane ap;

    @FXML
    private TextField txtSenha;

    @FXML
    private TextField txtUsuario;
    
    private Image logo;
    
    public void initialize() {
		logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
		ivLogo.setImage(logo);
		
		txtUsuario.setOnAction(e -> txtSenha.requestFocus());
		txtSenha.setOnAction(e -> Entrar());
		limparCampos();
    }
    
    public class Sessao {
        public static String tipoUsuario;
        public static String Usuario;
    }
    
    public void Entrar() {
    	String login = txtUsuario.getText();
    	String senha = txtSenha.getText();
    	
    	UsuarioDAO dao = new UsuarioDAO();
    	UsuarioModel logado = dao.autenticar(login, senha);
    	
    	String erro = "";
    	if(txtUsuario.getText().isBlank()) {
    		erro +="Usuário em branco";
    	}
    	if(txtSenha.getText().isBlank()) {
    		erro +="\nSenha em branco";
    	}
    	
    	if(logado != null) {
    		Sessao.tipoUsuario = logado.getTipo();
    		Sessao.Usuario = logado.getNome();
    		carregarTela("Sistema.fxml");

    	} else if(erro != "") {
    		Alert a = new Alert(Alert.AlertType.INFORMATION);
    		a.setTitle("Não foi possível entrar");
    		a.setHeaderText("Campos em branco");
    		a.setContentText(erro);
    		a.showAndWait();
    		limparCampos();
    	} else{
    		Alert a = new Alert(Alert.AlertType.INFORMATION);
    		a.setTitle("Não foi possível entrar");
    		a.setHeaderText("Usuário ou senha incorretos");
    		a.showAndWait();
    		limparCampos();
    	}
    	
    }
    
	public void carregarTela(String fxmlfile) {
		try {
			Parent fxml = FXMLLoader.load(getClass().getResource(fxmlfile));
			ap.getChildren().clear();
			ap.getChildren().add(fxml);
			
			AnchorPane.setTopAnchor(fxml,0.0);
			AnchorPane.setBottomAnchor(fxml,0.0);
			AnchorPane.setLeftAnchor(fxml,0.0);
			AnchorPane.setRightAnchor(fxml,0.0);
			Scene cena =  ap.getScene();
			
			if (cena!=null) {
				Stage stage = (Stage) cena.getWindow();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	public void limparCampos() {
		txtUsuario.clear();
		txtSenha.clear();
	}
	
}
