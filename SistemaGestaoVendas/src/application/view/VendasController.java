package application.view;

import java.util.Optional;

import application.dao.ClienteDAO;
import application.model.ClienteModel;
import javafx.scene.control.ComboBox;
import application.dao.ProdutoDAO;
import application.dao.VendaDAO;
import application.dao.VendaItemDAO;
import application.model.ProdutoModel;
import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class VendasController extends TelaInicialController {

	@FXML
	private ComboBox<ClienteModel> cbCliente;

	private ClienteModel clienteSelecionado;
	
    @FXML
    private AnchorPane ap;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnFinalizar;
    
    @FXML
    private TableView<VendaItemModel> tvCompra;

    @FXML
    private TableColumn<VendaItemModel, String> colProd;

    @FXML
    private TableColumn<VendaItemModel, Integer> colQtd;

    @FXML
    private TableColumn<VendaItemModel, Double> colPreco;

    @FXML
    private TableColumn<VendaItemModel, Double> colTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<ProdutoModel> tvProds;
    
    @FXML
    private TableColumn<ProdutoModel, Double> colPrecoEst;
    
    @FXML
    private TableColumn<ProdutoModel, String> colProdEst;
    
    ProdutoModel produtoSelecionado;
    private ObservableList<VendaItemModel> itens = FXCollections.observableArrayList();
    
    public void initialize() {
    	carregarClientes();

    	cbCliente.getSelectionModel().selectedItemProperty().addListener(
    	    (obs, oldValue, cliente) -> {
    	        if (cliente != null) {
    	            clienteSelecionado = cliente;
    	        }
    	    }
    	);
    	
		colPrecoEst.setCellValueFactory(new PropertyValueFactory<>("custoVenda"));
		colProdEst.setCellValueFactory(new PropertyValueFactory<>("nome"));
		carregarTabela(null);
		
		colProd.setCellValueFactory(new PropertyValueFactory<>("produto"));
		colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
		colTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

		tvCompra.setItems(itens);
		
	    tvProds.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 2) {
	            ProdutoModel produto = tvProds.getSelectionModel().getSelectedItem();

	            if (produto != null) {
	                pedirQuantidade(produto);
	            }
	        }
	    });
	    
	    tvCompra.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 2) {

	            VendaItemModel itemSelecionado =
	                    tvCompra.getSelectionModel().getSelectedItem();

	            if (itemSelecionado != null) {

	                TextInputDialog dialog = new TextInputDialog(
	                        String.valueOf(itemSelecionado.getQuantidade())
	                );

	                dialog.setTitle("Editar Quantidade");
	                dialog.setHeaderText("Produto: " + itemSelecionado.getProduto());
	                dialog.setContentText("Nova quantidade:");

	                Optional<String> result = dialog.showAndWait();

	                if (result.isPresent()) {
	                    try {
	                        int novaQtd = Integer.parseInt(result.get());

	                        if (novaQtd <= 0) {
	                            itens.remove(itemSelecionado);
	                        } else {

	                            itens.remove(itemSelecionado);

	                            VendaItemModel atualizado = new VendaItemModel(
	                                    itemSelecionado.getId(),
	                                    itemSelecionado.getVendaId(),
	                                    itemSelecionado.getProdutoId(),
	                                    itemSelecionado.getProduto(),
	                                    novaQtd,
	                                    itemSelecionado.getPreco()
	                            );

	                            itens.add(atualizado);
	                        }

	                        atualizarTotal();

	                    } catch (NumberFormatException e) {
	                        Alert alert = new Alert(Alert.AlertType.ERROR);
	                        alert.setContentText("Digite um número válido!");
	                        alert.showAndWait();
	                    }
	                }
	            }
	        }
	    });
	    

    }
    
    private void carregarClientes() {
        cbCliente.setItems(
            FXCollections.observableArrayList(
                ClienteDAO.listarTodos(null)
            )
        );
    }
    
    @FXML
    public void buscarCliente() {

        ClienteModel cliente = cbCliente.getSelectionModel().getSelectedItem();

        if (cliente == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Selecione um cliente!");
            a.showAndWait();
            return;
        }

        if (cliente.getStatus().equals("INATIVO")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Cliente INATIVO não pode comprar!");
            a.showAndWait();
            cbCliente.getSelectionModel().clearSelection();
            clienteSelecionado = null;
            return;
        }

        clienteSelecionado = cliente;

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Cliente selecionado: " + cliente.getNome());
        a.showAndWait();
    }
    
    private void pedirQuantidade(ProdutoModel produto) {

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Quantidade");
        dialog.setHeaderText("Produto: " + produto.getNome());
        dialog.setContentText("Digite a quantidade:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                int qtd = Integer.parseInt(result.get());

                if (qtd <= 0) return;

                adicionarItem(produto, qtd);

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Digite um número válido!");
                alert.showAndWait();
            }
        }
    }
    
    private void adicionarItem(ProdutoModel produto, int quantidade) {

        for (VendaItemModel item : itens) {
            if (item.getProdutoId() == produto.getId()) {
                int novaQtd = item.getQuantidade() + quantidade;

                itens.remove(item);

                VendaItemModel atualizado = new VendaItemModel(
                        item.getId(),
                        item.getVendaId(),
                        produto.getId(),
                        produto.getNome(),
                        novaQtd,
                        produto.getCustoVenda()
                );

                itens.add(atualizado);

                atualizarTotal();
                return;
            }
        }

        VendaItemModel item = new VendaItemModel(
                0,
                0,
                produto.getId(),
                produto.getNome(),
                quantidade,
                produto.getCustoVenda()
        );

        itens.add(item);

        atualizarTotal();
    }
    
    private void atualizarTotal() {

        double total = 0;

        for (VendaItemModel item : itens) {
            total += item.getSubtotal();
        }

        lblTotal.setText("R$ " + String.format("%.2f", total));
    }
    
    @FXML
    public void Finalizar() {

        if (clienteSelecionado == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Selecione um cliente!");
            a.showAndWait();
            return;
        }

        if (itens.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Adicione produtos à venda!");
            a.showAndWait();
            return;
        }

        ProdutoDAO produtoDAO = new ProdutoDAO();

        // 🔥 VALIDA ESTOQUE ANTES
        for (VendaItemModel item : itens) {
            ProdutoModel p = produtoDAO.buscarPorId(item.getProdutoId());

            if (item.getQuantidade() > p.getQuantidade()) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Estoque insuficiente para: " + p.getNome());
                a.showAndWait();
                return;
            }
        }

        double total = 0;

        for (VendaItemModel item : itens) {
            total += item.getSubtotal();
        }

        int usuarioId = Sessao.IdUser;

        VendaDAO vendaDAO = new VendaDAO();

        // 🔥 SALVA VENDA PRIMEIRO
        int vendaId = vendaDAO.inserirVenda(clienteSelecionado.getId(), usuarioId, total);

        if (vendaId <= 0) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Erro ao salvar venda!");
            a.showAndWait();
            return;
        }

        VendaItemDAO itemDAO = new VendaItemDAO();

        // 🔥 SALVA ITENS
        for (VendaItemModel item : itens) {
            itemDAO.inserirItem(
                vendaId,
                item.getProdutoId(),
                item.getQuantidade(),
                item.getPreco()
            );
        }

        // 🔥 AGORA SIM: BAIXA ESTOQUE
        for (VendaItemModel item : itens) {
            produtoDAO.baixarEstoque(item.getProdutoId(), item.getQuantidade());
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Venda finalizada com sucesso!");
        a.showAndWait();

        Cancelar(); // limpa carrinho
    }
    
    @FXML
    public void Cancelar() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar compra");
        confirm.setHeaderText("Deseja realmente cancelar a compra?");
        confirm.setContentText("Todos os itens serão removidos.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            itens.clear();         
            tvCompra.refresh();   
            lblTotal.setText("R$ 0,00"); 
        }
    }
    public void carregarTabela(String valor) {
        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList(ProdutoDAO.listarTodos(valor));

        tvProds.setItems(lista);
    }

}
