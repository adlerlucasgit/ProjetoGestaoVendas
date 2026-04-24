package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.VendaItemModel;
import application.util.Conexao;

public class VendaDAO {
	public int inserirVendaComItens(int clienteId, int usuarioId, double total, List<VendaItemModel> itens) {

	    String sqlVenda = "INSERT INTO vendas (cliente_id, usuario_id, total, status) VALUES (?, ?, ?, 'FINALIZADA')";
	    String sqlItem = "INSERT INTO vendaItens (venda_id, produto_id, quantidade, preco) VALUES (?, ?, ?, ?)";
	    String sqlEstoque = "UPDATE produtos SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";

	    try (Connection conn = Conexao.getConnection()) {

	        conn.setAutoCommit(false);

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

	            // 🔒 valida estoque direto no banco
	            psEstoque.setInt(1, item.getQuantidade());
	            psEstoque.setInt(2, item.getProdutoId());
	            psEstoque.setInt(3, item.getQuantidade());

	            int atualizado = psEstoque.executeUpdate();

	            if (atualizado == 0) {
	                conn.rollback();
	                throw new RuntimeException("Estoque insuficiente no banco!");
	            }

	            // insere item
	            psItem.setInt(1, vendaId);
	            psItem.setInt(2, item.getProdutoId());
	            psItem.setInt(3, item.getQuantidade());
	            psItem.setDouble(4, item.getPreco());
	            psItem.executeUpdate();
	        }

	        conn.commit();
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

	        conn.setAutoCommit(false);

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

	        conn.commit();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public List<VendaItemModel> buscarItensPorVenda(int vendaId) {

	    List<VendaItemModel> lista = new ArrayList<>();

	    String sql = "SELECT * FROM venda_itens WHERE venda_id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, vendaId);
	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {

	            VendaItemModel item = new VendaItemModel(
	                    rs.getInt("id"),
	                    rs.getInt("venda_id"),
	                    rs.getInt("produto_id"),
	                    "", // nome opcional
	                    rs.getInt("quantidade"),
	                    rs.getDouble("preco")
	            );

	            lista.add(item);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return lista;
	}
	
	public void inserirPagamento(int vendaId, String tipo, double valor) {

	    String sql = "INSERT INTO pagamentos (venda_id, tipo, valor) VALUES (?, ?, ?)";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, vendaId);
	        stmt.setString(2, tipo);
	        stmt.setDouble(3, valor);

	        stmt.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void registrarEstorno(int vendaId, String motivo) {

	    String sql = "UPDATE venda SET status = 'ESTORNADA', motivo_estorno = ? WHERE id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, motivo);
	        ps.setInt(2, vendaId);
	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isEstornada(int vendaId) {

	    String sql = "SELECT status FROM vendas WHERE id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, vendaId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return "ESTORNADA".equals(rs.getString("status"));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public void estornarVenda(int vendaId, String motivo) {

	    if (isEstornada(vendaId)) {
	        throw new RuntimeException("Venda já estornada!");
	    }

	    String sqlItens = "SELECT produto_id, quantidade FROM vendaItens WHERE venda_id=?";
	    String sqlEstoque = "UPDATE produtos SET estoque = estoque + ? WHERE id=?";
	    String sqlUpdate = "UPDATE vendas SET status='ESTORNADA', motivo_estorno=? WHERE id=?";

	    try (Connection conn = Conexao.getConnection()) {

	        conn.setAutoCommit(false);

	        // devolve estoque
	        PreparedStatement psItens = conn.prepareStatement(sqlItens);
	        psItens.setInt(1, vendaId);

	        ResultSet rs = psItens.executeQuery();

	        while (rs.next()) {
	            PreparedStatement psEstoque = conn.prepareStatement(sqlEstoque);
	            psEstoque.setInt(1, rs.getInt("quantidade"));
	            psEstoque.setInt(2, rs.getInt("produto_id"));
	            psEstoque.executeUpdate();
	        }

	        // marca estorno
	        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
	        psUpdate.setString(1, motivo);
	        psUpdate.setInt(2, vendaId);
	        psUpdate.executeUpdate();

	        conn.commit();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean vendaPodeSerAlterada(int vendaId) {

	    String sql = "SELECT status FROM vendas WHERE id=?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, vendaId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            String status = rs.getString("status");

	            return status.equals("FINALIZADA");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
}
