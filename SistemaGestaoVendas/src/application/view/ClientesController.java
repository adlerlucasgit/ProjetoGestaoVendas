package application.view;

import java.time.LocalDate;

import application.dao.ClienteDAO;
import application.dao.UsuarioDAO;
import application.model.ClienteModel;
import application.view.TelaInicialController.Sessao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientesController extends TelaInicialController {
	@FXML
	private AnchorPane ap;

	@FXML
	private Button btnAtivarDesativar;
	
	@FXML
	private Button btnCadastrar;

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnEditar;

	@FXML
	private Button btnExcluir;

	@FXML
	private TableColumn<ClienteModel, Integer> colId;

	@FXML
	private TableColumn<ClienteModel, String> colNome;

	@FXML
	private TableColumn<ClienteModel, String> colCpfCnpj;

	@FXML
	private TableColumn<ClienteModel, String> colEmail;

	@FXML
	private TableColumn<ClienteModel, String> colStatus;

	@FXML
	private TableView<ClienteModel> tvClientes;

	@FXML
	private TextField txtBuscar;

	@FXML
	private TextField txtCpfCnpj;

	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtNome;
	
    @FXML
    private ImageView ivLogo;
    private Image logo;

	public ClienteModel cliente = new ClienteModel(0, null, null, null, null);
	ClienteDAO dao = new ClienteDAO();
	private ClienteModel clienteSelecionado;

	@FXML
	public void Logout() {
		carregarTela("TelaInicial.fxml");
	}

	@FXML
	public void Sair() {
		System.exit(0);
	}
	
    @FXML
    public void MovEstoque() {
		if(Sessao.tipoUsuario.equals("ESTOQUISTA") || Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("MovEstoque.fxml");
		}else {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
    		a.setHeaderText("Acesso negado");
			a.setContentText("Tela indisponível para vendedores");
			a.showAndWait();
			return;
		}
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
		colCpfCnpj.setCellValueFactory(new PropertyValueFactory<>("cpfCnpj"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

		carregarTabela(null);

		tvClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, cliente) -> {
			if (cliente != null) {
				txtNome.setText(cliente.getNome());
				txtCpfCnpj.setText(cliente.getCpfCnpj());
				txtEmail.setText(cliente.getEmail());
				cliente.getStatus();

				clienteSelecionado = cliente;
			}
		});
	}

	public void Cadastrar() {
		String nome = txtNome.getText();
		String cpfCnpj = txtCpfCnpj.getText();
		String email = txtEmail.getText();
		String status = "ATIVO";

		ClienteModel c = new ClienteModel(0, nome, cpfCnpj, email, status);
		ClienteDAO dao = new ClienteDAO();
		String erro = "";
		if (txtNome.getText().isBlank()) {
			erro += "Nome em branco";
		}
		if (txtCpfCnpj.getText().isBlank()) {
			erro += "\nCPF/CNPJ em branco";
		}
		if (txtEmail.getText().isBlank()) {
			erro += "\nE-mail em branco";
		}

		if (txtNome.getText().isBlank() || txtCpfCnpj.getText().isBlank() || txtEmail.getText().isBlank()) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("Erro ao cadastrar usuário");
			a.setHeaderText("Campos em branco");
			a.setContentText(erro);
			a.showAndWait();
		} else {
			if(dao.inserirCliente(c)) {
			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Sucesso!");
			a.setHeaderText("Cliente cadastrado");
			a.showAndWait();
			carregarTabela(null);
			limparCampos();
			} else {
			    if (!validarCpfOuCnpj(cpfCnpj)) {
			        Alert a = new Alert(Alert.AlertType.ERROR);
			        a.setTitle("Erro");
			        a.setHeaderText("CPF/CNPJ inválido");
			        a.setContentText("Digite um CPF ou CNPJ válido!");
			        a.showAndWait();
			        return;
			    }
				
				if (dao.emailExiste(txtEmail.getText()) && dao.CpfCnpjExiste(txtCpfCnpj.getText())) {
					Alert a = new Alert(Alert.AlertType.INFORMATION);
					a.setTitle("Erro ao cadastrar cliente");
					a.setHeaderText("E-mail e CPF/CNPJ já está em uso");
					a.showAndWait();
				} else if (dao.CpfCnpjExiste(txtCpfCnpj.getText())) {
					Alert a = new Alert(Alert.AlertType.INFORMATION);
					a.setTitle("Erro ao cadastrar cliente");
					a.setHeaderText("CPF/CNPJ já está em uso");
					a.showAndWait();
				} else if (dao.emailExiste(txtEmail.getText())) {
					Alert a = new Alert(Alert.AlertType.INFORMATION);
					a.setTitle("Erro ao cadastrar cliente");
					a.setHeaderText("E-mail já está em uso");
					a.showAndWait();
				}
			}
		}

	}
	
	public static boolean validarCPF(String cpf) {
	    cpf = cpf.replaceAll("[^0-9]", "");

	    if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

	    try {
	        int soma = 0;
	        for (int i = 0; i < 9; i++) {
	            soma += (cpf.charAt(i) - '0') * (10 - i);
	        }

	        int dig1 = 11 - (soma % 11);
	        dig1 = (dig1 >= 10) ? 0 : dig1;

	        soma = 0;
	        for (int i = 0; i < 10; i++) {
	            soma += (cpf.charAt(i) - '0') * (11 - i);
	        }

	        int dig2 = 11 - (soma % 11);
	        dig2 = (dig2 >= 10) ? 0 : dig2;

	        return dig1 == (cpf.charAt(9) - '0') &&
	               dig2 == (cpf.charAt(10) - '0');

	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static boolean validarCNPJ(String cnpj) {
	    cnpj = cnpj.replaceAll("[^0-9]", "");

	    if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

	    try {
	        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
	        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

	        int soma = 0;
	        for (int i = 0; i < 12; i++) {
	            soma += (cnpj.charAt(i) - '0') * peso1[i];
	        }

	        int dig1 = soma % 11;
	        dig1 = (dig1 < 2) ? 0 : 11 - dig1;

	        soma = 0;
	        for (int i = 0; i < 13; i++) {
	            soma += (cnpj.charAt(i) - '0') * peso2[i];
	        }

	        int dig2 = soma % 11;
	        dig2 = (dig2 < 2) ? 0 : 11 - dig2;

	        return dig1 == (cnpj.charAt(12) - '0') &&
	               dig2 == (cnpj.charAt(13) - '0');

	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static boolean validarCpfOuCnpj(String valor) {
	    valor = valor.replaceAll("[^0-9]", "");

	    if (valor.length() == 11) {
	        return validarCPF(valor);
	    } else if (valor.length() == 14) {
	        return validarCNPJ(valor);
	    }
	    return false;
	}

	@FXML
	public void Editar() {
		if (clienteSelecionado == null) {
			Alert aviso = new Alert(Alert.AlertType.ERROR);
			aviso.setContentText("Nenhum cliente selecionado para edição!");
			aviso.showAndWait();
			return;
		} else {
			
			if (!validarCpfOuCnpj(txtCpfCnpj.getText())) {
			    Alert aviso = new Alert(Alert.AlertType.ERROR);
			    aviso.setContentText("CPF/CNPJ inválido!");
			    aviso.showAndWait();
			    return;
			}
			
			clienteSelecionado.setNome(txtNome.getText());
			clienteSelecionado.setCpfCnpj(txtCpfCnpj.getText());
			clienteSelecionado.setEmail(txtEmail.getText());

			dao.Editar(clienteSelecionado);

			carregarTabela(null);
			limparCampos();

			Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
			sucesso.setContentText("Cliente editado com sucesso!");
			sucesso.showAndWait();
		}
	}

	@FXML
	public void Pesquisar() {
		if (!txtBuscar.getText().isEmpty()) {
			cliente = dao.Buscar(txtBuscar.getText());
			carregarTabela(txtBuscar.getText());
			
			if(cliente == null) {
				carregarTabela(null);
				return;
			} else {
				txtNome.setText(cliente.getNome());
				txtCpfCnpj.setText(cliente.getCpfCnpj());
				txtEmail.setText(cliente.getEmail());

				clienteSelecionado = cliente;	
			}
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
		if (clienteSelecionado == null) {
			Alert aviso = new Alert(Alert.AlertType.ERROR);
			aviso.setContentText("Nenhum cliente selecionado para excluir!");
			aviso.showAndWait();
			return;
		} else {
			dao.Excluir(clienteSelecionado);

			carregarTabela(null);
			limparCampos();
		}
	}

	@FXML
	public void AtivarDesativar() {
		if (clienteSelecionado == null) {
			Alert aviso = new Alert(Alert.AlertType.ERROR);
			aviso.setContentText("Nenhum cliente selecionado para mudar status!");
			aviso.showAndWait();
			return;
		}

		if (clienteSelecionado.getStatus().equals("ATIVO")) {
			dao.ativaDesativa(clienteSelecionado);
			Alert aviso = new Alert(Alert.AlertType.INFORMATION);
			aviso.setContentText("Status atualizado para inativo!");
			aviso.showAndWait();
			carregarTabela(null);
		} else if(clienteSelecionado.getStatus().equals("INATIVO")) {
			dao.ativaDesativa(clienteSelecionado);
			Alert aviso = new Alert(Alert.AlertType.INFORMATION);
			aviso.setContentText("Status atualizado para ativo!");
			aviso.showAndWait();
			carregarTabela(null);
		}

	}
	
	@FXML 
	public void Historico() {

	    if (clienteSelecionado == null) {
	        Alert a = new Alert(Alert.AlertType.ERROR);
	        a.setContentText("Nenhum cliente selecionado");
	        a.showAndWait();
	        return;
	    }

	    LocalDate hoje = LocalDate.now();
	    LocalDate primeiroDia = hoje.withDayOfMonth(1);
	    LocalDate ultimoDia = hoje.withDayOfMonth(hoje.lengthOfMonth());

	    try {
	        FXMLLoader loader = new FXMLLoader(
	                getClass().getResource("HistoricoCliente.fxml"));

	        Parent root = loader.load();

	        HistoricoClienteController controller = loader.getController();

	        controller.setCliente(clienteSelecionado);

	        Stage stage = new Stage();
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void limparCampos() {
		txtNome.clear();
		txtCpfCnpj.clear();
		txtEmail.clear();
	}

	public void carregarTabela(String valor) {
		ObservableList<ClienteModel> lista = FXCollections.observableArrayList(ClienteDAO.listarTodos(valor));

		tvClientes.setItems(lista);
	}
}
