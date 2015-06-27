package br.com.thiagobarso.system;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
	public Connection getConnection() {
		String banco = null;
		String usuario = null;
		String senha = null;
		String host = null;
		String porta = null;

		try {
			Properties prop = DoRdf.getProp();
			banco = prop.getProperty("prop.server.banco");
			usuario = prop.getProperty("prop.server.usuario");
			senha = prop.getProperty("prop.server.senha");
			host = prop.getProperty("prop.server.host");
			porta = prop.getProperty("prop.server.porta");
		} catch (IOException e) {
			System.out
					.println("Ops!Algo deu errado com o nome da localização do arquivo rdf! ");
			System.out.println("Reveja o arquivo dados.properties na pasta desse jar.");
			e.printStackTrace();
		}

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
