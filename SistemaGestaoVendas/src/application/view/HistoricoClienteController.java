package application.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import application.dao.MovClienteDAO;
import application.dao.VendaItemDAO;
import application.model.ClienteModel;
import application.model.MovClienteModel;
import application.model.VendaItemModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class HistoricoClienteController {

    @FXML
    private AnchorPane ap;
    
    @FXML
    private TableView<MovClienteModel> tvCompras;

    @FXML
    private TableColumn<MovClienteModel, Integer> colId;

    @FXML
    private TableColumn<MovClienteModel, LocalDateTime> colData;

    @FXML
    private TableColumn<MovClienteModel, Double> colTotal;

    @FXML
    private TableColumn<MovClienteModel, String> colStatus;

    @FXML
    private TableView<VendaItemModel> tvItens;

    @FXML
    private TableColumn<VendaItemModel, Integer> colIdItem;

    @FXML
    private TableColumn<VendaItemModel, String> colProduto;

    @FXML
    private TableColumn<VendaItemModel, Integer> colIdVenda;

    @FXML
    private TableColumn<VendaItemModel, String> colPreco;
    
    @FXML
    private TableColumn<VendaItemModel, Integer> colQuantidade;
    
    @FXML
    private TableColumn<VendaItemModel, String> colSubtotal;
    
    @FXML
    private Label lblCompras;

    @FXML
    private DatePicker dtInicio;

    @FXML
    private DatePicker dtFinal;

    private ClienteModel cliente;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("vendaId"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colIdItem.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
        colIdVenda.setCellValueFactory(new PropertyValueFactory<>("vendaId"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("precoFormatado"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotalFormatado"));

        tvCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, vendaSelecionada) -> {

            if (vendaSelecionada != null) {

                List<VendaItemModel> itens =
                        new VendaItemDAO().buscarPorVenda(vendaSelecionada.getVendaId());

                tvItens.setItems(FXCollections.observableArrayList(itens));
            }
        });

        LocalDate hoje = LocalDate.now();
        dtInicio.setValue(hoje.minusDays(30));
        dtFinal.setValue(hoje);
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;

        if (cliente != null) {
            lblCompras.setText("Cliente: " + cliente.getNome());

            if (dtInicio.getValue() == null || dtFinal.getValue() == null) {
                LocalDate hoje = LocalDate.now();
                dtInicio.setValue(hoje.minusDays(30));
                dtFinal.setValue(hoje);
            }

            BuscarHistorico(cliente.getId(), dtInicio.getValue(), dtFinal.getValue());
        }
    }

    public void BuscarHistorico(int idCliente, LocalDate dataInicio, LocalDate dataFinal) {

        if (idCliente <= 0 || dataInicio == null || dataFinal == null) {
            return;
        }

        if (dataInicio.isAfter(dataFinal)) {
            return;
        }

        List<MovClienteModel> listaHistorico =
                new MovClienteDAO().buscarPorClientePeriodo(idCliente, dataInicio, dataFinal);

        System.out.println("Registros encontrados: " + listaHistorico.size());

        tvCompras.setItems(FXCollections.observableArrayList(listaHistorico));
    }

    public void Buscar() {

        if (cliente == null) {
            return;
        }

        LocalDate inicio = dtInicio.getValue();
        LocalDate fim = dtFinal.getValue();

        if (inicio == null || fim == null) {
            return;
        }

        BuscarHistorico(cliente.getId(), inicio, fim);
    }
}