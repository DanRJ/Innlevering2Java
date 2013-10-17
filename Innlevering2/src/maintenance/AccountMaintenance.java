package maintenance;

import java.sql.SQLException;
import java.util.*;

/**
 * This class performs a couple of different tasks coordinated towards
 * a database.
 * 
 * @author Daniel Rustad Johansen
 *
 */
public class AccountMaintenance {
	private static final String QUERY = "SELECT * FROM ";
	private static Map<String, Account> accounts;
	private static DataBaseCRUD dbCrud;

	/**
	 * Updates the first table with the parameters in the second
	 * table.
	 * 
	 * @param tableName		a table in the database
	 * @param tableUpdate	a table in the database
	 */
	public static void updateAccounts(String tableName, String tableUpdate) {
		// Makes new accounts and maps them out
		accounts = getAccounts(tableName);

		// Sends a query to the db, extracts data and puts in a list
		List<String> content;

		//I used try/catch instead of letting the 
		//method throw an SQLException,
		//since I thought we couldnt change the assignment
		try {
			content = dbCrud.dbQuery(QUERY + tableUpdate);
			LinkedList<AccountUpdate> accountUpdates = (LinkedList<AccountUpdate>) 
														dbCrud.makeNewAccountUpdates(content, 3);

			for (AccountUpdate key : accountUpdates) {
				if (accounts.containsKey(String.valueOf(key.getAccountNumber()))) {
					applyStrUpdates(key);
					
				} else {
					int checkAccNumber = key.getAccountNumber();
					
					if (String.valueOf(checkAccNumber).length() == 8) {
						accounts.put(
									String.valueOf(checkAccNumber),
									new Account(
										key.getAccountNumber(), 
										Integer.valueOf(key.getStrUpdate()), 
										key.getStrValue()));
					}
				}
			}// End foreach
			
			dbCrud.updateValuesInDB(accounts, tableName);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a Map of the objects from the specified table
	 * 
	 * @param tableName		a table in the database
	 * @return				a Map<String, Account>
	 */
	public static Map<String, Account> getAccounts(String tableName) {
		//I used try/catch instead of letting the 
		//method throw an SQLException,
		//since I thought we couldnt change the assignment
		try {
			return dbCrud.getAccountsFromDB(tableName);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a specific Account object from the specified table
	 * 
	 * @param tableName		a String that represents a tablename
	 * @param accountNumber	the number of the account which will be returned
	 * @return				an account
	 */
	public static Account getAccount(String tableName, String accountNumber) {
		String getAccQuery = tableName + " WHERE ACCOUNTNUMBER = " + accountNumber;

		List<String> accountData;
		Account account = new Account();
		
		//I used try/catch instead of letting the 
		//method throw an SQLException,
		//since I thought we couldnt change the assignment
		try {

			accountData = dbCrud.dbQuery(QUERY + getAccQuery);

			account = new Account(
					Integer.valueOf(accountData.get(0)),
					Integer.valueOf(accountData.get(1)),
					Double.valueOf(accountData.get(2)));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return account;
	}

	/**
	 * Checks the strValue and applies the update
	 * 
	 * @param key	an accountupdate object
	 */
	private static void applyStrUpdates(AccountUpdate key) {
		//DRY principle in effect
		String getUpdateValueAtKey = key.getStrUpdate();
		double getValueAtKey = key.getStrValue();
		String IntToString = String.valueOf(key.getAccountNumber());
		Account accountGets = accounts.get(IntToString);

		if (getUpdateValueAtKey.equals("b+")) {
			accountGets.increaseBalance((int) getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("b-")) {
			accountGets.decreaseBalance((int) getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("i+")) {
			accountGets.increaseInterest(getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("i-")) {
			accountGets.decreaseInterest(getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("b")) {
			accountGets.setBalance((int) getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("i")) {
			accountGets.setInterest(getValueAtKey);
			
		} else if (getUpdateValueAtKey.equals("")) {
			System.out.println("No update value found!");
			
		}
	}

	/**
	 * Gives AccountMaintenance the connection that was
	 * established in the client class
	 * 
	 * @param inDbCrud	a database connection
	 */
	public static void setConnection(DataBaseCRUD inDbCrud) {
		dbCrud = inDbCrud;
	}

}
