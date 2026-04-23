package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.model.MovEstoqueModel;
import application.model.ProdutoModel;
import application.util.Conexao;
import javafx.scene.control.Alert;

public class MovEstoqueDAO {
	public void InsereMovimentacao(MovEstoqueModel m) {
		String sql = "INSERT INTO estoqueMovimentacao " + "(produto_id, tipo, quantidade, data) "
				+ "VALUES (?, ?, ?, NOW())";
		
		try (Connection conn = Conexao.getConnection(); PreparedStatement consulta = conn.prepareStatement(sql)) {

			consulta.setInt(1, m.getIdProd());
			consulta.setString(2, m.getTipo().toUpperCase());
			consulta.setInt(3, m.getQuantidade());
			consulta.executeUpdate();

		} catch (Exception e) {
		    e.printStackTrace();

		    Alert erro = new Alert(Alert.AlertType.ERROR);
		    erro.setContentText("Erro ao salvar: " + e.getMessage());
		    erro.showAndWait();
		}
	}

	public static List<MovEstoqueModel> HistoricoMovimentacao() {
		List<MovEstoqueModel> movimentacao = new ArrayList<>();

		String sql = "SELECT " + "DATE_FORMAT(m.data, '%d/%m/%Y %H:%i:%s') AS data, " + "m.id, "
				+ "m.produto_id AS idProd, " + "p.nome, " + "m.quantidade, " + "m.tipo " + "FROM estoqueMovimentacao m "
				+ "INNER JOIN produtos p ON p.id = m.produto_id " + "ORDER BY m.data DESC";

		try (Connection conn = Conexao.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				MovEstoqueModel m = new MovEstoqueModel(rs.getInt("id"), rs.getInt("idProd"), rs.getString("nome"),
						rs.getString("data"), rs.getInt("quantidade"), rs.getString("tipo"));
				movimentacao.add(m);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movimentacao;
	}

	public List<MovEstoqueModel> HistoricoMovimentacaoFiltro(String filtro) {
		List<MovEstoqueModel> movimentacao = new ArrayList<>();

		String sql = "SELECT " + "DATE_FORMAT(m.data, '%d/%m/%Y %H:%i:%s') AS data, " + "m.id, "
				+ "m.produto_id AS idProd, " + "p.nome, " + "m.quantidade, " + "m.tipo " + "FROM estoqueMovimentacao m "
				+ "INNER JOIN produtos p ON p.id = m.produto_id " + "WHERE p.nome LIKE ? " + "ORDER BY m.data DESC";

		try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, "%" + filtro + "%");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				MovEstoqueModel m = new MovEstoqueModel(rs.getInt("id"), rs.getInt("idProd"), rs.getString("nome"),
						rs.getString("data"), rs.getInt("quantidade"), rs.getString("tipo"));
				movimentacao.add(m);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return movimentacao;
	}

}
