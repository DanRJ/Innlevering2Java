package db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBHandler implements AutoCloseable{
	
	private ConnectToDB db;
	private Connection connection;

	public DBHandler(String user, String password) throws SQLException {
		db = new ConnectToDB(user, password);
		connection = db.getConnection();
	}
	
	public void close() throws Exception {
		db.close();
	}
}
