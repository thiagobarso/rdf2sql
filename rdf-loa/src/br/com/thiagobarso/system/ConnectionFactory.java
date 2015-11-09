package br.com.thiagobarso.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ConnectionFactory {
	
	public Connection getConnection(Map<String, String> prop) {
		String banco = prop.get("database");
		String usuario = prop.get("user");
		String senha = prop.get("password");
		String host = prop.get("host");
		String porta = prop.get("port");
		try {
			return DriverManager.getConnection("jdbc:postgresql://" + host
					+ ":" + porta + "/" + banco, usuario, senha);
		} catch (SQLException e) {
			System.out
					.println("Ops!Algo deu errado com a conexao da aplicação com o banco de dados!");
			throw new RuntimeException(e);
		}
	}
}
