package br.com.thiagobarso.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	public Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:postgresql://DSBD01:5432/rdf2rdb",
					"thiagosoares", "@)!$thiago");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
