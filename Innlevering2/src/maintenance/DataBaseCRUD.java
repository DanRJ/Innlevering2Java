package maintenance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import maintenance.AccountUpdate;
import maintenance.Account;

import db.ConnectToDB;

public class DataBaseCRUD implements AutoCloseable{
	private static final String QUERY = "SELECT * FROM ";
	private static final int NUMBER_OF_COLUMNS = 3;
	private ConnectToDB db;
	private Connection connection;
	
	public DataBaseCRUD(String user, String password) throws SQLException {
		db = new ConnectToDB(user, password);
		connection = db.getConnection();
	}
	
	/**
	 * Sends a query to a database, returns the row data in a List
	 * @param query			a String which is an SQL query
	 * @return				a List<String>
	 * @throws SQLException
	 */
	public List<String> dbQuery(String query) throws SQLException {
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
	
	/**
	 * Collects everything from a table, and puts them into a Map<String, Account>
	 * 
	 * @param tableName					a table in the database
	 * @return A Map<String, Account>	a Map
	 * @throws SQLException				
	 */
	public Map<String, Account> getAccountsFromDB(String tableName) throws SQLException {
			
			Map<String, Account> mapOfAccounts = new HashMap<String, Account>();
			
			List<String> content = dbQuery(QUERY + tableName);
			List<Account> newAccountObjects = makeNewAccounts(content, NUMBER_OF_COLUMNS);
			
			for (Account account2 : newAccountObjects) {
				mapOfAccounts.put(String.valueOf(account2.getAccountNumber()), account2);
			}
			
			return mapOfAccounts;
	}
	
	/**
	 * Sends a query into the database
	 * 
	 * @param query2		a string that is an SQL query
	 * @throws SQLException 
	 */
	private void sendStatementIntoDB(String query2) throws SQLException {
		try(Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(query2);
		}
	}

	
	/**
	 * Returns a String which is an INSERT query in SQL with values for an Account.
	 * 
	 * @param tableName		a table in the database
	 * @param inAccount		an Account object
	 * @return				a String which is a SQL query
	 */
	public String insertIntoDB(String tableName, Account inAccount) {
			String query = "INSERT INTO " + tableName + " VALUES (";
			String values = inAccount.getAccountNumber() + ", " + inAccount.getBalance() + ", " + inAccount.getInterest() + " );";
			
			query += values;
			
			return query;
	}

	/**
	 * Updates the row values in a table
	 * 
	 * @param inAccountMap	a Map of Accounts
	 * @param tableName		name of the table
	 * @throws SQLException	
	 */
	public void updateValuesInDB(Map<String, Account> inAccountMap, String tableName) throws SQLException {
		Map<String, Account> dbCheck = getAccountsFromDB(tableName);
		String query = "UPDATE " + tableName + "\n";
		
		for (String key : inAccountMap.keySet()) {
			String set = "SET balance = '" + inAccountMap.get(key).getBalance() + "', interest = '" + inAccountMap.get(key).getInterest() + "'\n";
			String where = "WHERE accountNumber = '" + inAccountMap.get(key).getAccountNumber() + "';";
			
			query += set + where;
			
			sendStatementIntoDB(query);
			
			query = "UPDATE " + tableName + "\n";
			
			if (!dbCheck.containsKey(key)) {
				sendStatementIntoDB(insertIntoDB(tableName, inAccountMap.get(key)));
			}
		}
		
	}
	
	/**
	 * Returns a List of Account objects,
	 * it receives a list of Strings which will be converted
	 * to Accounts and put into a List.
	 * 
	 * @param content			a List of Strings
	 * @param numberOfColumns	the number of columns in the database table
	 * @return 					a List of Accounts
	 */
	private List<Account> makeNewAccounts(List<String> content, int numberOfColumns) {
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

	/**
	 * Returns a List of AccountUpdate objects,
	 * it receives a list of Strings which will be converted
	 * into AccountUpdate objects and put into a List.
	 * 
	 * @param content			a List of Strings
	 * @param numberOfColumns	the number of columns in the database table
	 * @return
	 */
	public List<AccountUpdate> makeNewAccountUpdates(List<String> content, int numberOfColumns) {
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
	 * An extremely crude way of inserting default rows 
	 * but great for troubleshooting, so I will leave it in.
	 * The way to use it is to write this in the client-class:
	 * dbCRUD.insertDefaultRowsInTable(NameOfTable);
	 * 
	 * @param tableName		name of a database table
	 * @throws SQLException
	 */
	public void insertDefaultRowsInTable(String tableName) throws SQLException {
		Account acc1, acc2, acc3;
		List<Account> listOfAcc = new ArrayList<Account>();
		
		deleteRowsFromTable(tableName);
		
		String queryInsert = "";
		acc1 = new Account(11111111, 1000, 3.50);
		acc2 = new Account(22222222, 2000, 2.50);
		acc3 = new Account(33333333, 3000, 1.50);
		
		listOfAcc.add(acc1);
		listOfAcc.add(acc2);
		listOfAcc.add(acc3);
		
		for (int i = 0; i < listOfAcc.size(); i++) {
			queryInsert = insertIntoDB(tableName, listOfAcc.get(i));
			sendStatementIntoDB(queryInsert);
			queryInsert = "";
		}
	}
	
	/**
	 * A crude way to reset a table.
	 * This is not part of the assignment but great for troubleshooting, so I will leave it in.
	 * 
	 * @throws SQLException
	 */
	public void deleteRowsFromTable(String tableName) throws SQLException {
			String queryDelete = "DELETE FROM " + tableName;
			sendStatementIntoDB(queryDelete);
	}
	

	/**
	 * Is implemented automatically when using AutoClosable
	 */
	@Override
	public void close() throws Exception {
		connection.close();
		db.close();
	}
}
