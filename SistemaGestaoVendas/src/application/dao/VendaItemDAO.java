package application.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import application.model.VendaItemModel;
import application.util.Conexao;

public class VendaItemDAO {

    public List<VendaItemModel> buscarPorVenda(int vendaId) {

        List<VendaItemModel> lista = new ArrayList<>();

        String sql = "SELECT vi.id, vi.venda_id, vi.produto_id, " +
                     "p.descricao AS produto, vi.quantidade, vi.preco " +
                     "FROM vendaItens vi " +
                     "JOIN produtos p ON p.id = vi.produto_id " +
                     "WHERE vi.venda_id = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vendaId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                VendaItemModel item = new VendaItemModel(
                        rs.getInt("id"),
                        rs.getInt("venda_id"),
                        rs.getInt("produto_id"),
                        rs.getString("produto"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco")
                );

                lista.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}