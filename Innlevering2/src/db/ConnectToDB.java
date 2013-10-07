package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Laget av:
 * Daniel Rustad Johansen
 */

public class ConnectToDB implements AutoCloseable {
	private static final String URL = "jdbc:mysql://localhost/";
	private Connection connection;
	
	public ConnectToDB (String bruker, String passord) throws SQLException {
		this(bruker, passord, "Innlevering2");
	}
	
	public ConnectToDB (String bruker, String passord, String dbName) throws SQLException {
		connection = DriverManager.getConnection(URL + dbName, bruker, passord);
	}
	
	public void close() {
		try {
			connection.close();
			System.out.println("ConnToDB lukket!");
		} catch (SQLException e) {
			System.out.println("Ingen connection � stoppe!");
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return connection;
	}
}
