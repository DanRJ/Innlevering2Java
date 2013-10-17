package db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * ConnectToDB class
 * 
 * @author Daniel Rustad Johansen
 *
 */
public class ConnectToDB implements AutoCloseable {
	private static final String URL = "localhost";
	private Connection connection;
	private MysqlDataSource ds;
	
	public ConnectToDB (String bruker, String passord) throws SQLException {
		this(bruker, passord, "innlevering2");
	}
	
	public ConnectToDB (String user, String password, String dbName) throws SQLException {
		ds = new MysqlDataSource();
		ds.setUser(user);
		ds.setPassword(password);
		ds.setServerName(URL);
		ds.setDatabaseName(dbName);
		
		connection = ds.getConnection();
	}
	
	public void close() {
		try {
			connection.close();
			System.out.println("Connection to DB closed!");
		} catch (SQLException e) {
			System.out.println("No connection to close!");
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return connection;
	}
}
