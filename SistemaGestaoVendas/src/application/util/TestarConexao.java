package application.util;

import java.sql.Connection;

public class TestarConexao {
    public static void main(String[] args) {
        try {
            Connection conn = Conexao.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✔ Conexão estabelecida com sucesso!");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("✘ Falha ao conectar:");
            e.printStackTrace();
        }
    }
}