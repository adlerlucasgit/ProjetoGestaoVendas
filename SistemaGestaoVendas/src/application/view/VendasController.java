package application.view;

import java.util.Optional;

import application.dao.ClienteDAO;
import application.dao.ProdutoDAO;
import application.dao.VendaDAO;
import application.dao.VendaItemDAO;
import application.model.ClienteModel;
import application.model.ProdutoModel;
import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

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

    private ObservableList<VendaItemModel> itens = FXCollections.observableArrayList();

    private double desconto = 0;

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

        // adicionar produto
        tvProds.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                ProdutoModel produto = tvProds.getSelectionModel().getSelectedItem();

                if (produto != null) {
                    pedirQuantidade(produto);
                }
            }
        });

        // editar item
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

                if (qtd > produto.getEstoque()) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Estoque insuficiente!");
                    a.showAndWait();
                    return;
                }

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

        double totalComDesconto = total - (total * (desconto / 100));

        lblTotal.setText("R$ " + String.format("%.2f", totalComDesconto));
    }

    @FXML
    public void Finalizar() {

        // ✅ validações
        if (clienteSelecionado == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Selecione um cliente!");
            a.showAndWait();
            return;
        }

        if (clienteSelecionado.getStatus().equals("INATIVO")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Cliente inativo não pode comprar!");
            a.showAndWait();
            return;
        }

        if (itens.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Adicione produtos!");
            a.showAndWait();
            return;
        }

        ProdutoDAO produtoDAO = new ProdutoDAO();

        // ✅ valida estoque (RN02)
        for (VendaItemModel item : itens) {
            ProdutoModel p = produtoDAO.buscarPorId(item.getProdutoId());

            if (p == null) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Produto não encontrado!");
                a.showAndWait();
                return;
            }

            if (item.getQuantidade() > p.getEstoque()) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Estoque insuficiente para: " + p.getNome());
                a.showAndWait();
                return;
            }
        }

        // ✅ calcula total
        double total = 0;
        for (VendaItemModel item : itens) {
            total += item.getSubtotal();
        }

        int usuarioId = Sessao.IdUser;

        VendaDAO vendaDAO = new VendaDAO();

        int vendaId = vendaDAO.inserirVendaComItens(
                clienteSelecionado.getId(),
                usuarioId,
                total,
                itens
        );

        if (vendaId <= 0) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Erro ao salvar venda!");
            a.showAndWait();
            return;
        }

        gerarCupom(vendaId, total);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Venda finalizada com sucesso!");
        a.showAndWait();

        Cancelar(); 
    }

    private void gerarCupom(int vendaId, double total) {

        StringBuilder cupom = new StringBuilder();

        cupom.append("===== CUPOM =====\n\n");

        for (VendaItemModel item : itens) {
            cupom.append(item.getProduto())
                    .append(" x")
                    .append(item.getQuantidade())
                    .append(" - R$ ")
                    .append(String.format("%.2f", item.getSubtotal()))
                    .append("\n");
        }

        cupom.append("\nTOTAL: R$ ").append(String.format("%.2f", total));

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Cupom");
        a.setContentText(cupom.toString());
        a.showAndWait();
    }

    public void aplicarDesconto(double percentual) {

        if (percentual > 5 && !Sessao.tipoUsuario.equals("GERENTE")) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Apenas gerente pode aplicar >5%");
            a.showAndWait();
            return;
        }

        desconto = percentual;
        atualizarTotal();
    }

    @FXML
    public void Cancelar() {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar compra");
        confirm.setHeaderText("Deseja cancelar?");
        confirm.setContentText("Todos os itens serão removidos.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            itens.clear();
            tvCompra.refresh();
            lblTotal.setText("R$ 0,00");
        }
    }

    public void carregarTabela(String valor) {
        ObservableList<ProdutoModel> lista =
                FXCollections.observableArrayList(ProdutoDAO.listarTodos(valor));

        tvProds.setItems(lista);
    }
}