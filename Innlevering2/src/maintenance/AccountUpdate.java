package maintenance;

/**
 * AccountUpdate class
 * 
 * @author Daniel Rustad Johansen
 *
 */
public class AccountUpdate {
	private int accountNumber;
	private String strUpdate;
	private double strValue;
	
	public AccountUpdate() {
		this(0, "", 0.0);
	}
	
	public AccountUpdate(int inAccountNumber, String inStrUpdate, double inStrValue) {
		setAccountNumber(inAccountNumber);
		setStrUpdate(inStrUpdate);
		setStrValue(inStrValue);
	}

	public int getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(int inAccountNumber) {
		accountNumber = inAccountNumber;
	}

	public String getStrUpdate() {
		return strUpdate;
	}
	public void setStrUpdate(String inStrUpdate) {
		strUpdate = inStrUpdate;
	}

	public double getStrValue() {
		return strValue;
	}
	public void setStrValue(double inStrValue) {
		strValue = inStrValue;
	}
	
	public String toString() {
		return accountNumber + "\t" + strUpdate + "\t" + strValue;
	}
}
