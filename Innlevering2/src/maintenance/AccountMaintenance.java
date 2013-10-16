package maintenance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import db.ConnectToDB;

public class AccountMaintenance implements AutoCloseable {
	private static final String QUERY = "SELECT * FROM ";
	private static final int NUMBER_OF_COLUMNS = 3;
	private static Map<String, Account> accounts;
	private ConnectToDB db;
	private static Connection connection;

	public AccountMaintenance(String user, String password) throws SQLException {
		db = new ConnectToDB(user, password);
		connection = db.getConnection();
	}

	public static void updateAccounts(String tableName, String tableUpdate) throws SQLException {
		//Mapper ut accounts
		accounts = getAccounts(tableName);
		
		//Query databasen, henter ut data, legger det i en list
		List<String> content = dbQuery(QUERY + tableUpdate);
		
		LinkedList<AccountUpdate> accountUpdates = (LinkedList<AccountUpdate>) makeNewAccountUpdates(content, NUMBER_OF_COLUMNS);

		for (AccountUpdate key : accountUpdates) {
			if (accounts.containsKey(String.valueOf(key.getAccountNumber()))) {
				
				applyStrUpdates(key);
				
			}else {
				int checkAccNumber = key.getAccountNumber();
				if (String.valueOf(checkAccNumber).length() == 8) {
					accounts.put(
							String.valueOf(checkAccNumber), 
							new Account(
										key.getAccountNumber(),
										Integer.valueOf(key.getStrUpdate()),
										key.getStrValue()
										)
							);
				}
			}
		}//End foreach
		updateValuesInDB(accounts, tableName);
	}

	public static void updateValuesInDB(Map<String, Account> inAccountMap, String tableName) throws SQLException {
		//UPDATE tableName
		//SET balance = 'balansen i mappet', interest = 'interesten i mappet'
		//WHERE accountNumber = 'accountnumber i mappen'
		Map<String, Account> dbCheck = getAccounts(tableName);
		String query = "UPDATE " + tableName + "\n";
		
		for (String key : inAccountMap.keySet()) {
			String set = "SET balance = '" + inAccountMap.get(key).getBalance() + "', interest = '" + inAccountMap.get(key).getInterest() + "'\n";
			String where = "WHERE accountNumber = '" + inAccountMap.get(key).getAccountNumber() + "';";
			
			query += set + where;
			
			statementIntoDB(query);
			
			query = "UPDATE " + tableName + "\n";
			
			if (!dbCheck.containsKey(key)) {
				statementIntoDB(insertIntoDB(tableName, inAccountMap.get(key)));
			}
		}
		
	}
	
	private static void statementIntoDB(String query2) throws SQLException {
		try(Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(query2);
		}
		
	}

	public static String insertIntoDB(String tableName, Account inAccount) throws SQLException {
			String query = "INSERT INTO " + tableName + " VALUES (";
			String values = inAccount.getAccountNumber() + ", " + inAccount.getBalance() + ", " + inAccount.getInterest() + " );";
			
			query += values;
			
			return query;
	}
	
	private static void applyStrUpdates(AccountUpdate key) {
		//Variabelen nedenfor blir brukt hyppig under de igjen:
		String getUpdateValueAtKey = key.getStrUpdate();
		double getValueAtKey = key.getStrValue();
		String IntToString = String.valueOf(key.getAccountNumber());
		
		if (getUpdateValueAtKey.equals("b+")) {
			accounts.get(IntToString).increaseBalance((int) getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("b-")) {
			accounts.get(IntToString).decreaseBalance((int) getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("i+")) {
			accounts.get(IntToString).increaseInterest(getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("i-")) {
			accounts.get(IntToString).decreaseInterest(getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("b")) {
			accounts.get(IntToString).setBalance((int) getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("i")) {
			accounts.get(IntToString).setInterest(getValueAtKey);
		}
		else if (getUpdateValueAtKey.equals("")) {
			System.out.println("No update value found!");
		}

	}
	
	public static Map<String, Account> getAccounts(String tableName) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			
			Map<String, Account> mapOfAccounts = new HashMap<String, Account>();
			
			List<String> content = dbQuery(QUERY + tableName);
			List<Account> newAccountObjects = makeNewAccounts(content, NUMBER_OF_COLUMNS);
			
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
	
	private static List<AccountUpdate> makeNewAccountUpdates(List<String> content, int numberOfColumns) {
		List<AccountUpdate> accountUpdates = new LinkedList<AccountUpdate>();
		
		for (int i = 0; i < content.size(); i += numberOfColumns) {
			accountUpdates.add(new AccountUpdate(
					Integer.valueOf(content.get(i)),
					String.valueOf(content.get(i + 1)),
					Double.valueOf(content.get(i + 2))
					));
		}
		
		return accountUpdates;
	}
	
	/**
	 * A crude way to reset a table.
	 * 
	 * @throws SQLException
	 */
	public void deleteRowsFromTable(String tableName) throws SQLException {
		try(Statement stmt = connection.createStatement()) {
			String queryDelete = "DELETE FROM " + tableName;
			stmt.executeUpdate(queryDelete);
		}
	}
	
	/**
	 * An extremely crude way of inserting default rows
	 * 
	 * @param tableName
	 * @throws SQLException
	 */
	public void insertDefaultRowsInTable(String tableName) throws SQLException {
		Account acc1, acc2, acc3;
		List<Account> listOfAcc = new ArrayList<Account>();
		
		deleteRowsFromTable(tableName);
		
		try(Statement stmt = connection.createStatement()) {
			String queryInsert = "";
			acc1 = new Account(11111111, 1000, 3.50);
			acc2 = new Account(22222222, 2000, 2.50);
			acc3 = new Account(33333333, 3000, 1.50);
			
			listOfAcc.add(acc1);
			listOfAcc.add(acc2);
			listOfAcc.add(acc3);
			
			for (int i = 0; i < listOfAcc.size(); i++) {
				queryInsert = insertIntoDB(tableName, listOfAcc.get(i));
				stmt.executeUpdate(queryInsert);
				queryInsert = "";
			}
 		}
	}
	
	
	
	@Override
	public void close() throws Exception {
		connection.close();
		db.close();
	}
}
