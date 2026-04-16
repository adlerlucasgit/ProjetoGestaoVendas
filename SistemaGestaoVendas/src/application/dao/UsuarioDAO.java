package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.model.UsuarioModel;
import application.util.Conexao;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UsuarioDAO {
	public static boolean encontrou;
    public UsuarioModel autenticar(String login, String senha) {
        try {
            Connection conn = Conexao.getConnection();
            String sql = "SELECT * FROM usuarios WHERE BINARY login = ? AND BINARY senha = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UsuarioModel u = new UsuarioModel(0, sql, sql, sql, sql);
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setTipo(rs.getString("tipo"));
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean inserirUsuario(UsuarioModel m) {
        try {
            Connection conn = Conexao.getConnection();

            String sql = "INSERT INTO usuarios (nome, login, senha, tipo) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, m.getNome());
            stmt.setString(2, m.getLogin());
            stmt.setString(3, m.getSenha());
            stmt.setString(4, m.getTipo());

            boolean resultado = stmt.executeUpdate() > 0;            
            conn.close();

            return resultado;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean loginExiste(String login) {
        try {
            Connection conn = Conexao.getConnection();
            String sql = "SELECT COUNT(*) FROM usuarios WHERE login = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
	public static List<UsuarioModel> listarTodos(String valor) {
		String sql = "";

		List<UsuarioModel> lista = new ArrayList<>();
		if(valor == null) {
			sql = "select * from usuarios";
		} else {
			sql = "select * from usuarios where id like ? or nome like ? or login like ? or tipo like ?";
		}
		
		try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			if(valor != null) {
				stmt.setString(1, "%"+valor+"%");
				stmt.setString(2, "%"+valor+"%");
				stmt.setString(3, "%"+valor+"%");
				stmt.setString(4, "%"+valor+"%");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				UsuarioModel user = new UsuarioModel(rs.getInt("id"), rs.getString("nome"),
						rs.getString("login"), rs.getString("senha"), rs.getString("tipo"));

				lista.add(user);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lista;
	}
	
	public UsuarioModel Buscar(String valor) {
		String busca = "%" + valor + "%";
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement(
						"select * from usuarios where id like ? or nome like ? or login like ? or tipo like ?");) {
			consulta.setString(1, busca);
			consulta.setString(2, busca);
			consulta.setString(3, busca);
			consulta.setString(4, busca);

			ResultSet resultado = consulta.executeQuery();
			if (resultado.next()) {
				return new UsuarioModel(
		                resultado.getInt("id"),
		                resultado.getString("nome"),
		                resultado.getString("login"),
		                null,
		                resultado.getString("tipo")
						);
			} else {
				Alert mensagem = new Alert(Alert.AlertType.ERROR);
				mensagem.setContentText("Usuário não encontrado!");
				encontrou = false;
				mensagem.showAndWait();
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		

	}
	
	public void Editar(UsuarioModel usuario) {
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement(
						"UPDATE usuarios SET nome =?, login=?, tipo=? WHERE id = ?")) {
			

			consulta.setString(1, usuario.getNome());
			consulta.setString(2, usuario.getLogin());
			consulta.setString(3, usuario.getTipo());
			consulta.setInt(4, usuario.getId());

			consulta.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
	
	public void Excluir(UsuarioModel usuario) {
		try (Connection conn = Conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement("delete from usuarios where id=?");) {

			if (usuario.getId() > 0) {
				Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
				confirmacao.setTitle("Confirmação");
				confirmacao.setHeaderText("Excluir usuário");
				confirmacao.setContentText("Deseja realmente excluir o usuário: " + usuario.getNome() + "?");

				Optional<ButtonType> resultado = confirmacao.showAndWait();

				if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

					consulta.setInt(1, usuario.getId());
					consulta.executeUpdate();

					Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
					sucesso.setContentText("Usuário excluído com sucesso!");
					sucesso.showAndWait();

				} else {
					Alert cancelado = new Alert(Alert.AlertType.CONFIRMATION);
					cancelado.setContentText("Exclusão cancelada");
					cancelado.showAndWait();
				}

			} else {
				Alert mensagem = new Alert(Alert.AlertType.ERROR);
				mensagem.setContentText("Usuário não encontrado!");
				mensagem.showAndWait();

			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

}
