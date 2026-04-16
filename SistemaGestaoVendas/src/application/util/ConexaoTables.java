package application.util;
import java.sql.*;

public class ConexaoTables {
    public static void main(String[] args) {
        try (Connection conn = Conexao.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            System.out.println("Conexão OK. Tabelas encontradas:");
            while (rs.next()) {
                System.out.println("- " + rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}