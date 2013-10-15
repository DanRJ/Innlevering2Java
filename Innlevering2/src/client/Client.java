package client;

import java.util.Map;

import maintenance.Account;
import maintenance.AccountMaintenance;

public class Client {

	public static void main(String[] args) {
		try(AccountMaintenance acmt = new AccountMaintenance(args[0], args[1])) {
			
//			Map<String, Account> test = AccountMaintenance.getAccounts("accounts");
//			Account account = AccountMaintenance.getAccount("accounts", "22222222");
			
			AccountMaintenance.updateAccounts("accounts", "accountupdate");
			
			
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
