package maintenance;

public class Account {
	private int accountNumber;
	private int balance;
	private double interest;
	
	public Account() {
		this(0, 0, 0.0);
	}
	
	public Account(int inAccountNumber, int inBalance, double inInterest) {
		setAccountNumber(inAccountNumber);
		setBalance(inBalance);
		setInterest(inInterest);
	}

	public void increaseBalance(int addToBalance) {
		balance += addToBalance;
	}
	
	public void decreaseBalance(int removeFromBalance) {
		balance -= removeFromBalance;
	}
	
	public void increaseInterest(double addToInterest) {
		interest += addToInterest;
	}
	
	public void decreaseInterest(double removeFromInterest) {
		interest -= removeFromInterest;
	}
	
	public void setAccountNumber(int inAccountNumber) {
		accountNumber = inAccountNumber;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}
	
	public void setBalance(int inBalance) {
		balance = inBalance;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setInterest(double inInterest) {
		interest = inInterest;
	}
	
	public double getInterest() {
		return interest;
	}
	
	
	public String toString() {
		return getAccountNumber() + "\t" + getBalance() + "\t" + getInterest();
	}


}
