package maintenance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import db.ConnectToDB;

public class AccountMaintenance implements AutoCloseable {
	private static final String QUERY = "SELECT * FROM ";
	private ConnectToDB db;
	private static Connection connection;

	public AccountMaintenance(String user, String password) throws SQLException {
		db = new ConnectToDB(user, password);
		connection = db.getConnection();
	}

	public static void updateAccounts(String tableName, String tableUpdate) throws SQLException {
		Map<String, Account> accounts = getAccounts(tableName);
		
	}

	public static Map<String, Account> getAccounts(String tableName) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			
			Map<String, Account> mapOfAccounts = new HashMap<String, Account>();
			
			List<String> content = dbQuery(QUERY + tableName);
			List<Account> newAccountObjects = makeNewAccounts(content, 3);
			
			for (Account account2 : newAccountObjects) {
				mapOfAccounts.put(String.valueOf(account2.getAccountNumber()), account2);
			}
			
			return mapOfAccounts;
		}
	}
	
	public static Account getAccount(String tableName, String accountNumber) throws SQLException {
		String getAccQuery = tableName + " WHERE ACCOUNTNUMBER = " + accountNumber;
		
		List<String> accountData = dbQuery(QUERY + getAccQuery);
		
		Account account = new Account(
				Integer.valueOf(accountData.get(0)),
				Integer.valueOf(accountData.get(1)), 
				Double.valueOf(accountData.get(2))
				);
		
		return account;
	}
	
	public static List<String> dbQuery(String query) throws SQLException {
		try(Statement stmt = connection.createStatement()) {
			
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsMetaData = rs.getMetaData();
			
			List<String> content = new ArrayList<>();
			
			while(rs.next()) {
				for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
					content.add(rs.getObject(i).toString());
				}
			}
			return content;
		}
	}

	
	private static List<Account> makeNewAccounts(List<String> content, int numberOfColumns) {
		List<Account> account = new ArrayList<Account>();

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
