package maintenance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import db.ConnectToDB;

public class AccountMaintenance implements AutoCloseable {
	private ConnectToDB db;
	private static Connection connection;

	public AccountMaintenance(String user, String password) throws SQLException {
		db = new ConnectToDB(user, password);
		connection = db.getConnection();
	}

	public static void updateAccounts(String tableName, String tableUpdate)
			throws SQLException {
		getAccounts(tableName);
		
	}

	public static Map<String, Account> getAccounts(String tableName)
			throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			
			Map<String, Account> mapOfAccounts = new HashMap<String, Account>();
			
			String query = "Select * from " + tableName;

			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsMetaData = rs.getMetaData();

			ArrayList<String> content = new ArrayList<String>();

			while (rs.next()) {
				for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
					content.add(rs.getObject(i).toString());
				}
			}

			List<Account> makeNewAccounts = makeNewAccounts(content, rsMetaData.getColumnCount());
			
			for (Account account2 : makeNewAccounts) {
				mapOfAccounts.put(String.valueOf(account2.getAccountNumber()),
						account2);
			}
			return mapOfAccounts;
		}
	}

	public static Account getAccount(String tableName, String accountNumber) {
		return null;
	}
	
	private static List<Account> makeNewAccounts(ArrayList<String> content, int numberOfColumns) {
		ArrayList<Account> account = new ArrayList<Account>();

		for (int i = 0; i < content.size(); i += numberOfColumns) {
			account.add(new Account(
					Integer.valueOf(content.get(i)),
					Integer.valueOf(content.get(i + 1)), 
					Double.valueOf(content.get(i + 2))
					));
		}
		return account;

	}
	@Override
	public void close() throws Exception {
		db.close();
	}
}
