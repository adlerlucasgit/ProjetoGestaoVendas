package application.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.model.MovClienteModel;
import application.util.Conexao;

public class MovClienteDAO {

    public List<MovClienteModel> buscarPorCliente(int clienteId) {

        List<MovClienteModel> lista = new ArrayList<>();

        String sql = "SELECT id, data, total, status " +
                     "FROM vendas " +
                     "WHERE cliente_id = ? " +
                     "ORDER BY data DESC LIMIT 5";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MovClienteModel mov = new MovClienteModel(
                        rs.getInt("id"),
                        rs.getTimestamp("data").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("status")
                );

                lista.add(mov);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
}