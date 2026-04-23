package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.model.MovEstoqueModel;
import application.model.ProdutoModel;
import application.util.Conexao;
import application.view.TelaInicialController.Sessao;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ProdutoDAO {
	public boolean Salvar(ProdutoModel produto) {
	    String sql = "INSERT INTO produtos(nome, codigo_barras, descricao, preco_custo, preco_venda, estoque, estoque_minimo, categoria) VALUES(?,?,?,?,?,?,?,?)";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, produto.getNome());
	        stmt.setString(2, produto.getCodBarras());
	        stmt.setString(3, produto.getDescricao());
	        stmt.setDouble(4, produto.getPrecoCusto());
	        stmt.setDouble(5, produto.getCustoVenda());
	        stmt.setInt(6, produto.getEstoque());
	        stmt.setInt(7, produto.getEstoqueMin());
	        stmt.setString(8, produto.getCategoria());

	        int linhas = stmt.executeUpdate();

	        if (linhas > 0) {
	            Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
	            sucesso.setContentText("Produto salvo com sucesso!");
	            sucesso.showAndWait();
	            return true;
	        }

	    } catch (SQLException e) {
	        Alert erro = new Alert(Alert.AlertType.ERROR);
	        erro.setContentText("Erro ao salvar produto!");
	        erro.showAndWait();
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public void Editar(ProdutoModel produto) {
	    String sql = "UPDATE produtos SET nome=?, codigo_barras=?, descricao=?, preco_custo=?, preco_venda=?, estoque=?, estoque_minimo=?, categoria=? WHERE id=?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	    	stmt.setString(1, produto.getNome());
	    	stmt.setString(2, produto.getCodBarras());
	    	stmt.setString(3, produto.getDescricao());
	    	stmt.setDouble(4, produto.getPrecoCusto());
	    	stmt.setDouble(5, produto.getCustoVenda());
	    	stmt.setInt(6, produto.getEstoque());
	    	stmt.setInt(7, produto.getEstoqueMin());
	    	stmt.setString(8, produto.getCategoria());
	    	stmt.setInt(9, produto.getId());

	        stmt.executeUpdate();

	        Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
	        sucesso.setContentText("Produto atualizado com sucesso!");
	        sucesso.showAndWait();

	    } catch (SQLException e) {
	        Alert erro = new Alert(Alert.AlertType.ERROR);
	        erro.setContentText("Erro ao atualizar produto!");
	        erro.showAndWait();
	        e.printStackTrace();
	    }
	}
	
	public ProdutoModel Buscar(String valor) {
	    String busca = "%" + valor + "%";

	    String sql = "SELECT * FROM produtos WHERE id LIKE ? OR nome LIKE ? OR descricao LIKE ? OR categoria LIKE ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, busca);
	        stmt.setString(2, busca);
	        stmt.setString(3, busca);
	        stmt.setString(4, busca);

	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	        	return new ProdutoModel(
	            rs.getInt("id"),
	            rs.getString("nome"),
	            rs.getString("codigo_barras"),
	            rs.getString("descricao"),
	            rs.getDouble("preco_custo"),
	            rs.getDouble("preco_venda"),
	            rs.getInt("estoque"),
	            rs.getInt("estoque_minimo"),
	            rs.getString("categoria")
	        	);
	        	
	        } else {
	            Alert mensagem = new Alert(Alert.AlertType.ERROR);
	            mensagem.setContentText("Produto não encontrado!");
	            mensagem.showAndWait();
	            return null;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	public void Excluir(ProdutoModel produto) {
	    String sql = "DELETE FROM produtos WHERE id=?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        if (produto.getId()> 0) {

	            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
	            confirmacao.setHeaderText("Excluir produto");
	            confirmacao.setContentText("Deseja excluir: " + produto.getNome() + "?");

	            Optional<ButtonType> resultado = confirmacao.showAndWait();

	            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

	                stmt.setInt(1, produto.getId());
	                stmt.executeUpdate();

	                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
	                sucesso.setContentText("Produto excluído com sucesso!");
	                sucesso.showAndWait();

	            } else {
	                Alert cancelado = new Alert(Alert.AlertType.INFORMATION);
	                cancelado.setContentText("Exclusão cancelada");
	                cancelado.showAndWait();
	            }

	        } else {
	            Alert erro = new Alert(Alert.AlertType.ERROR);
	            erro.setContentText("Produto inválido!");
	            erro.showAndWait();
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static List<ProdutoModel> listarTodos(String valor) {

	    List<ProdutoModel> lista = new ArrayList<>();
	    String sql;

	    if (valor == null || valor.isEmpty()) {
	        sql = "SELECT * FROM produtos";
	    } else {
	        sql = "SELECT * FROM produtos WHERE id LIKE ? OR nome LIKE ? OR descricao LIKE ? OR categoria LIKE ?";
	    }

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        if (valor != null && !valor.isEmpty()) {
	            String busca = "%" + valor + "%";
	            stmt.setString(1, busca);
	            stmt.setString(2, busca);
	            stmt.setString(3, busca);
	            stmt.setString(4, busca);
	        }

	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            ProdutoModel p = new ProdutoModel(
	                    rs.getInt("id"),
	                    rs.getString("nome"),
	                    rs.getString("codigo_barras"),
	                    rs.getString("descricao"),
	                    rs.getDouble("preco_custo"),
	                    rs.getDouble("preco_venda"),
	                    rs.getInt("estoque"),
	                    rs.getInt("estoque_minimo"),
	                    rs.getString("categoria")
	            );

	            lista.add(p);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return lista;
	}
	
	public void ProcessarEstoque(ProdutoModel produto, String operacao) {
	    if (produto.getId() > 0) {

	        String sql = "UPDATE produtos SET estoque = estoque + ? WHERE id = ?";

	        if (operacao.equals("Saída")) {
	            sql = "UPDATE produtos SET estoque = estoque - ? WHERE id = ?";
	        }

	        try (Connection conn = Conexao.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            stmt.setInt(1, produto.getEstoque());
	            stmt.setInt(2, produto.getId());
	            stmt.execute();
	            
	            MovEstoqueModel mov = new MovEstoqueModel(0, 0, sql, sql, 0, sql);
	            mov.setIdProd(produto.getId());
	            mov.setQuantidade(produto.getEstoque()); 
	            mov.setTipo(operacao);
	            
	            MovEstoqueDAO dao = new MovEstoqueDAO();
				dao.InsereMovimentacao(mov);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public static boolean existeCodigoBarras(String codigo) {
	    String sql = "SELECT COUNT(*) FROM produtos WHERE codigo_barras = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, codigo);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            return rs.getInt(1) > 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public void baixarEstoque(int produtoId, int quantidade) {

	    String sql = "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, quantidade);
	        ps.setInt(2, produtoId);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void devolverEstoque(int produtoId, int quantidade) {

	    String sql = "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, quantidade);
	        ps.setInt(2, produtoId);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public ProdutoModel buscarPorId(int id) {

	    String sql = "SELECT * FROM produtos WHERE id = ?";

	    try (Connection conn = Conexao.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, id);

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {

	            return new ProdutoModel(
	                rs.getInt("id"),
	                rs.getString("nome"),
	                sql, sql, rs.getDouble("custo"),
	                rs.getDouble("custoVenda"),
	                rs.getInt("quantidade"),
	                id, rs.getString("categoria")
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
}
