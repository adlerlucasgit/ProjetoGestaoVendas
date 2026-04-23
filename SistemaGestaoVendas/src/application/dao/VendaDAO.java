package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import application.util.Conexao;

public class VendaDAO {
	public int inserirVenda(int clienteId, int usuarioId, double total) {

	    String sql = "INSERT INTO vendas (cliente_id, usuario_id, total) VALUES (?, ?, ?)";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        ps.setInt(1, clienteId);
	        ps.setInt(2, usuarioId);
	        ps.setDouble(3, total);

	        ps.executeUpdate();

	        ResultSet rs = ps.getGeneratedKeys();

	        if (rs.next()) {
	            return rs.getInt(1); // ID da venda
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return -1;
	}
}
