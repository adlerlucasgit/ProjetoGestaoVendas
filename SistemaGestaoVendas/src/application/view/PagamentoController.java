package application.view;

import java.util.Optional;

import application.model.PagamentoModel;
import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PagamentoController {

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<String> cbPagamento;

    @FXML
    private TextField txtValorPago;
    
    @FXML
    private TableView<PagamentoModel> tvPagamentos;

    @FXML
    private TableColumn<PagamentoModel, String> colTipo;

    @FXML
    private TableColumn<PagamentoModel, Double> colValor;

    @FXML
    private Label lblTroco;

    private ObservableList<VendaItemModel> itens;
    private double total;
    
    private ObservableList<PagamentoModel> pagamentos = FXCollections.observableArrayList();
    
    public ObservableList<PagamentoModel> getPagamentos() {
        return pagamentos;
    }

    public void initialize() {
    	colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    	colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

    	tvPagamentos.setItems(pagamentos);

        cbPagamento.setItems(FXCollections.observableArrayList(
                "Dinheiro",
                "Cartão Débito",
                "Cartão Crédito",
                "PIX"
        ));
    }
    
    @FXML
    public void adicionarPagamento() {

        if (cbPagamento.getValue() == null) {
            erro("Selecione a forma de pagamento");
            return;
        }

        double valor;

        try {
            valor = Double.parseDouble(txtValorPago.getText());
        } catch (Exception e) {
            erro("Valor inválido");
            return;
        }

        if (valor <= 0) {
            erro("Valor deve ser maior que zero");
            return;
        }

        pagamentos.add(new PagamentoModel(cbPagamento.getValue(), valor));

        txtValorPago.clear();

        // calcular total pago
        double totalPago = pagamentos.stream()
                .mapToDouble(PagamentoModel::getValor)
                .sum();

        double restante = total - totalPago;

        if (restante > 0) {
            lblTroco.setText("Restante: R$ " + String.format("%.2f", restante));
        } else {
            double troco = totalPago - total;
            lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));
        }
    }

    private void erro(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg);
        a.showAndWait();
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

        if (pagamentos.isEmpty()) {
            erro("Adicione pelo menos um pagamento!");
            return;
        }

        double totalPago = 0;

        for (PagamentoModel p : pagamentos) {
            totalPago += p.getValor();
        }

        if (totalPago < total) {
            erro("Pagamento incompleto!");
            return;
        }

        double troco = totalPago - total;

        if (troco > 0) {
            lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));
        }

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