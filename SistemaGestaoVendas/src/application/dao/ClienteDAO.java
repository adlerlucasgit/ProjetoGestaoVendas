package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.model.ClienteModel;
import application.util.Conexao;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ClienteDAO {
	public static boolean encontrou;

	public boolean inserirCliente(ClienteModel cliente) {
		String sql = "INSERT INTO clientes (nome, cpf_cnpj, email, status) VALUES (?, ?, ?, ?)";

		try (Connection conn = Conexao.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, cliente.getNome());
			ps.setString(2, cliente.getCpfCnpj());
			ps.setString(3, cliente.getEmail());
			ps.setString(4, cliente.getStatus());

			ps.executeUpdate();
			return true;

		} catch (java.sql.SQLIntegrityConstraintViolationException e) {
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean CpfCnpjExiste(String cpfCnpj) {
		try {
			Connection conn = Conexao.getConnection();
			String sql = "SELECT COUNT(*) FROM clientes WHERE cpf_cnpj = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, cpfCnpj);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean emailExiste(String email) {
		try {
			Connection conn = Conexao.getConnection();
			String sql = "SELECT COUNT(*) FROM clientes WHERE email = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<ClienteModel> listarTodos(String valor) {
		String sql = "";

		List<ClienteModel> lista = new ArrayList<>();
		if (valor == null) {
			sql = "select * from clientes";
		} else {
			sql = "select * from clientes where id like ? or nome like ? or email like ? or status like ?";
		}

		try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			if (valor != null) {
				stmt.setString(1, "%" + valor + "%");
				stmt.setString(2, "%" + valor + "%");
				stmt.setString(3, "%" + valor + "%");
				stmt.setString(4, "%" + valor + "%");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				ClienteModel cliente = new ClienteModel(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf_cnpj"),
						rs.getString("email"), rs.getString("status"));

				lista.add(cliente);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lista;
	}

	public ClienteModel Buscar(String valor) {
		String busca = "%" + valor + "%";
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement(
						"select * from clientes where id like ? or nome like ? or cpf_cnpj like ? or email like ? or status like ?");) {
			consulta.setString(1, busca);
			consulta.setString(2, busca);
			consulta.setString(3, busca);
			consulta.setString(4, busca);
			consulta.setString(5, busca);

			ResultSet resultado = consulta.executeQuery();
			if (resultado.next()) {
				return new ClienteModel(resultado.getInt("id"), resultado.getString("nome"),
						resultado.getString("cpf_cnpj"), resultado.getString("email"), null);
			} else {
				Alert mensagem = new Alert(Alert.AlertType.ERROR);
				mensagem.setContentText("Cliente não encontrado!");
				encontrou = false;
				mensagem.showAndWait();
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void Editar(ClienteModel cliente) {
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn
						.prepareStatement("UPDATE clientes SET nome =?, cpf_cnpj=?, email=? WHERE id = ?")) {

			consulta.setString(1, cliente.getNome());
			consulta.setString(2, cliente.getCpfCnpj());
			consulta.setString(3, cliente.getEmail());
			consulta.setInt(4, cliente.getId());

			consulta.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public void Excluir(ClienteModel cliente) {
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement("delete from clientes where id=?");) {

			if (cliente.getId() > 0) {
				Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
				confirmacao.setTitle("Confirmação");
				confirmacao.setHeaderText("Excluir usuário");
				confirmacao.setContentText("Deseja realmente excluir o cliente: " + cliente.getNome() + "?");

				Optional<ButtonType> resultado = confirmacao.showAndWait();

				if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

					consulta.setInt(1, cliente.getId());
					consulta.executeUpdate();

					Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
					sucesso.setContentText("Cliente excluído com sucesso!");
					sucesso.showAndWait();

				} else {
					Alert cancelado = new Alert(Alert.AlertType.CONFIRMATION);
					cancelado.setContentText("Exclusão cancelada");
					cancelado.showAndWait();
				}

			} else {
				Alert mensagem = new Alert(Alert.AlertType.ERROR);
				mensagem.setContentText("Cliente não encontrado!");
				mensagem.showAndWait();

			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public void ativaDesativa(ClienteModel cliente) {
		String sql;
		
		if (cliente.getStatus().equals("ATIVO")) {
			sql = "UPDATE clientes SET status= 'INATIVO' WHERE id = ?";
		} else {
			sql = "UPDATE clientes SET status= 'ATIVO' WHERE id = ?";
		}
		
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement(sql)) {
			
			consulta.setInt(1, cliente.getId());

			consulta.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
