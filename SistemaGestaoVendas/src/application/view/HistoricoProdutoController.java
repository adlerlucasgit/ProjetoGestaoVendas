package application.view;

import java.util.List;

import application.dao.MovEstoqueDAO;
import application.model.MovEstoqueModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HistoricoProdutoController extends TelaInicialController{
	
    @FXML
    private AnchorPane ap;

    @FXML
    private TableColumn<MovEstoqueModel, String> colData;

    @FXML
    private TableColumn<MovEstoqueModel, Integer> colId;

    @FXML
    private TableColumn<MovEstoqueModel, Integer> colIdProd;

    @FXML
    private TableColumn<MovEstoqueModel, String> colProd;
    
    @FXML
    private TableColumn<MovEstoqueModel, String> colTipo;
    
    @FXML
    private TableColumn<MovEstoqueModel, Integer> colQuantidade;

    @FXML
    private Button btnBuscar;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private TableView<MovEstoqueModel> tvHist;
    
    MovEstoqueModel p;
    MovEstoqueDAO dao;
    
	@FXML
	private ImageView ivLogo;
	private Image logo;
	
    @FXML
    public void initialize() {
		logo = new Image(getClass().getResourceAsStream("LogoETDv4.png"));
		ivLogo.setImage(logo);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdProd.setCellValueFactory(new PropertyValueFactory<>("idProd"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("nomeProd"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        BuscarHistorico();
    }
	
	MovEstoqueDAO movimentacao = new MovEstoqueDAO();

	public void BuscarHistorico() {

	    List<MovEstoqueModel> lista =
	            MovEstoqueDAO.HistoricoMovimentacao();

	    ObservableList<MovEstoqueModel> listaHistorico =
	            FXCollections.observableArrayList(lista);

	    tvHist.setItems(listaHistorico);
	}
	
	@FXML
	public void Pesquisar() {

	    String filtro = txtBuscar.getText();

	    if (filtro == null || filtro.isBlank()) {
	        BuscarHistorico();
	        return;
	    }

	    ObservableList<MovEstoqueModel> lista =
	            FXCollections.observableArrayList(
	                    movimentacao.HistoricoMovimentacaoFiltro(filtro)
	            );

	    tvHist.setItems(lista);
	}


}
