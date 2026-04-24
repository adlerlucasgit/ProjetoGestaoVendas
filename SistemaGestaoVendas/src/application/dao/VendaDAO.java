package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import application.model.VendaItemModel;
import application.util.Conexao;

public class VendaDAO {
	public int inserirVendaComItens(int clienteId, int usuarioId, double total, List<VendaItemModel> itens) {

	    String sqlVenda = "INSERT INTO vendas (cliente_id, usuario_id, total) VALUES (?, ?, ?)";
	    String sqlItem = "INSERT INTO vendaItens (venda_id, produto_id, quantidade, preco) VALUES (?, ?, ?, ?)";
	    String sqlEstoque = "UPDATE produtos SET estoque = estoque - ? WHERE id = ?";

	    try (Connection conn = Conexao.getConnection()) {

	        conn.setAutoCommit(false); // 🔥 inicia transação

	        PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
	        psVenda.setInt(1, clienteId);
	        psVenda.setInt(2, usuarioId);
	        psVenda.setDouble(3, total);
	        psVenda.executeUpdate();

	        ResultSet rs = psVenda.getGeneratedKeys();
	        rs.next();
	        int vendaId = rs.getInt(1);

	        PreparedStatement psItem = conn.prepareStatement(sqlItem);
	        PreparedStatement psEstoque = conn.prepareStatement(sqlEstoque);

	        for (VendaItemModel item : itens) {

	            // item
	            psItem.setInt(1, vendaId);
	            psItem.setInt(2, item.getProdutoId());
	            psItem.setInt(3, item.getQuantidade());
	            psItem.setDouble(4, item.getPreco());
	            psItem.executeUpdate();

	            // estoque
	            psEstoque.setInt(1, item.getQuantidade());
	            psEstoque.setInt(2, item.getProdutoId());
	            psEstoque.executeUpdate();
	        }

	        conn.commit(); // ✅ sucesso
	        return vendaId;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
	
	public void cancelarVenda(int vendaId) {

	    String sqlUpdate = "UPDATE vendas SET status='CANCELADA' WHERE id=?";
	    String sqlItens = "SELECT produto_id, quantidade FROM vendaItens WHERE venda_id=?";
	    String sqlEstoque = "UPDATE produtos SET estoque = estoque + ? WHERE id=?";

	    try (Connection conn = Conexao.getConnection()) {

	        PreparedStatement psItens = conn.prepareStatement(sqlItens);
	        psItens.setInt(1, vendaId);

	        ResultSet rs = psItens.executeQuery();

	        while (rs.next()) {
	            PreparedStatement psEstoque = conn.prepareStatement(sqlEstoque);
	            psEstoque.setInt(1, rs.getInt("quantidade"));
	            psEstoque.setInt(2, rs.getInt("produto_id"));
	            psEstoque.executeUpdate();
	        }

	        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
	        psUpdate.setInt(1, vendaId);
	        psUpdate.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
