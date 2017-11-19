import java.util.ArrayList;
import java.util.Arrays;
/*
 * Must provide an import to the classes for the assignment for which the test case methods
 * contained in this class are written against.
 */
import hw1.*;
/**
 * Contains stand-alone test cases, written as static methods, that are written against the
 * classes for an assignment. Important notes:
 * <ol>
 *
 *   <li><p>This class must contain in {@code import} statement for the classes to be graded
 *   for this assignment. Thus, this class must be compiled.
 *   <li><p>Methods must be static
 *   <li><p>Method names must contain the string "test" somewhere in the name, all others are
 *      ignored. If a test needs a helper method, then the helper method name should not
 *      contain the string "test".
 *   <li><p>Tests methods are called alphabetically, thus the order they occur in this file
 *      is not important.
  *  <li>In the output that is produced, tests are numbered 1, 2, ..., regardless of the
 *       test method name. For example, if you have these test methods in this order:
 *	 <pre>{@code
 *   test_w()
 *   test_b()
 *   b_helper()
 *   a_test() }</pre>
 *   then they will be run in this order (and output labeled as shown)
 *	 <pre>{@code
 *   a_test()  --> Test 1
 *   test_b()  --> Test 2
 *   test_w()  --> Test 3 }</pre>
 *      <p>Note that <code>b_helper</code> will not be run (because it doesn't contain the string "test"
 *      in its name. It is recommended, for clarity, to list the methods alphabetically, for example:
 *      test01, test02, etc
 *
 *   <li><p>Each test method is a test case and contains 1 or more items (parts) to be graded. A test case has a
 *       "worth", the number of points the test case is worth. For example, if a test case is worth 10 points
 *       and there are 4 parts, then each part is worth 2.5 points. Thus, if a student solution gets 3 of
 *       the 4 parts correct for this test case, then they earn 7.5 out of 10 points (More information follows).
 *
 *   <li>Every test method MUST return an {@code ArrayList<String>}. There can be no line breaks (\n)
 *       in any string in the list. Every string in the list must have this format:
 *	 <pre>{@code
 *   Element Contents
 *   ----------------
 *   0       Description of test
 *   1       Points this problem is worth
 *   2       Item to be graded
 *   3       Item to be graded
 *   ...	... }</pre>
 *       <p>Thus, each item to be graded is an individual string in the returned list. (More information follows).
 *
 *    <li>Two types of items can be graded: strings and doubles. Here, we consider only strings, in the
 *       next item we consider doubles. A string item to be graded would typically be composed of text,
 *       integers, and/or booleans. It would not contain any double results. For example two items to be
 *       graded might look like this:
 *   <pre>{@code
 *   r2.compareTo(g1)=-3
 *   r2.getId()=3, g1.getID()=6 }</pre>
 *       Which might be produced by code like this (<code>results</code> is the list that will be returned):
 *   <pre>{@code
 *   results.add("r2.compareTo(g1)= " + r2.compareTo(g1));
 *   results.add(String.format("r2.getId()=%d, g1.getId()=%d", r2.getId(), g1.getId())); }</pre>
 *       The output of the program will show the expected answer and the actual (student) answer side-by-side.
 *       Thus, it is useful to make the items to be graded to read as clear as possible so that a student
 *       can understand (some) of where their program failed. For example, the output of the program might
 *       look like this:
 *   <pre>{@code
 *   Test 4 - compareTo() and getId()
 *   Incorrect - Expected: r2.compareTo(g1)=-3 | Actual: r2.compareTo(g1)=24
 *   Correct - Expected: r2.getId()=3, g1.getID()=6 | Actual: r2.getId()=3, g1.getID()=4
 *   Summary: 1 out of 2 answers correct: 5.0 points out of 10 }</pre>
 *       String items are graded by string comparison (equals) meaning that the expected answer string
 *       is compared to the actual answer string. It is important to keep that in mind. For example, that is
 *       why you would not want any doubles in such a string as there could be small differences that
 *       don't invalidate a student's answer. For example: x=3.1415926 vs. x=3.1415927. However, there may
 *       be cases where you could include a double rounded to a certain precision. For example, if you
 *       were dealing with currency, you possibly could round so that you were comparing against a string
 *       like this: "total deposits=$123.45". Again, we deal with doubles next.
  *      <p>
 *       Finally, the designer of tests will want to be careful as to how many "answers" there are in a
 *       string that is being graded, and any dependencies between them so that you don't over-penalize
 *       errors. For example, suppose you have a class with methods to calculate the surface area and
 *       volume of a 3-d object. If you wanted to weight these differently (different max points for each)
 *       you could have two separate test methods, one to test the area, and one to test the volume. Or,
 *       it you want the max points to be the same for each, you could have a single test with two parts:
 *       one part for the area and one part for the volume.
 *    <li>Doubles are also converted to strings in test methods; however, they are marked up to indicate that
 *       they are a double and then followed by markup to indicate either the absolute tolerance or tolerance
 *       as a percent. When the grading program encounters this markup it converts the value to a double and
 *       compares it to the expected double using the tolerance. This is the idea: A part is correct if:
 *       {@code Math.abs(exp-act)<maxError}. (More on this below). For example, the output of this program for a test
 *       that contains doubles might look like this:
 *   <pre>{@code
 *   Test: 16-Testing double result
 *   Correct - Expected: Average ID=8.833333333333334 | Actual: Average ID=8.833333333333334
 *   Actual Error=0.0<0.08833333333333333=Max Error
 *   Incorrect - Expected: Average ID=8.833333333333334 | Actual: Average ID=8.333333333333334
 *   Actual Error=0.5>0.01=Max Error
 *   Summary: 1 out of 2 answers correct : 2.5 points out of 5.0 }</pre>
 *
 *       Notice that the results still show the expected and actual strings, but they also show actual
 *       and max error when comparing the doubles. The answer strings that are contained in the return of the
 *       test method would look like this:
 *   <pre>{@code
 *   %d 8.833333333333334 %tp 1.0 Average ID=8.833333333333334
 *   %d 8.833333333333334 %ta 0.01 Average ID=8.333333333333334 }</pre>
 *       Thus, the format for indicating a double is:
 *   <pre>{@code
 *   %d value %t[p|a] tol informative_string_with_double }</pre>
 *       Only one double can be contained in a string. The only thing that is graded is the double itself,
 *       not "informative_string_with_double", this is used for display. Note:
 *   <ul>
 *   <li>%tp - means you specify the tolerance as a percent of the expected value. For example:
 *   %d 1000.0 %tp 1.0 means that the max error is: 1000*(1/100) = 10 and any actual
 *   answer such that: {@code Math.abs(1000-act)<10} is correct. Note, percent is specified
 *   as a percent, not a fraction.
 *
 *   <li>%ta - means you specify the tolerance as an absolute number (which is the maximum error). For example:
 *   %d 1000.0 %ta 5.0 means any actual answer such that: {@code Math.abs(1000-act)<5} is correct.
 *   </ul>
 *       Finally, the code to produce the strings above might look like this:
 *   <pre>{@code
 *   results.add("%d " + avg + " %tp 1.0 Average ID=" + avg);
 *   results.add("%d " + avg + " %ta 0.01 Average ID=" + avg); }</pre>
 *    <li>Each "part" (answer, string, item to be graded) within a test method should be surrounded with
 *       try/catch so that if a runtime error is generated the grading code can take appropriate action so
 *       that the program can continue and mark that part as incorrect. For example, consider these
 *       two strings as items to be graded in a test method:
 *   <pre>{@code
 *         r2.compareTo(g1)=-3
 *         r2.getId()=3, g1.getID()=6 }</pre>
 *       we would use code like this:
 *   <pre>{@code
 *         try{
 *             results.add("r2.compareTo(g1)= " + r2.compareTo(g1));
 *         }
 *         catch(Exception e) {
 *             results.add(e.toString());
 *         }
 *         try{
 *             results.add(String.format("r2.getId()=%d, g1.getId()=%d", r2.getId(), g1.getId()));
 *         }
 *         catch(Exception e) {
 *             results.add(e.toString());
 *         }  }</pre>
 *       Thus, an expected answer of say: "r2.compareTo(g1)=3" would be compared to the exception string which
 *       of course would be incorrect and having the added bonus of showing that the student code generated
 *       an exception and its type.
 * </ol>
 *  @author David R. Gibson, November 19, 2017
 */
public class TestSuite {

	public static ArrayList<String> test01() {
		ArrayList<String> results = new ArrayList<>();
		/*
		 * Test description. Must be present.
		 */
		results.add("Account constructor");
		/*
		 * Total points for this test. Must be present.
		 */
		results.add("20.0");
		/*
		 * This test has two parts, thus each part is worth 10 points and each part is
		 * surrounded by try/catch.
		 */
		try{
			Account acnt = new Account("Walter", 1000.0);
			results.add("acnt.getNumWithdrawals()=" + acnt.getNumWithdrawals());
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		try{
			Account acnt = new Account("Walter", 1000.0);
			String bal = String.format("$%,.2f", acnt.getBalance());
			results.add("%d " + acnt.getBalance() + " %tp 1.0 acnt.getBalanace()=" + bal);
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}

		return results;
	}

	public static ArrayList<String> test02() {
		ArrayList<String> results = new ArrayList<>();
		results.add("Account constructor detailed");
		results.add("10.0");  // Problem worth (points)
		try{
			/*
			 * This test has 4 parts (thus each part is worth 2.5 points) as indicated by
			 * the 4 strings that are added to results; however, only one try/catch block
			 * is used. This is risky in the sense that if one of the first parts generates
			 * an exception there will not be 4 strings in the actual (student) results which
			 * will cause the TestEngine to abort when grading. Obviously this is simpler
			 * than having 4 try/catch blocks. It is up to the test writer to weigh this risk,
			 * even eliminating try/catch if desired.
			 */
			Account acnt = new Account("Walter", 1000.0);
			results.add("acnt.getName()=" + acnt.getName());
			results.add("acnt.getNumWithdrawals()=" + acnt.getNumWithdrawals());
			results.add("acnt.isOverdrawn()= " + acnt.isOverdrawn());
			String bal = String.format("$%,.2f", acnt.getBalance());
			results.add("%d " + acnt.getBalance() + " %tp 1.0 acnt.getBalanace()=" + bal);
		}
		catch(RuntimeException e) {
			results.add(e.toString());
		}
		return results;
	}

	public static ArrayList<String> test03() {
		ArrayList<String> results = new ArrayList<>();
		results.add("Account aggregate test");
		results.add("10.0");  // Problem worth (points)
		try{
			/*
			 * This test has 1 part. Notice that there are 3 "answers" in the one string that
			 * is added to results. Thus, any one of these 3 being incorrect will cause the
			 * the entire test to be incorrect. This is shown for illustrative purposes. This
			 * may be what the test writer wants, or if not, written as test02 above.
			 */
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

}
