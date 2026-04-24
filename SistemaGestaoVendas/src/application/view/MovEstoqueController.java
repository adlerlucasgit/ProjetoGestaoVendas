package application.view;

import java.time.LocalDate;
import java.util.Optional;

import application.dao.ProdutoDAO;
import application.model.ProdutoModel;
import application.view.TelaInicialController.Sessao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MovEstoqueController extends TelaInicialController {
	@FXML
	private AnchorPane ap;

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnHistorico;

	@FXML
	private Button btnProcessar;

	@FXML
	private TableColumn<ProdutoModel, String> colCatM;

	@FXML
	private TableColumn<ProdutoModel, Double> colCustoM;

	@FXML
	private TableColumn<ProdutoModel, Integer> colIdM;

	@FXML
	private TableColumn<ProdutoModel, String> colProdM;

	@FXML
	private TableColumn<ProdutoModel, Integer> colQtdM;

	@FXML
	private TableColumn<ProdutoModel, Double> colValorM;

	@FXML
	private TableColumn<ProdutoModel, Integer> colMin;

	@FXML
	private ToggleGroup rdOp;

	@FXML
	private TableView<ProdutoModel> tvMov;

	@FXML
	private TextField txtBuscar;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtQuantidade;

	@FXML
	private TextField txtMin;

	@FXML
	private ImageView ivLogo;
	private Image logo;

	ProdutoDAO dao = new ProdutoDAO();
	ProdutoModel produtoSelecionado;
	ProdutoModel produto = new ProdutoModel(0, null, null, null, 0, 0, 0, 0, null);

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
		if (Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("NovoUsuario.fxml");
		} else {
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
		if (Sessao.tipoUsuario.equals("ESTOQUISTA") || Sessao.tipoUsuario.equals("GERENTE")) {
			carregarTela("Produtos.fxml");
		} else {
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

	public String FormatarID(int id) {
		return String.format("%06d", id);
	}

	public void initialize() {
		logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
		ivLogo.setImage(logo);

		colIdM.setCellValueFactory(new PropertyValueFactory<>("idFormatado"));
		colProdM.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colCatM.setCellValueFactory(new PropertyValueFactory<>("categoria"));
		colCustoM.setCellValueFactory(new PropertyValueFactory<>("precoCusto"));
		colValorM.setCellValueFactory(new PropertyValueFactory<>("custoVenda"));
		colQtdM.setCellValueFactory(new PropertyValueFactory<>("estoque"));
		colMin.setCellValueFactory(new PropertyValueFactory<>("estoqueMin"));

		carregarTabela(null);

		tvMov.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, produto) -> {
			if (produto != null) {
				txtId.setText(FormatarID(produto.getId()));
				txtNome.setText(produto.getNome());
				txtQuantidade.setText(String.valueOf(produto.getEstoque()));
				txtMin.setText(String.valueOf(produto.getEstoqueMin()));
				produtoSelecionado = produto;
			}
		});
	}

	public void Processar() {
		if (!txtQuantidade.getText().isBlank()) {
			int valorAntigo = produtoSelecionado.getEstoque();
			int quantidade = Integer.parseInt(txtQuantidade.getText());
			RadioButton operacao = (RadioButton) rdOp.getSelectedToggle();

			if ((Integer.parseInt(txtQuantidade.getText())) > produtoSelecionado.getEstoqueMin()
					&& operacao.getText().equals("SAIDA")) {
				Alert mensagem = new Alert(Alert.AlertType.WARNING);
				mensagem.setTitle("Valor mínimo em estoque próximo");
				mensagem.setContentText("");
				mensagem.showAndWait();
				limparCampos();
			} else {
				produtoSelecionado.setEstoque(Integer.parseInt(txtQuantidade.getText()));
				dao.ProcessarEstoque(produtoSelecionado, operacao.getText());
				carregarTabela(null);
				int valorNovo;
				if (operacao.getText().equalsIgnoreCase("ENTRADA")) {
					valorNovo = valorAntigo + quantidade;
				} else {
					valorNovo = valorAntigo - quantidade;
				}

				Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
				mensagem.setTitle("Produto processado com sucesso!");
				mensagem.setContentText("Quantidade: " + valorAntigo + " -> " + valorNovo);
				mensagem.showAndWait();
				limparCampos();
			}
		} else {
			Alert mensagem = new Alert(Alert.AlertType.ERROR);
			mensagem.setContentText("Nenhum produto selecionado");
			mensagem.showAndWait();
		}

	}

	@FXML
	public void setMin() {
		if (txtMin.getText().isBlank()) {
			Alert erro = new Alert(Alert.AlertType.ERROR);
			erro.setTitle("Erro");
			erro.setContentText("Campo estoque mínimo em branco");
			erro.showAndWait();
			return;
		}

		int novoValor;
		try {
			novoValor = Integer.parseInt(txtMin.getText());
		} catch (NumberFormatException e) {
			Alert erro = new Alert(Alert.AlertType.ERROR);
			erro.setTitle("Erro");
			erro.setContentText("Valor inválido!");
			erro.showAndWait();
			return;
		}

		int valorAntigo = produtoSelecionado.getEstoqueMin();

		Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
		mensagem.setTitle("Confirmar atualização");
		mensagem.setHeaderText("Atualizar estoque mínimo de " + produtoSelecionado.getNome());
		mensagem.setContentText("Quantidade: " + valorAntigo + " -> " + novoValor);

		Optional<ButtonType> resultado = mensagem.showAndWait();

		if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

			produtoSelecionado.setEstoqueMin(novoValor);

			dao.Editar(produtoSelecionado);

			limparCampos();
			carregarTabela(null);

		} else {
			Alert cancelado = new Alert(Alert.AlertType.INFORMATION);
			cancelado.setTitle("Cancelado");
			cancelado.setContentText("Operação cancelada pelo usuário.");
			cancelado.showAndWait();
		}
	}
	
	@FXML 
	public void Historico() {
    	try {
    		FXMLLoader loader = new FXMLLoader(
    				getClass().getResource("HistoricoMovimentacao.fxml"));
    		Parent root = loader.load();
    		HistoricoProdutoController controller = loader.getController();
    		controller.BuscarHistorico();
    		Stage stage= new Stage();
    		stage.setScene(new Scene (root));
    		stage.show();
        } catch(Exception e) {
    		e.printStackTrace();
    	}
		
	}

	@FXML
	public void Pesquisar() {
		if (!txtBuscar.getText().isEmpty()) {

			ProdutoModel resultado = dao.Buscar(txtBuscar.getText());

			if (resultado != null) {

				produto = resultado;
				produtoSelecionado = resultado;

				carregarTabela(txtBuscar.getText());

				txtId.setText(FormatarID(produto.getId()));
				txtNome.setText(produto.getNome());
				txtQuantidade.setText(String.valueOf(produto.getEstoque()));

			} else {
				Alert mensagem = new Alert(Alert.AlertType.ERROR);
				mensagem.setContentText("Produto não encontrado!");
				mensagem.showAndWait();

				carregarTabela(null);
				limparCampos();
				produtoSelecionado = null;
			}

		} else {
			Alert mensagem = new Alert(Alert.AlertType.ERROR);
			mensagem.setContentText("Produto não encontrado!");
			mensagem.showAndWait();

			carregarTabela(null);
			limparCampos();
		}
	}

	public void limparCampos() {
		txtBuscar.clear();
		txtQuantidade.clear();
		txtNome.clear();
		txtId.clear();
	}

	public void carregarTabela(String valor) {
		ObservableList<ProdutoModel> lista = FXCollections.observableArrayList(ProdutoDAO.listarTodos(valor));

		tvMov.setItems(lista);
	}

}
