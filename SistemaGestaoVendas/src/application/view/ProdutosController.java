package application.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import application.dao.ProdutoDAO;
import application.model.ProdutoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProdutosController extends TelaInicialController {

    @FXML
    private ImageView ivLogo;

    @FXML
    private TableView<ProdutoModel> tvProdutos;

    @FXML
    private TableColumn<ProdutoModel, Integer> colIdP;
    @FXML
    private TableColumn<ProdutoModel, String> colProd;
    @FXML
    private TableColumn<ProdutoModel, String> colDesc;
    @FXML
    private TableColumn<ProdutoModel, String> colCat;
    @FXML
    private TableColumn<ProdutoModel, Integer> colQtd;
    @FXML
    private TableColumn<ProdutoModel, Double> colCusto;
    @FXML
    private TableColumn<ProdutoModel, Double> colVenda;
    @FXML
    private TableColumn<ProdutoModel, String> colCodBarras;

    @FXML
    private TextField txtBusca;
    @FXML
    private TextField txtCategoria;
    @FXML
    private TextField txtCodBarras;
    @FXML
    private TextField txtCusto;
    @FXML
    private TextField txtDesc;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtProd;
    @FXML
    private TextField txtQuantidade;
    @FXML
    private TextField txtVenda;

    private ProdutoModel produtoSelecionado;
    private ProdutoDAO dao = new ProdutoDAO();

	ProdutoModel produto = new ProdutoModel(0,null, null, null, 0, 0, 0, 0, null);

    @FXML
	public void Logout() {
		carregarTela("TelaInicial.fxml");
	}
	
    @FXML
    public void Voltar() {
        carregarTela("Sistema.fxml");
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
    
	public String FormatarID(int id) {
	    return String.format("%06d", id);
	}
	
	public String FormatarPreco(double preco) {
		// Configura o formato brasileiro com vírgula
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("pt", "BR"));
		simbolos.setDecimalSeparator(',');
		DecimalFormat formato = new DecimalFormat("#0.00", simbolos); // 2 casas decimais

		// Converte para string com vírgula
		String precoFormatado = formato.format(preco);
		
		return precoFormatado;
	}
    
    public void initialize() {
        Image logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
        ivLogo.setImage(logo);

        colIdP.setCellValueFactory(new PropertyValueFactory<>("idFormatado"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colCusto.setCellValueFactory(new PropertyValueFactory<>("precoCusto"));
        colVenda.setCellValueFactory(new PropertyValueFactory<>("custoVenda"));
        colCodBarras.setCellValueFactory(new PropertyValueFactory<>("codBarras"));

        carregarTabela(null);

        tvProdutos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, produto) -> {
                if (produto != null) {
                    txtId.setText(FormatarID(produto.getId()));
                    txtProd.setText(produto.getNome());
                    txtDesc.setText(produto.getDescricao());
                    txtCategoria.setText(produto.getCategoria());
                    txtQuantidade.setText(String.valueOf(produto.getEstoque()));
                    txtCusto.setText(String.valueOf(produto.getPrecoCusto()));
                    txtVenda.setText(String.valueOf(produto.getCustoVenda()));
                    txtCodBarras.setText(produto.getCodBarras());

                    produtoSelecionado = produto;
                }
            }
        );
    }

    @FXML
    public void Cadastrar() {
    	int quantidade = 0;
        if (txtProd.getText().isEmpty() || txtCusto.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Preencha os campos obrigatórios!");
            a.show();
            return;
        }
        if (txtQuantidade.getText().isEmpty()) {
        	quantidade = 0;
        }

        try {
        	
            ProdutoModel produto = new ProdutoModel(
                    0,
                    txtProd.getText(),
                    gerarCodigoBarrasUnico(),
                    txtDesc.getText(),
                    Double.parseDouble(txtCusto.getText()),
                    Double.parseDouble(txtVenda.getText()),
                    quantidade,
                    0,
                    txtCategoria.getText()
            );

            dao.Salvar(produto);

            carregarTabela(null);
            limparCampos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String gerarCodigoBarrasUnico() {
        String codigo = "P";

        try {
			do {
			    // Gera 12 dígitos aleatórios
			    codigo += String.format("%012d", (long)(Math.random() * 1_000_000_000_000L));
			} while (ProdutoDAO.existeCodigoBarras(codigo));
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return codigo;
    }

    @FXML
    public void Editar() {
        if (produtoSelecionado == null) return;

        produtoSelecionado.setNome(txtProd.getText());
        produtoSelecionado.setDescricao(txtDesc.getText());
        produtoSelecionado.setCategoria(txtCategoria.getText());
        produtoSelecionado.setEstoque(Integer.parseInt(txtQuantidade.getText()));
        produtoSelecionado.setPrecoCusto(Double.parseDouble(txtCusto.getText()));
        produtoSelecionado.setCustoVenda(Double.parseDouble(txtVenda.getText()));
        produtoSelecionado.setCodBarras(txtCodBarras.getText());

        dao.Editar(produtoSelecionado);

        carregarTabela(null);
        limparCampos();
    }

    @FXML
    public void Excluir() {
        if (produtoSelecionado == null) return;

        dao.Excluir(produtoSelecionado);

        carregarTabela(null);
        limparCampos();
    }

    @FXML
    public void Pesquisar() {
    	if(!txtBusca.getText().isEmpty()) {
    		produto = dao.Buscar(txtBusca.getText());
       		carregarTabela(txtBusca.getText());
    		
       		if(produto == null) {
       			carregarTabela(null);
       			return;
       		} else {
           		txtId.setText(FormatarID(produto.getId()));
        		txtProd.setText(produto.getNome());
        		txtDesc.setText(produto.getDescricao());
        		txtCategoria.setText(produto.getCategoria());
        		txtQuantidade.setText(String.valueOf((produto.getEstoque())));
        		txtCusto.setText(FormatarPreco(produto.getPrecoCusto()));
        		txtVenda.setText(FormatarPreco(produto.getCustoVenda()));

        		produtoSelecionado = produto;	
       		}
    	} else {
        	Alert mensagem = new Alert(Alert.AlertType.ERROR);
        	mensagem.setContentText("Campo em branco!");
        	mensagem.showAndWait();
        	carregarTabela(null);
        	limparCampos();
    	}	
    }

    public void carregarTabela(String valor) {
        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList(ProdutoDAO.listarTodos(valor));

        tvProdutos.setItems(lista);
    }

    public void limparCampos() {
        txtId.clear();
        txtProd.clear();
        txtDesc.clear();
        txtCategoria.clear();
        txtQuantidade.clear();
        txtCusto.clear();
        txtVenda.clear();
        txtCodBarras.clear();
        txtBusca.clear();
    }
}