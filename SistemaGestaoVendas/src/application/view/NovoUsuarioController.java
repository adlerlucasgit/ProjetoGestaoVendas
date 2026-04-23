package application.view;

import application.dao.UsuarioDAO;
import application.model.UsuarioModel;
import application.view.TelaInicialController.Sessao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class NovoUsuarioController extends TelaInicialController {

	@FXML
	private AnchorPane ap;

	@FXML
	private Button btnCadastrar;

    @FXML
    private RadioButton rdEstoquista;

    @FXML
    private RadioButton rdGerente;

    @FXML
    private ToggleGroup rdTipo;

    @FXML
    private RadioButton rdVendedor;

	@FXML
	private TextField txtLogin;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtSenha;

	@FXML
	private TableView<UsuarioModel> tvUsuarios;

	@FXML
	private TextField txtBuscar;

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnEditar;

	@FXML
	private Button btnExcluir;

	@FXML
	private TableColumn<UsuarioModel, Integer> colId;

	@FXML
	private TableColumn<UsuarioModel, String> colNome;

	@FXML
	private TableColumn<UsuarioModel, String> colTipo;

	@FXML
	private TableColumn<UsuarioModel, String> colUsuario;
	
    @FXML
    private ImageView ivLogo;
    private Image logo;    

	UsuarioModel usuario = new UsuarioModel(0, null, null, null, null);
	UsuarioDAO dao = new UsuarioDAO();
	private UsuarioModel usuarioSelecionado;

	@FXML
	public void Logout() {
		carregarTela("TelaInicial.fxml");
	}

	@FXML
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
	
	@FXML
	public void Clientes() {
		carregarTela("Clientes.fxml");
	}
	
    @FXML
    public void MovEstoque() {
        carregarTela("MovEstoque.fxml");
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
    
	public String FormatarID(int id) {
	    return String.format("%06d", id);
	}
	

	public void initialize() {
		logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
		ivLogo.setImage(logo);
		
		colId.setCellValueFactory(new PropertyValueFactory<>("idFormatado"));
		colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colUsuario.setCellValueFactory(new PropertyValueFactory<>("login"));
		colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

		carregarTabela(null);

		tvUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, usuario) -> {
			if (usuario != null) {
				txtNome.setText(usuario.getNome());
				txtLogin.setText(usuario.getLogin());
				
				String tipo = usuario.getTipo();
				
				if("VENDEDOR".equals(tipo)) {
					rdVendedor.setSelected(true);
				}else if("GERENTE".equals(tipo)) {
					rdGerente.setSelected(true);
				}else if("ESTOQUISTA".equals(tipo)) {
					rdEstoquista.setSelected(true);
				}

				usuarioSelecionado = usuario;
			}
		});
	}

	public void Cadastrar() {
		String nome = txtNome.getText();
		String usuario = txtLogin.getText();
		String senha = txtSenha.getText();
		RadioButton tipo = (RadioButton) rdTipo.getSelectedToggle();

		UsuarioModel u = new UsuarioModel(0, nome, usuario, senha, tipo.getText());
		UsuarioDAO dao = new UsuarioDAO();
		String erro = "";
		if (txtNome.getText().isBlank()) {
			erro += "Nome em branco";
		}
		if (txtLogin.getText().isBlank()) {
			erro += "\nNome de usuário em branco";
		}
		if (txtSenha.getText().isBlank()) {
			erro += "\nSenha em branco";
		}

		if (txtNome.getText().isBlank() || txtLogin.getText().isBlank() || txtSenha.getText().isBlank()) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("Erro ao cadastrar usuário");
			a.setHeaderText("Campos em branco");
			a.setContentText(erro);
			a.showAndWait();
		} else if (dao.loginExiste(txtLogin.getText())) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Erro ao cadastrar usuário");
			a.setHeaderText("Nome de usuário já está em uso");
			a.showAndWait();
		} else {
			if (dao.inserirUsuario(u)) {
				Alert a = new Alert(Alert.AlertType.INFORMATION);
				a.setTitle("Sucesso!");
				a.setHeaderText("Usuário cadastrado");
				a.showAndWait();
				carregarTabela(null);
				limparCampos();
			}
		}

	}
	
    @FXML
    public void Editar() {
        if (usuarioSelecionado == null) {
            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.setContentText("Nenhum usuario selecionado para edição!");
            aviso.showAndWait();
            return;
        } else {

            // Atualiza os atributos do objeto com os valores dos campos
        	usuarioSelecionado.setNome(txtNome.getText());
        	usuarioSelecionado.setLogin(txtLogin.getText());
            
			String tipo = usuarioSelecionado.getTipo();

			if (rdVendedor.isSelected()) {
			    tipo = "VENDEDOR";
			} else if (rdGerente.isSelected()) {
			    tipo = "GERENTE";
			} else if(rdEstoquista.isSelected()){
			    tipo = "ESTOQUISTA";
			}

			usuarioSelecionado.setTipo(tipo);
			dao.Editar(usuarioSelecionado);

            // Recarrega a tabela para refletir a edição
            carregarTabela(null);
            limparCampos();

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setContentText("Produto editado com sucesso!");
            sucesso.showAndWait();
        }
    }

	@FXML
	public void Pesquisar() {
		// usa o on action do botao pesquisar
		if (!txtBuscar.getText().isEmpty()) {
			usuario = dao.Buscar(txtBuscar.getText());
			carregarTabela(txtBuscar.getText());

			txtNome.setText(usuario.getNome());
			txtLogin.setText(usuario.getLogin());
			
			String tipo = usuario.getTipo();
			
			if("VENDEDOR".equals(tipo)) {
				rdVendedor.setSelected(true);
			}else if("GERENTE".equals(tipo)) {
				rdGerente.setSelected(true);
			}else if("ESTOQUISTA".equals(tipo)) {
				rdEstoquista.setSelected(true);
			}

			usuarioSelecionado = usuario;
		} else {
			Alert mensagem = new Alert(Alert.AlertType.ERROR);
			mensagem.setContentText("Campo em branco!");
			mensagem.showAndWait();
			carregarTabela(null);
			limparCampos();
		}

	}
	
	@FXML
	public void Excluir() {
        if (usuarioSelecionado == null) {
            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.setContentText("Nenhum usuário selecionado para excluir!");
            aviso.showAndWait();
            return;
        } else {
            dao.Excluir(usuarioSelecionado);

            carregarTabela(null);
            limparCampos();
        }
	}

	public void limparCampos() {
		txtNome.clear();
		txtLogin.clear();
		txtSenha.clear();
	}

	public void carregarTabela(String valor) {
		ObservableList<UsuarioModel> lista = FXCollections.observableArrayList(UsuarioDAO.listarTodos(valor));

		tvUsuarios.setItems(lista);
	}

}
