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

        // valida pagamento ANTES
        if (cbPagamento.getValue().equals("Dinheiro")) {
            double pago;

            try {
                pago = Double.parseDouble(txtValorPago.getText());
            } catch (NumberFormatException e) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Valor inválido!");
                a.showAndWait();
                return;
            }

            if (pago < total) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setContentText("Pagamento insuficiente!");
                a.showAndWait();
                return;
            }
        }

        formaPagamento = cbPagamento.getValue();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirmar pagamento");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            confirmado = true;
            fecharTela();
        }
    }
    
    private String formaPagamento;

    public String getFormaPagamento() {
        return formaPagamento;
    }
    
    public boolean confirmado = false;
    public boolean isConfirmado() {
        return confirmado;
    }
    
    @FXML
    public void calcularTroco() {
    	double pago;
    	try {
    	    pago = Double.parseDouble(txtValorPago.getText());
    	} catch (NumberFormatException e) {
    	    lblTroco.setText("Valor inválido!");
    	    return;
    	}

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