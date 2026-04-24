package application.view;

import java.util.Optional;

import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PagamentoController {

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<String> cbPagamento;

    @FXML
    private TextField txtValorPago;

    @FXML
    private Label lblTroco;

    private ObservableList<VendaItemModel> itens;
    private double total;

    public void initialize() {

        cbPagamento.setItems(FXCollections.observableArrayList(
                "Dinheiro",
                "Cartão Débito",
                "Cartão Crédito",
                "PIX"
        ));
    }

    public void setItens(ObservableList<VendaItemModel> itens) {
        this.itens = itens;
    }

    public void setTotal(double total) {
        this.total = total;
        lblTotal.setText("R$ " + String.format("%.2f", total));
    }

    @FXML
    public void Confirmar() {

        if (cbPagamento.getValue() == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Finalizar Venda");
        confirm.setHeaderText("Confirmar pagamento");
        confirm.setContentText("Forma: " + cbPagamento.getValue() +
                "\nTotal: R$ " + String.format("%.2f", total));

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            confirmado = true; 

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
            sucesso.setHeaderText("Venda realizada com sucesso!");
            sucesso.showAndWait();

            fecharTela();
        }
    }
    
    public boolean confirmado = false;
    public boolean isConfirmado() {
        return confirmado;
    }

    @FXML
    public void calcularTroco() {

        double pago = Double.parseDouble(txtValorPago.getText());

        if (pago < total) {
            lblTroco.setText("Valor insuficiente!");
            return;
        }

        double troco = pago - total;
        lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));
    }

    @FXML
    public void Cancelar() {
        fecharTela();
    }

    private void fecharTela() {
        lblTotal.getScene().getWindow().hide();
    }
}