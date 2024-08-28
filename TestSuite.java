package ver11_dev;

import java.util.ArrayList;

public class TestSuite {

	public static ArrayList<String> test00() {
		System.out.println("test00");
		ArrayList<String> results = new ArrayList<>();

		results.add("BasketballPlayer: getTotalPoints()");
		results.add("20");  // Problem worth (points)

		try {
			BasketballPlayer p = new BasketballPlayer("Paul");
			p.shootThreePointer(false);
			p.shootTwoPointer(false);
			p.shootFreeThrow(true);
			p.shootFreeThrow(true);
			p.shootTwoPointer(true);
			p.shootThreePointer(false);
			p.shootThreePointer(true);
			p.shootFreeThrow(false);
			p.shootTwoPointer(true);
			p.shootThreePointer(false);
			p.shootThreePointer(true);

			int numPoints = p.getTotalPoints();
//			String msgExpected = String.format("getTotalPoints()=%d", 12);
			String msgActual =   String.format("3-f,2-f,1-t,1-t,2-t,3-f,3-t,1-f,2-t,3-f,3-t: getTotalPoints()=%d", numPoints);
			
			results.add(msgActual);
		}
		catch(RuntimeException ex) {
			results.add(ex.toString());
		}
		return results;
	}

	public static ArrayList<String> test01() {
		ArrayList<String> results = new ArrayList<>();
		results.add("Account constructor");
		results.add("20.0");
		try{
			Account acnt = new Account("Xavier", 1000.0);
			results.add("acnt.getName()=" + acnt.getName());
			//results.add("acnt.getName()=" + acnt.getName());
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		try{
			Account acnt = new Account("Xavier", 1000.0);
			String bal = String.format("$%,.2f", acnt.getBalance());
			results.add("acnt.getBalanace()=" + bal);
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		return results;
	}

	public static ArrayList<String> test02() {
		ArrayList<String> results = new ArrayList<>();
		results.add("getBalance after multiple deposit/withdrawals");
		results.add("10.0");  // Problem worth (points)
		try{
			
			Account acnt = new Account("Xavier", 2851.86);
			String bal = String.format("$%,.2f", acnt.getBalance());
			results.add("%d " + acnt.getBalance() + " %tp 1.0 acnt.getBalanace()=" + bal);
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		try {
			Account acnt = new Account("Xavier", 3618.5);
			String bal = String.format("$%,.2f", acnt.getBalance());
			results.add("%d " + acnt.getBalance() + " %ta 5.0 acnt.getBalanace()=" + bal);
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		try {
			Account acnt = new Account("Xavier", 1000.0);
			results.add("acnt.isOverdrawn()= " + acnt.isOverdrawn());
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}
		return results;
	}
	
	private Account buildAccount() {
		return null;
	}

	public static ArrayList<String> test03() {
		ArrayList<String> results = new ArrayList<>();
		results.add("Account aggregate test");
		results.add("10.0");  // Problem worth (points)
		try{
			Account acnt = new Account("Walter", 1000.0);
			StringBuilder res = new StringBuilder();
			res.append("acnt.getName()=" + acnt.getName() + ", ");
			res.append("acnt.getNumWithdrawals()=" + acnt.getNumWithdrawals() + ", ");
			res.append("acnt.isOverdrawn()= " + acnt.isOverdrawn());
			results.add(res.toString());
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}
		return results;
	}
	
	public static ArrayList<String> testManually_toString() {
		ArrayList<String> results = new ArrayList<>();
		results.add("---> GRADE MANUALLY <---, toString");
		Account acnt = new Account("Walter", 1000.0);
		results.add(acnt.toString());
		return results;
	}

	public static ArrayList<String> testManually_getPay() {
		ArrayList<String> results = new ArrayList<>();
		results.add("---> GRADE MANUALLY <---, getBalance()");
		Account acnt = new Account("Walter", 1000.0);
		results.add("getBalance()="+acnt.getBalance());
		return results;
	}

	public static ArrayList<String> testManually_BasketBallPlayer_toString() {
		System.out.println("test manual");
		ArrayList<String> results = new ArrayList<>();
		results.add("---> GRADE MANUALLY <---, bballPlayer.toString()");
		BasketballPlayer p = new BasketballPlayer("Paul");
		p.shootThreePointer(false);
		p.shootTwoPointer(false);
		p.shootFreeThrow(true);
		p.shootFreeThrow(true);
		p.shootTwoPointer(true);
		p.shootThreePointer(false);
		p.shootThreePointer(true);
		p.shootFreeThrow(false);
		p.shootTwoPointer(true);
		p.shootThreePointer(false);
		p.shootThreePointer(true);

		results.add(p.toString());
		return results;
	}

	
}
