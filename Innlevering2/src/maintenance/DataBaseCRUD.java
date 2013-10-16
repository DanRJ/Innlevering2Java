package maintenance;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataBaseCRUD {
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

	public static String insertIntoDB(String tableName, Account inAccount) throws SQLException {
			String query = "INSERT INTO " + tableName + " VALUES (";
			String values = inAccount.getAccountNumber() + ", " + inAccount.getBalance() + ", " + inAccount.getInterest() + " );";
			
			query += values;
			
			return query;
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
}
