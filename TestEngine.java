import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;
/**
 * Used to generated expected results by running against the solution and also used to grade student
 * assignments once expected results have been generated.
 *
 * How to use to generate expected results:
 * <ol>
 * <li><p>Copy TestEngine and TestSuite to package where solution resides. Add package statement to each
 *    as necessary.
 * <li><p>Write test methods in TestSuite.
 * <li><p>Set path variable so that expectedResults.txt will be located in the package. If using
 *    Eclipse, path="src\\soln_package\\" is probably what you want.
 * <li><p>Set shouldGenerateExpectedResults=true
 * <li><p>Run. This generates expectedResults.txt and also displays it to the console.
 * <li><p>Set shouldGenerateExpectedResults=false
 * <li><p>Run. You are grading the solution, thus, examine the console which shows the studentReport
 *      and make sure everything looks correct. If not, repeat these steps.
 * </ol>
 * How to use to grade a student assignment:
 * <ol>
 * <li><p>Copy TestEngine, TestSuite, and expectedResults.txt to package where student solution resides.
 *     Add package statement to each as necessary.
 * <li><p>Set path variable so that expectedResults.txt will be located in the package. If using
 *    Eclipse, path="src\\student_package\\" is probably what you want.
 * <li><p>Set shouldGenerateExpectedResults=false
 * <li><p>Run. This generates studentReport.txt and also displays it to the console
 * </ol>
 * @author David R. Gibson, November 19, 2017
 *
 */
public class TestEngine {
	GradeReport gradeReport;

	/**
	 * Set to <code>true</code> when generating expected results by running against the solution.
	 * Set to <code>false</code> when running against a student solution. Must have expected results file.
	 */
	boolean shouldGenerateExpectedResults = true;

	/**
	 * Set to <code>true</code> to save assessment summary from running against student solution. This is
	 * optional because it always displays to console.
	 */
	boolean shouldSaveStudentReport = true;
	/**
	 * Path to location where student assessment summary should be saved if <code>shouldSaveStudentReport=true</code>.
	 * Can ignore if set to <code>false</code>
	 */
	String path = "src\\";
	// File containing all test results and assessment for student.
	File studentReportFile = new File(path + "studentReport.txt");

	// File containing expected results.
	File expectedResultsFile = new File(path + "expectedResults.txt");

	public TestEngine() {}

	/**
	 * Builds and saves the expected results by running program against the solution.
	 */
	private void generateExpectedResults() {
		ArrayList<ArrayList<String>> testResults = generateTestResults();
		gradeReport = buildExpectedGradeReport(testResults);
		String expectedOutput = gradeReport.expectedOutput();
		saveResults(expectedOutput, expectedResultsFile);
		System.out.println(expectedOutput);
	}

	/**
	 * Runs against student solution, assesses against expected results and displays a summary.
	 */
	private void generateActualResults() {

		buildExpectedGradeReport(readExpectedResults(expectedResultsFile));
		ArrayList<ArrayList<String>> actualTestResults = generateTestResults();
		addActualResults(actualTestResults);
		gradeReport.assessTests();
		System.out.println(gradeReport);
		if(shouldSaveStudentReport) {
			saveResults(gradeReport.toString(), studentReportFile);
		}
	}

	/**
	 * Gets the names of the test case methods in <code>TestSuite</code>
	 * @return the sorted list of method names.
	 */
	private ArrayList<String> testMethodNames() {
		ArrayList<String> testMethodNames = new ArrayList<>();
		Class<TestSuite> c = TestSuite.class;
		/*
		 * According to the documentation, "The elements in the returned array are
		 * not sorted and are not in any particular order." So, we've got to fix that
		 * as the order needs to be the same as the expected results.
		 */
        Method[] methods = c.getDeclaredMethods();
        for(int i=0; i<methods.length; i++) {
        	if(!methods[i].getName().contains("test")) {
        		continue;
        	}
        	testMethodNames.add(methods[i].getName());
        	Collections.sort(testMethodNames);
        }
		return testMethodNames;
	}

	/**
	 * Run the test case methods.
	 * @return a list of results from teach test case method. Each method produces an {@code ArrayList<String>}
	 * of results. This return is the collection of all these lists.
	 */
	private ArrayList<ArrayList<String>> generateTestResults() {
		ArrayList<ArrayList<String>> resultsLists = new ArrayList<>();
        ArrayList<String> results;
		ArrayList<String> testMethodNames = testMethodNames();
		//System.out.println(testMethodNames);
		try {
            for(String testMethodName : testMethodNames) {
        		Method method = TestSuite.class.getDeclaredMethod(testMethodName);
        		results = (ArrayList<String>)method.invoke(null);
            	resultsLists.add(results);
            }
		}
		catch (Throwable e) {
            System.out.println(e);
		}
		return resultsLists;

		// Example: has parameter, no return
//      Class[] param = new Class[1];
//		param[0] = ArrayList.class;
//		Method method = TestSuite.class.getDeclaredMethod(m[0].getName(), param);
//		method.invoke(null, results);
	}

	/**
	 * Builds the <code>GradeReport</code> holding the expected results when running against the solution.
	 * @param resultsLists the list of expected results from each test case method when running
	 * against the solution.
	 * @return
	 */
	private GradeReport buildExpectedGradeReport(ArrayList<ArrayList<String>> resultsLists) {
		gradeReport = new GradeReport();
		for(ArrayList<String> results : resultsLists) {
			Test test = new Test();
			test.description = results.remove(0);
			test.pointsMax = Double.parseDouble(results.remove(0));
			test.expectedOutput.addAll(results);
			gradeReport.tests.add(test);
		}
		return gradeReport;
	}

	/**
	 * Adds the list of results from running against the student solution (actual results) to
	 * the <code>GradeReport</code>
	 * @param resultsLists the list of actual results from each test case method when running
	 * against the student solution.
	 */
	private void addActualResults(ArrayList<ArrayList<String>> resultsLists) {
		int i=0;
		for(ArrayList<String> results : resultsLists) {
			Test test = gradeReport.tests.get(i++);
			// Remove description and max points, already initialized with expected.
			results.remove(0);
			results.remove(0);
			test.actualOutput.addAll(results);
		}
	}

	/**
	 * Read the expected results that are saved in <code>file</code>
	 * @param file contains the expected results
	 * @return a list of results containing the expected results.
	 */
	private ArrayList<ArrayList<String>> readExpectedResults(File file) {
		ArrayList<ArrayList<String>> resultsLists = new ArrayList<>();

		try {
			Scanner expScan = new Scanner(file);

			while(expScan.hasNextLine()){
				ArrayList<String> results = new ArrayList<String>();
				// Ignore "#" delimeter. Not actually used!
				expScan.nextLine();
				results.add(expScan.nextLine());
				results.add(expScan.nextLine());
				int numAnswers = Integer.parseInt(expScan.nextLine());
				for(int i=0; i<numAnswers; i++) {
					results.add(expScan.nextLine());
				}
				resultsLists.add(results);
			}
			expScan.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return resultsLists;
	}

	/**
	 * Saves a text file.
	 * @param results either the expected results or the <code>GradeReport</code> summary from
	 * running against the student solution.
	 * @param file
	 */
	private void saveResults(String results, File file) {
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print(results);
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Program either (a) generates and saves the expected results or (b) generates, assesses and saves
	 * the actual results from running against the student solution.
	 * @param args
	 */
	public static void main(String[] args) {
		TestEngine testEngine = new TestEngine();

		if(testEngine.shouldGenerateExpectedResults) {
			testEngine.generateExpectedResults();
		}
		else {
			testEngine.generateActualResults();
		}
	}
}

/**
 * This class holds the collection of test cases, <code>tests</code> and a summary of the assessment
 * results.
 * @author dgibson
 *
 */
class GradeReport {

	double pointsMax=0.0;
	double pointsEarned=0.0;
	double percentCorrect=0.0;
	int numCorrect=0;
	int numTotal=0;
	List<Test> tests = new ArrayList<Test>();

	public GradeReport() {}

	/**
	 * Assesses all the test cases, tallying the points earned.
	 */
	public void assessTests() {
		for(Test test : tests) {
			test.assess();
			this.pointsMax += test.pointsMax;
			this.pointsEarned += test.pointsEarned;
		}
		this.percentCorrect = pointsEarned/pointsMax * 100.0;
	}

	/**
	 * Builds and returns a string representing to be used by clients to store the expected
	 * results text file. The format is:
	 *     # Test test_num Expected Results
	 *     test_description
	 *     max_points
	 *     results_to_be_assessed
	 *     results_to_be_assessed
	 *     ...
	 *
	 * The first line is simply used as a delimiter to make the expected results file slightly
	 * more readable.
	 * @return
	 */
	public String expectedOutput() {
		StringBuilder res = new StringBuilder();
		int i=1;
		for(Test test : tests) {
			res.append("# Test " + (i++) + " Expected Results\n");
			res.append(test.description + "\n");
			res.append(test.pointsMax + "\n");
			res.append(test.expectedOutput.size() + "\n");
			for(String s : test.expectedOutput) {
				res.append(s + "\n");
			}
		}
		return res.toString();
	}

	/**
	 * Returns a summary of each test case and an overall summary.
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		int i=1;
		for(Test test : tests) {
			res.append("Test: " + (i++) + "-" + test + "\n");
		}

		res.append(String.format("Overall Summary: %.1f points out of %.1f (%.1f%%)\n",
				pointsEarned, pointsMax, percentCorrect));

		return res.toString();
	}
}

/**
 * This <code>Test</code> class represents the results of a single test case. A test case can
 * have multiple parts (answers) to be evaluated where each part is stored as an entry in
 * <code>actualOutput</code>. There is an <code>assess</code> method that compares the actual
 * and expected output. A test case has a maximum number of points, <code>pointsMax</code>. The
 * <code>assess</code> method determines the <code>numCorrect</code> parts which is used to
 * compute the <code>pointsEarned</code>. The class also keeps track of which parts are correct
 * with <code>isCorrect</code>. <code>toString</code> returns a report of this test case showing
 * expeced and actual output, wether correct, points earned, etc.
 * <code>expectedOutput</code> and <code>actualOutput</code> and can have multiple parts to be
 *
 * @author dgibson
 *
 */
class Test {
	String description;
	double pointsMax;
	double pointsEarned;
	int numCorrect;
	int numTotal;
	List<String> expectedOutput = new ArrayList<>();
	List<String> actualOutput = new ArrayList<>();
	List<Boolean> isCorrect = new ArrayList<>();
	boolean hasRunTimeError = false;

	public Test() {}

	/**
	 * Adds the actual output.
	 * @param results The results of an actual test case. Includes the description and maximum
	 * points.
	 */
	public void addActualOutput(ArrayList<String> results) {
		this.description = results.remove(0);
		this.pointsMax = Double.parseDouble(results.remove(0));
		this.actualOutput.addAll(results);
	}
	/**
	 * Grades this test case. <code>expectedOutput</code> and <code>actualOutput</code> must be
	 * the same size, if not a runtime exception is thrown. Each entry in <code>expectedOutput</code>
	 * and <code>actualOutput</code> are compared on a one-to-one basis to determine wether the
	 * actualOutput is correct. There are two types of output: (1) string output that can contain
	 * characters, integers, and booleans (2) string output that also contains a double. These two
	 * types are handled differently. Strings are assessed with string equality where doubles are
	 * assessed using a tolerance to allow for slightly different results.
	 */
	public void assess() {
		compareExpAndActOutputSizes();
		initializeInstanceVars();
		for(int i=0; i<expectedOutput.size(); i++) {
			String exp = expectedOutput.get(i);
			String act = actualOutput.get(i);
			if(hasDoubleResult(exp)) {
				assessDouble(exp,act);
			}
			else {
				assessString(exp,act);
			}
			numTotal++;
		}
		pointsEarned = (double)numCorrect/numTotal * pointsMax;
	}

	private void compareExpAndActOutputSizes() {
		if(expectedOutput.size()!=actualOutput.size()) {
			System.out.println(expectedOutput);
			System.out.println("expectedOutput.size()=" + expectedOutput.size() +
					", actualOutput.size()" + actualOutput.size());
			throw new RuntimeException("expectedOutput and actualOutput not the same size");
		}
	}

	private void initializeInstanceVars() {
		isCorrect = new ArrayList<>();
		numCorrect = 0;
		numTotal = 0;
	}

	private boolean hasDoubleResult(String exp) {
		if(exp.substring(0, 2).equals("%d"))
			return true;
		return false;
	}

	/**
	 * Compares the expected and actual output of a part of a test case using string equality.
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
	private void assessString(String exp, String act) {
		if(exp.equals(act)) {
			numCorrect++;
			isCorrect.add(true);
		}
		else {
			isCorrect.add(false);
		}
	}

	/**
	 * Compares an expected double result to an actual double result using a tolerance
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
	private void assessDouble(String exp, String act) {
		double errorAct = getActError(exp, act);
		double errorMax = getMaxError(exp);
		if(errorAct<=errorMax) {
			numCorrect++;
			isCorrect.add(true);
		}
		else {
			isCorrect.add(false);
		}
	}

	/**
	 * Computes the actual error between the expected double and the actual double.
	 * @param exp
	 * @param act
	 * @return
	 */
	private double getActError(String exp, String act) {
		double valueExp = getDouble(exp);
		double valueAct = getDouble(act);
		double errorAct = Math.abs(valueExp-valueAct);
		return errorAct;
	}

	/**
	 * Extracts the double from the <code>output</code> string. The format of <code>output</code>
	 * is: %d double_value ...". Thus, <code>output</code> is split on " " and the second element
	 * is the double.
	 * @param output
	 * @return
	 */
	private double getDouble(String output) {
		String[] tokensExp = output.split(" ");
		double value;
		try {
			value = Double.parseDouble(tokensExp[1]);
		}
		catch(Exception e) {
			// Not the best choice. Could fail in the (remote) case where the
			// expected value is within the max error of Double.MAX_VALUE.
			// Not sure how to fix right now.
			value = Double.MAX_VALUE;
		}
		return value;
	}

	/**
	 * Computes the maximum error that is allowed. Maximum error is computed as a function of
	 * either the allowable tolerance specfied as either a percent of the expected double or as
	 * an absolute tolerance. "%tp tol_percent" or "%ta tol_absolute" is used. The format
	 * of <code>output</code> is: "%d double_value %t[p|a] tol ...". Thus, split on " " and the
	 * second element is the double, the third is the type of tolerance, and the fourth is the
	 * tolerance value.
	 * @param output
	 * @return
	 */
	private double getMaxError(String output) {
		String[] tokensExp = output.split(" ");
		double value = Double.parseDouble(tokensExp[1]);
		String tolType = tokensExp[2];
		double tol = Double.parseDouble(tokensExp[3]);
		double errorMax=0.0;
		if(tolType.equals("%tp")) {
			errorMax = value * tol/100.0;
		}
		else if(tolType.equals("%ta")) {
			errorMax = tol;
		}
		return errorMax;
	}

	/**
	 * Returns a summary of this test case showing expected and actual results for each part as well
	 * as correctness, number correct, and points earned.
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(String.format("%s\n", this.description));

		for(int i=0; i<expectedOutput.size(); i++) {
			String correct = isCorrect.get(i) ? "Correct" : "Incorrect";
			String exp = expectedOutput.get(i);
			String act = actualOutput.get(i);
			if(hasDoubleResult(exp)) {
				// Find 4th space
				double errorAct = getActError(exp,act);
				double errorMax = getMaxError(exp);
				exp = stripDouble(exp);
				act = stripDouble(act);
				res.append(String.format("%s - Expected: %s | Actual: %s\n", correct, exp, act));
				String strAct = "Actual Error=" + errorAct;
				String strMax = "" + errorMax + "=Max Error";
				String strError = isCorrect.get(i) ? strAct + "<" + strMax : strAct + ">" + strMax;
				res.append(strError + "\n");
			}
			else {
				res.append(String.format("%s - Expected: %s | Actual: %s\n", correct, exp, act));
			}
		}

		res.append(String.format("Summary: %d out of %d answers correct : %.1f points out of %.1f\n",
				numCorrect, numTotal, pointsEarned, pointsMax));

		return res.toString();
	}

	/**
	 * Removes the double and tolerance from <code>output</code> and returns just the output string
	 * containing the double. The full format of <code>output</code> is:
	 *   %d double_value %t[p|a] tol output_that_contains_chars_etc_with_double_embedded
	 * @param output
	 * @return
	 */
	private String stripDouble(String output) {
		// Get location of spaces surrounding double.
		int pos1 = output.indexOf(" ");
		int pos2 = output.indexOf(" ",pos1+1);
		// Get location of space after %t markup
		int pos3 = output.indexOf(" ", pos2+1);
		// Get location of space after tolerance
		int pos4 = output.indexOf(" ", pos3+1);
		// Return remainder of string
		return output.substring(pos4+1);
	}
}

