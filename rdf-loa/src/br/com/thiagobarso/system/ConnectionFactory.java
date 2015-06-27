package br.com.thiagobarso.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
	public Connection getConnection() {
		try {
			Properties prop = DoRdf.getProp();
			String banco = prop.getProperty("prop.server.banco");
			String usuario = prop.getProperty("prop.server.usuario");
			String senha = prop.getProperty("prop.server.senha");
			String host = prop.getProperty("prop.server.host");
			String porta = prop.getProperty("prop.server.porta");
			return DriverManager.getConnection("jdbc:postgresql://"+host+":"+ porta +"/" + banco,
					usuario, senha);
		} catch (Exception e) {
			System.out.println("Ops!Algo deu errado com a conexao da aplciação com o banco de dados!");
			throw new RuntimeException(e);
		}
	}
}
