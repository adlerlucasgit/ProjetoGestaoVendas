package application.view;

import java.util.Optional;

import application.dao.ProdutoDAO;
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

        if (itens.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Nenhum item na compra");
            alert.setContentText("Adicione produtos antes de finalizar.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Pagamento.fxml"));

            Parent root = loader.load();

            PagamentoController controller = loader.getController();

            controller.setItens(itens);

            double total = itens.stream()
                    .mapToDouble(VendaItemModel::getSubtotal)
                    .sum();

            controller.setTotal(total);

            Stage stage = new Stage();
            stage.setTitle("Pagamento");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
