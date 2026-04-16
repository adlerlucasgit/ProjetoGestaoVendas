package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.model.MovEstoqueModel;
import application.util.Conexao;
import javafx.scene.control.Alert;

public class MovEstoqueDAO {
	public void InsereMovimentacao(MovEstoqueModel m) {
		try(Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement("INSERT INTO movimentacaoEstoque "
						+ "(idProd,dataHora,quantidade,tipo) VALUES (?,NOW(),?,?)");){
			int tipo = 0;
			if(m.getTipo().equals("Saída")) {
				tipo = 1;
			}
			consulta.setInt(1, m.getIdProd());
			consulta.setInt(2, m.getQuantidade());
			consulta.setInt(3, tipo);
			consulta.executeUpdate();
			
			Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
			mensagem.setContentText("Estoque processado");
			mensagem.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<MovEstoqueModel> HistoricoMovimentacao(int idProd, LocalDate dataInicio, LocalDate dataFim) {
	    List<MovEstoqueModel> movimentacao = new ArrayList<MovEstoqueModel>();

	    String sql = "SELECT " +
	             "DATE_FORMAT(m.dataHora, '%d/%m/%Y %H:%i:%s') AS data, " +
	             "m.id, " +
	             "m.idProd, " +
	             "p.nome, " +
	             "m.quantidade, " +
	             "u.nome AS usuario, " +
	             "CASE " +
	             "    WHEN m.tipo = 0 THEN 'Entrada' " +
	             "    WHEN m.tipo = 1 THEN 'Saída' " +
	             "    ELSE 'Não Informado' " +
	             "END AS tipo " +
	             "FROM produtos p " +
	             "INNER JOIN movimentacaoEstoque m ON p.id = m.idProd " +
	             "INNER JOIN usuarios u ON u.id = m.idUser " +
	             "WHERE p.id = ? " +
	             "AND m.dataHora >= ? AND m.dataHora < ? " +
	             "ORDER BY m.dataHora DESC";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement consulta = conn.prepareStatement(sql)) {
	    	
	    	if(idProd == 0) {
	    		consulta.setNull(1, java.sql.Types.INTEGER);
	    	}else {
		        consulta.setInt(1, idProd);
	    	}
	    	
	        consulta.setTimestamp(2, java.sql.Timestamp.valueOf(dataInicio.atStartOfDay()));
	        consulta.setTimestamp(3, java.sql.Timestamp.valueOf(dataFim.plusDays(1).atStartOfDay()));
	        
	        ResultSet resultado = consulta.executeQuery();

	        while (resultado.next()) {
	            MovEstoqueModel m = new MovEstoqueModel(
	                    resultado.getInt("id"),
	                    resultado.getInt("idProd"),
	                    resultado.getInt("idUser"),
	                    resultado.getString("nome"),
	                    resultado.getString("data"),
	                    resultado.getInt("quantidade"),
	                    resultado.getString("tipo")
	                    );
	            movimentacao.add(m);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return movimentacao;
	}

}
