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

        tvProds.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProdutoModel produto = tvProds.getSelectionModel().getSelectedItem();
                if (produto != null) pedirQuantidade(produto);
            }
        });
    }

    private void pedirQuantidade(ProdutoModel produto) {

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setHeaderText("Produto: " + produto.getNome());

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int qtd = Integer.parseInt(result.get());
            if (qtd <= 0 || qtd > produto.getEstoque()) return;
            adicionarItem(produto, qtd);
        }
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

            // depois que fechar pagamento:
            concluirVenda(total);

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

        gerarCupom(vendaId, total);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Venda finalizada com sucesso!");
        a.showAndWait();

        itens.clear();
        tvCompra.refresh();
        lblTotal.setText("R$ 0,00");
    }
}