package ver11_dev;

/**
 * Sample class used to illustrate how tests are written in <code>TestSuite</code>.
 */
public class Account {
	private double balance;
	private String name;
	private int numWithdrawals = 0;
	private boolean isOverdrawn = false;

	public Account(String name, double balance) {
		this.name = name;
		this.balance = balance;
	}

	public int getNumWithdrawals() {
		return numWithdrawals;
	}

	public boolean isOverdrawn() {
		throw new IndexOutOfBoundsException("index must be >0");
		//return isOverdrawn;
	}

	public double getBalance() {
		return balance;
	}

	public String getName() {
		return name;
	}

	public void deposit(double amount) {
		if(amount>0.0) {
			balance += amount;
			if(isOverdrawn && balance>=0) {
				isOverdrawn = false;
			}
		}
	}

	public void withdraw(double amount) {
		if(amount>0.0) {
			balance -= amount;
			numWithdrawals++;
			if(balance < 0.0) {
				isOverdrawn = true;
			}
		}
	}

	@Override
	public String toString() {
		String msg = String.format("Account: name=%s, balance=$%,.2f, num withdrawals=%d, isOverdrawn=%b",
				name, balance, numWithdrawals, isOverdrawn);
		return msg;
	}

}

