package client;

import db.DBHandler;

public class Client {

	public static void main(String[] args) {
		try(DBHandler dbh = new DBHandler(args[0], args[1])) {
			
			
			
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
