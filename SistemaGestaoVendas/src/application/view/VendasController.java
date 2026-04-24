package application.view;

import java.util.Optional;

import application.dao.ClienteDAO;
import application.dao.ProdutoDAO;
import application.dao.VendaDAO;
import application.model.ClienteModel;
import application.model.ProdutoModel;
import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class VendasController extends TelaInicialController {

    @FXML
    private ComboBox<ClienteModel> cbCliente;

    private ClienteModel clienteSelecionado;

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

    private ObservableList<VendaItemModel> itens = FXCollections.observableArrayList();

    private double desconto = 0;
    
    public void initialize() {

        cbCliente.setItems(FXCollections.observableArrayList(ClienteDAO.listarTodos(null)));

        cbCliente.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, cliente) -> {
                    if (cliente != null) clienteSelecionado = cliente;
                }
        );

        colPrecoEst.setCellValueFactory(new PropertyValueFactory<>("custoVenda"));
        colProdEst.setCellValueFactory(new PropertyValueFactory<>("nome"));

        colProd.setCellValueFactory(new PropertyValueFactory<>("produto"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tvCompra.setItems(itens);
        tvProds.setItems(FXCollections.observableArrayList(ProdutoDAO.listarTodos(null)));

        tvProds.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProdutoModel produto = tvProds.getSelectionModel().getSelectedItem();
                if (produto != null) pedirQuantidade(produto);
            }
        });
    }

    @FXML
    public void Cancelar() {
        btnFinalizar.getScene().getWindow().hide();
    }
    
    private void pedirQuantidade(ProdutoModel produto) {

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setHeaderText("Produto: " + produto.getNome());

        Optional<String> result = dialog.showAndWait();

        if (!result.isPresent()) {
            return;
        }

        int qtd;

        try {
            qtd = Integer.parseInt(result.get());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Quantidade inválida!");
            alert.showAndWait();
            return;
        }

        if (qtd <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Quantidade deve ser maior que zero!");
            alert.showAndWait();
            return;
        }

        int estoqueDisponivel = produto.getEstoque();

        for (VendaItemModel item : itens) {
            if (item.getProdutoId() == produto.getId()) {
                estoqueDisponivel -= item.getQuantidade();
            }
        }

        if (qtd > estoqueDisponivel) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Estoque insuficiente!");
            alert.showAndWait();
            return;
        }

        adicionarItem(produto, qtd);
    }

    private void adicionarItem(ProdutoModel produto, int quantidade) {

        for (VendaItemModel item : itens) {
            if (item.getProdutoId() == produto.getId()) {

                int novaQtd = item.getQuantidade() + quantidade;

                itens.remove(item);

                itens.add(new VendaItemModel(
                        item.getId(),
                        item.getVendaId(),
                        produto.getId(),
                        produto.getNome(),
                        novaQtd,
                        produto.getCustoVenda()
                ));

                atualizarTotal();
                return;
            }
        }

        itens.add(new VendaItemModel(
                0,
                0,
                produto.getId(),
                produto.getNome(),
                quantidade,
                produto.getCustoVenda()
        ));

        atualizarTotal();
    }

    private void atualizarTotal() {

        double total = 0;

        for (VendaItemModel item : itens) {
            total += item.getSubtotal();
        }

        double totalComDesconto = total - (total * (desconto / 100));
        lblTotal.setText("R$ " + String.format("%.2f", totalComDesconto));
    }

    @FXML
    public void buscarCliente() {

        ClienteModel cliente = cbCliente.getSelectionModel().getSelectedItem();

        if (cliente == null) return;

        if (cliente.getStatus().equals("INATIVO")) {
            cbCliente.getSelectionModel().clearSelection();
            clienteSelecionado = null;
            return;
        }

        clienteSelecionado = cliente;
    }
    
    @FXML
    public void Finalizar() {
    	if (clienteSelecionado == null) {
    	    Alert a = new Alert(Alert.AlertType.WARNING);
    	    a.setContentText("Selecione um cliente válido!");
    	    a.showAndWait();
    	    return;
    	}
    	
    	if (itens.isEmpty()) {
    	    Alert a = new Alert(Alert.AlertType.WARNING);
    	    a.setContentText("Adicione produtos à venda!");
    	    a.showAndWait();
    	    return;
    	}
        double total = 0;
        for (VendaItemModel item : itens) {
            total += item.getSubtotal();
        }

        int usuarioId = Sessao.IdUser;

        abrirPagamento(total);
    }
    
    private void abrirPagamento(double total) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Pagamento.fxml"));
            Parent root = loader.load();

            PagamentoController controller = loader.getController();
            controller.setItens(itens);
            controller.setTotal(total);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Pagamento");
            stage.showAndWait();

            if (controller.isConfirmado()) {
                
                String formaPagamento = controller.getFormaPagamento(); 

                concluirVenda(total); 
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void concluirVenda(double total) {

        ProdutoDAO produtoDAO = new ProdutoDAO();
        VendaDAO vendaDAO = new VendaDAO();

        int vendaId = vendaDAO.inserirVendaComItens(
                clienteSelecionado.getId(),
                Sessao.IdUser,
                total,
                itens
        );
        for (VendaItemModel item : itens) {
            produtoDAO.baixarEstoque(item.getProdutoId(), item.getQuantidade());
        }

        gerarCupom(vendaId, total);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Venda finalizada com sucesso!");
        a.showAndWait();

        itens.clear();
        tvCompra.refresh();
        lblTotal.setText("R$ 0,00");
    }
    
    private void gerarCupom(int vendaId, double total) {

        StringBuilder cupom = new StringBuilder();

        cupom.append("========= CUPOM NÃO FISCAL =========\n");
        cupom.append("Venda Nº: ").append(vendaId).append("\n");
        cupom.append("------------------------------------\n");

        if (clienteSelecionado != null) {
            cupom.append("Cliente: ").append(clienteSelecionado.getNome()).append("\n");
        }

        cupom.append("------------------------------------\n");

        for (VendaItemModel item : itens) {
            cupom.append(item.getProduto())
                 .append(" | Qtd: ").append(item.getQuantidade())
                 .append(" | R$ ").append(String.format("%.2f", item.getSubtotal()))
                 .append("\n");
        }

        cupom.append("------------------------------------\n");
        cupom.append("TOTAL: R$ ").append(String.format("%.2f", total)).append("\n");
        cupom.append("====================================\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cupom");
        alert.setHeaderText("Resumo da Venda");
        alert.setContentText(cupom.toString());
        alert.showAndWait();
    }
}