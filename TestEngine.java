package ver11_dev;

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
 * @author David R. Gibson, January 25, 2018
 *
 */
public class TestEngine {
	/**
	 * Program either (a) generates and saves the expected results or (b) generates, assesses and saves
	 * the actual results from running against the student solution.
	 * @param args
	 */
	public static void main(String[] args) {
		TestEngine testEngine = new TestEngine();
		testEngine.run();
	}
	/**
	 * Set to <code>true</code> when generating expected results by running against the solution.
	 * Set to <code>false</code> when running against a student solution. Must have expected results file.
	 */
	boolean shouldGenerateExpectedResults = false;
	/**
	 * Set to <code>true</code> to save Grade Report generated from running against student solution. This is
	 * optional because it always displays to console.
	 */
	boolean shouldSaveStudentReport = false;
	/**
	 * Name of the package where expected results file is found, and where Grade Report will be saved.
	 */
    String packageName = this.getClass().getPackage().getName();
	/**
	 * Path to location of the expected results file and where the Grade Report will be saved.
	 */
	String path = "src\\" + packageName + "\\";

	/**
	 * Grade Report file, contains detailed report from grading including expected and actual results
	 * for each test case, for each test; correct vs. incorrect, points awarded, %'s, etc.
	 */
	File gradeReportFile = new File(path + "gradeReport.txt");

	/**
	 * File where expected results are stored. This file is written to when <code>shouldGenerateExpectedResults</code>
	 * is <code>true</code> and read from when <code>false</code>.
	 */
	File expectedResultsFile = new File(path + "expectedResults.txt");

	public TestEngine() {}
	
	/**
	 * Discovers and then runs the test methods after which the results are either saved as the 
	 * expected results, or, the results are the student's results which are graded (i.e. compared to
	 * the expected results) when <code>shouldGenerateExpectedResults</code> is <code>true</code> or 
	 * <code>true</code>, respectively.
	 */
	private void run() {
		TestMethods testMethods = discoverTestMethods();
		// This is either the expected results (instructor's answers) or the actual results
		// (student answers), depending on whether shouldGenerateExpectedResults is true of false, respectively.
		RawTestResults rawResults = runTests(testMethods);
		
		// Value is true when running against instructor solution in order to generate expected results
		if(shouldGenerateExpectedResults) {
			saveExpectedResults(expectedResultsFile, rawResults);
			displayExpectedResults(expectedResultsFile);  // this is more of a debugging artifact
		}
		// Value is false when running against student solution in order to generate student grade report.
		else {
			TestResultsSuite testSuite = buildTestResultsSuite(expectedResultsFile, rawResults);
			// gradeTestSuite should probably be static
			Grader grader = new Grader();
			GradeReport gradeReport = grader.gradeTestSuite(getStudentName(), testSuite);
			if(shouldSaveStudentReport) {
				saveGradeReport(gradeReportFile, gradeReport.toString());
			}
			System.out.println(gradeReport);
		}

	}
	
	/**
	 * 
	 * @param actResult
	 * @param expResult
	 * @return
	 */
	private AutomatedTest buildAutomatedTest(ArrayList<String> actResult, ArrayList<String> expResult) {
		String description = actResult.remove(0);
		double pointsMax = Double.parseDouble(actResult.remove(0));

		// Get rid of desc & max points from expected results. Leaves just the expected result answer.
		expResult.remove(0); expResult.remove(0);

		AutomatedTest test = new AutomatedTest(description, pointsMax, actResult, expResult);
		return test;
	}
	
	private ManualTest buildManualTest(ArrayList<String> actResult, ArrayList<String> expResult) {
		String description = actResult.get(0);
		String actualOutput = actResult.get(1);
		String expectedOutput = expResult.get(1);
		ManualTest test = new ManualTest(description, actualOutput, expectedOutput);
		return test;
	}

	/**
	 * Builds the {@link TestResultsSuite} from the {@link RawTestResults}. The TestResultsSuite object
	 * simply holds the actual and expected output in a way that is convenient for assessing the test later on.
	 * 
	 * @param file File where the expected results are found
	 * @param actResults The actual results
	 * @return
	 */
	private TestResultsSuite buildTestResultsSuite(File file, RawTestResults actResults) {
		RawTestResults expResults = readExpectedResults(file);
		TestResultsSuite testResultsSuite = new TestResultsSuite(); 
		
		for(int i=0; i<actResults.getNumAutomatedTests(); i++) {
			ArrayList<String> actResult = actResults.getAutomatedResults(i);
			ArrayList<String> expResult = expResults.getAutomatedResults(i);
			AutomatedTest test = buildAutomatedTest(actResult, expResult);
			testResultsSuite.addAutomatedTest(test);
		}
		for(int i=0; i<actResults.getNumManualTests(); i++) {
			ArrayList<String> actResult = actResults.getManualResults(i);
			ArrayList<String> expResult = expResults.getManualResults(i);			
			ManualTest test = buildManualTest(actResult, expResult);
			testResultsSuite.addManualTest(test);
		}

		return testResultsSuite;
	}
	
	/**
	 * Gets the names of the test methods in <code>TestSuite</code>
	 * According to the documentation for getDeclaredMethods, "The elements in the 
	 * returned array are not sorted and are not in any particular order." Thus, to 
	 * maintain order when comparing expected results to actual results, we sort
	 * the lists of method names so that they are called consistently..
	 * @return the sorted list of method names.
	 */
	private TestMethods discoverTestMethods() {
		ArrayList<String> testMethodNames = new ArrayList<>();
		ArrayList<String> testManuallyMethodNames = new ArrayList<>();
		Class<TestSuite> c = TestSuite.class;
		/*
		 * According to the documentation, "The elements in the returned array are
		 * not sorted and are not in any particular order." So, we've got to fix that
		 * as the order needs to be the same as the expected results.
		 */
        Method[] methods = c.getDeclaredMethods();
        for(int i=0; i<methods.length; i++) {
        	if(methods[i].getName().startsWith("testManually")) {
        		testManuallyMethodNames.add(methods[i].getName());
        	}
        	else if(methods[i].getName().startsWith("test")) {
            	testMethodNames.add(methods[i].getName());
        	}
        	else {
        		continue;
        	}
        }
    	Collections.sort(testMethodNames);
    	Collections.sort(testManuallyMethodNames);
    	TestMethods testMethods = new TestMethods(testMethodNames, testManuallyMethodNames);
		return testMethods;
	}

	/**
	 * Display the expected results. Used at conclusion of run to create expected results.
	 * @param file contains the expected results
	 */
	private void displayExpectedResults(File file) {
		String text = "Expected results read from file\n";
		String dashes = "-------------------------------\n";
		String header = text + dashes;
		String footer = "\n" + dashes + text;
		
		try {
			Scanner scan = new Scanner(file);
			scan.useDelimiter("\\Z");  // The end of the input for final terminator
			String content = scan.next();
			System.out.println(header + content + footer);
			scan.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getStudentName() {
		// This is the Eclipse project name, i.e. the root folder for the project. By conventions, this
		// is the student's last name.
        String dir = System.getProperty("user.dir");
        int pos = dir.lastIndexOf("\\");
        String name = dir.substring(pos+1);
//        System.out.println("current dir = " + dir);
//        System.out.println("pos = " + pos);
//        System.out.println("name = " + name);
        return name;
	}
	
	/**
	 * Read the expected results that are saved in <code>file</code>
	 * @param file contains the expected results
	 * @return a list of results containing the expected results.
	 */
	private RawTestResults readExpectedResults(File file) {
		ArrayList<ArrayList<String>> automatedResultsLists = new ArrayList<>();
		ArrayList<ArrayList<String>> manualResultsLists = new ArrayList<>();
	
		ArrayList<String> rawResults = new ArrayList<>();
		try {
			Scanner expScan = new Scanner(file);
			while(expScan.hasNextLine()) {
				rawResults.add(expScan.nextLine());
			}
			expScan.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		boolean isDone = false;
		int curLineNum = 0;
		while(!isDone){
			ArrayList<String> results = new ArrayList<String>();
			// Ignore "#" delimiter. Not actually used!
			String line = rawResults.get(curLineNum++);
			if(line.contains("Manual")) {
				results.add(rawResults.get(curLineNum++)); // Description
				boolean isManualOutputFinished = false;
				String output = "";
				while(!isManualOutputFinished) {  // Output
					String curLine = rawResults.get(curLineNum++);
					if(curLine.contains("Manual")) {
						isManualOutputFinished = true;
						curLineNum--;
					}
					else {
						output += curLine + "\n";	
						if(curLineNum==rawResults.size()) {
							isManualOutputFinished = true;
							isDone = true;
						}
					}
				}
				output = output.substring(0,output.length()-1);
				results.add(output);
				manualResultsLists.add(results);
			}
			else if (line.contains("Test")) {
				results.add(rawResults.get(curLineNum++)); // description
				results.add(rawResults.get(curLineNum++)); // points
				int numAnswers = Integer.parseInt(rawResults.get(curLineNum++)); // num parts
				for(int i=0; i<numAnswers; i++) {
					results.add(rawResults.get(curLineNum++));
				}
				automatedResultsLists.add(results);
				if(curLineNum==rawResults.size()) {
					isDone = true;
				}
			}
		}
		RawTestResults rawTestResults = new RawTestResults(automatedResultsLists,manualResultsLists);
		return rawTestResults;
	}

	private ArrayList<ArrayList<String>> runMethods(ArrayList<String> methodNames) {
        ArrayList<String> results;
		ArrayList<ArrayList<String>> resultsLists = new ArrayList<>();
		
		try {
            for(String methodName : methodNames) {
        		Method method = TestSuite.class.getDeclaredMethod(methodName);
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
	 * Run the test case methods.
	 * @return a list of results from each test case method. Each method produces an <code>ArrayList<String></code>
	 * of results. This return is the collection of all these lists.
	 */
	private RawTestResults runTests(TestMethods testMethods) {
		ArrayList<ArrayList<String>> automatedResultsLists = runMethods(testMethods.getAutomatedMethodNames());
		ArrayList<ArrayList<String>> manualResultsLists = runMethods(testMethods.getManualMethodNames());
		RawTestResults rawResults = new RawTestResults(automatedResultsLists,manualResultsLists);
		return rawResults;
	}

	/**
	 * Takes the rawResults from running the test against the solution and writes them to 
	 * file using a prescribed format, for ease of reading back in (and debugging) when 
	 * running against student solution. The format of the file is:
	 * <pre>
	 * # Test 0 Expected Results
	 * problem description
	 * max value of problem
	 * number of parts to problem
	 * answer 1
	 * answer 2
	 * ...
	 * # Test 1 Expected Results
	 * problem description
	 * max value of problem
	 * number of parts to problem
	 * answer 1
	 * answer 2
	 * ...
	 * </pre>
	 * For example:
	 * <pre>
	 * # Test 0 Expected Results
	 * Account constructor
     * 20.0
     * 2
     * acnt.getNumWithdrawals()=0
     * %d 996.0 %tp 1.0 acnt.getBalanace()=$996.00
     * # Test 1 Expected Results
     * Account constructor detailed
     * 10.0
     * 4
     * acnt.getName()=Walter
     * acnt.getNumWithdrawals()=0
     * acnt.isOverdrawn()= false
     * %d 1008.0 %ta 5.0 acnt.getBalanace()=$1,008.00
	 * </pre>
	 * @param file Location to write expected results.
	 * @param rawResults The raw results from running against the solution.
	 */
	private void saveExpectedResults(File file, RawTestResults rawResults) {
		try {
			PrintWriter writer = new PrintWriter(file);
			// Save automated tests
			for(int i=0; i<rawResults.getNumAutomatedTests(); i++) {
				ArrayList<String> results = rawResults.getAutomatedResults(i);
				writer.println(String.format("# Test %d Expected Results", (i+1)));
				// First two entries are description and max points, remove each after saving...
				writer.println(results.remove(0)); // description
				writer.println(Double.parseDouble(results.remove(0))); // points max
				// ...then what is left is the actual test results, a list where each line is the
				// result of one sub-test. Save number of sub-tests (for ease of reading back in later)
				writer.println(results.size()); // num sub-tests (parts)
				for(String result : results) {
					writer.println(result); // sub-test output
				}
			}
			
			// Save manual tests
			for(int i=0; i<rawResults.getNumManualTests(); i++) {
				ArrayList<String> results = rawResults.getManualResults(i);
				writer.println(String.format("# Test %d (Manual) Expected Results", (i+1)));
				writer.println(results.get(0)); // description
				writer.println(results.get(1)); // manual test output
			}
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves a Grade Report.
	 * @param file Location to save Grade Report
	 * @param report The Grade Report.
	 */
	private void saveGradeReport(File file, String report) {
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print(report);
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

/**
 * This <code>Test</code> class represents the results of a single test case (test method). 
 * However, a test case can have multiple parts (answers). For example on a written test, this
 * might represent: Problem 3, parts a,b,c,d. The result of each "part" is stored as a string
 * and the list of strings is stored in <code>actualOutput</code>. 
 * There is an <code>assess</code> method that compares the actual
 * and expected output. A test case has a maximum number of points, <code>pointsMax</code>. The
 * <code>assess</code> method determines the <code>numCorrect</code> parts which is used to
 * compute the <code>pointsEarned</code>. For example, if the test case is worth 10 points and 
 * has 4 parts, each part is worth 2.5 points. So, if a student gets 3 of 4 parts correct, they
 * score 7.5 points for this test case. The class also remembers which parts are correct
 * with the <code>isCorrect</code> list, which is used when generating the report for this test case.
 * <code>toString</code> returns a report of this test case showing
 * expeced and actual output, whether correct, points earned, etc.
 * 
 * @author dgibson
 *
 */
class AutomatedTest {
	private String description="";
	private double pointsMax=0;
	private double pointsEarned=0;
	private int numAnswersCorrect=0;
	private int numAnswers=0;
	private List<String> expectedOutput = new ArrayList<>();
	private List<String> actualOutput = new ArrayList<>();
	private List<Boolean> isCorrect = new ArrayList<>();
	private List<Boolean> hasDouble;
	private List<Double> actErrors;
	private List<Double> maxErrors;
	private boolean hasRunTimeError = false;

	public AutomatedTest(String description, double pointsMax, List<String> actualOutput, List<String> expectedOutput) {
		this.description = description;
		this.pointsMax = pointsMax;
		this.expectedOutput = expectedOutput;
		this.actualOutput = actualOutput;
		int size = expectedOutput.size();
		actErrors = new ArrayList<>(Collections.nCopies(size, 0.0));
		maxErrors = new ArrayList<>(Collections.nCopies(size, 0.0));
		hasDouble = new ArrayList<>(Collections.nCopies(size, false));
	}

	public AutomatedTest() {
	}


	public void setIsCorrect(boolean val) {
		isCorrect.add(val);
	}
	public void incrementNumAnswersCorrect() {
		numAnswersCorrect++;
	}
	public void incrementNumAnswers() {
		numAnswers++;
	}
	public void setActualError(int i, double val) {
		actErrors.set(i, val);
	}

	public void setPointsEarned(double val) {
		pointsEarned = val;
	}
	
	public void setMaxError(int i, double val) {
		maxErrors.set(i, val);
	}

	public void setHasDouble(int i, boolean val) {
		hasDouble.set(i, val);
	}
	public String getExpectedAnswer(int i) {
		return expectedOutput.get(i);
	}

	public String getActualAnswer(int i) {
		return actualOutput.get(i);
	}

	public int getNumAnswersCorrect() {
		return numAnswersCorrect;
	}
	
	public String getDescription() {
		return description;
	}

	public double getPointsMax() {
		return pointsMax;
	}

	public double getPointsEarned() {
		return pointsEarned;
	}

	public int getNumAnswers() {
		return numAnswers;
	}

	public List<String> getExpectedOutput() {
		return expectedOutput;
	}

	public int getExpectedNumAnswers() {
		return expectedOutput.size();
	}

	public List<String> getActualOutput() {
		return actualOutput;
	}

	public int getActualNumAnswers() {
		return actualOutput.size();
	}

	public List<Double> getActErrors() {
		return actErrors;
	}

	/**
	 * Adds the actual output.
	 * @param results The results of an actual test case. Includes the description and maximum
	 * points.
	 */
//	public void addActualOutput(ArrayList<String> results) {
//		this.description = results.remove(0);
//		this.pointsMax = Double.parseDouble(results.remove(0));
//		this.actualOutput.addAll(results);
//	}
	/**
	 * Grades this test case. <code>expectedOutput</code> and <code>actualOutput</code> must be
	 * the same size, if not a runtime exception is thrown. Each entry in <code>expectedOutput</code>
	 * and <code>actualOutput</code> are compared on a one-to-one basis to determine wether the
	 * actualOutput is correct. There are two types of output: (1) string output that can contain
	 * characters, integers, and booleans (2) string output that also contains a double. These two
	 * types are handled differently. Strings are assessed with string equality where doubles are
	 * assessed using a tolerance to allow for slightly different results.
	 */
//	public void assess() {
//		compareExpAndActOutputSizes();
//		initializeInstanceVars();
//		for(int i=0; i<expectedOutput.size(); i++) {
//			String exp = expectedOutput.get(i);
//			String act = actualOutput.get(i);
//			if(hasDoubleResult(exp)) {
//				assessDouble(exp,act);
//			}
//			else {
//				assessString(exp,act);
//			}
//			numTotal++;
//		}
//		pointsEarned = (double)numCorrect/numTotal * pointsMax;
//	}

	/**
	 * Compares an expected double result to an actual double result using a tolerance
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
//	private void assessDouble(String exp, String act) {
//		double errorAct = getActError(exp, act);
//		double errorMax = getMaxError(exp);
//		if(errorAct<=errorMax) {
//			numCorrect++;
//			isCorrect.add(true);
//		}
//		else {
//			isCorrect.add(false);
//		}
//	}

	/**
	 * Compares the expected and actual output of a part of a test case using string equality.
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
//	private void assessString(String exp, String act) {
//		if(exp.equals(act)) {
//			numCorrect++;
//			isCorrect.add(true);
//		}
//		else {
//			isCorrect.add(false);
//		}
//	}

//	private void compareExpAndActOutputSizes() {
//		if(expectedOutput.size()!=actualOutput.size()) {
//			System.out.println(expectedOutput);
//			System.out.println("expectedOutput.size()=" + expectedOutput.size() +
//					", actualOutput.size()" + actualOutput.size());
//			throw new RuntimeException("expectedOutput and actualOutput not the same size");
//		}
//	}

	/**
	 * Computes the actual error between the expected double and the actual double.
	 * @param exp
	 * @param act
	 * @return
	 */
//	private double getActError(String exp, String act) {
//		double valueExp = getDouble(exp);
//		double valueAct = getDouble(act);
//		double errorAct = Math.abs(valueExp-valueAct);
//		return errorAct;
//	}

	/**
	 * Extracts the double from the <code>output</code> string. The format of <code>output</code>
	 * is: %d double_value ...". Thus, <code>output</code> is split on " " and the second element
	 * is the double.
	 * @param output
	 * @return
	 */
//	private double getDouble(String output) {
//		String[] tokensExp = output.split(" ");
//		double value;
//		try {
//			value = Double.parseDouble(tokensExp[1]);
//		}
//		catch(Exception e) {
//			// Not the best choice. Could fail in the (remote) case where the
//			// expected value is within the max error of Double.MAX_VALUE.
//			// Not sure how to fix right now.
//			value = Double.MAX_VALUE;
//		}
//		return value;
//	}

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
//	private double getMaxError(String output) {
//		String[] tokensExp = output.split(" ");
//		double value = Double.parseDouble(tokensExp[1]);
//		String tolType = tokensExp[2];
//		double tol = Double.parseDouble(tokensExp[3]);
//		double errorMax=0.0;
//		if(tolType.equals("%tp")) {
//			errorMax = value * tol/100.0;
//		}
//		else if(tolType.equals("%ta")) {
//			errorMax = tol;
//		}
//		return errorMax;
//	}

//	private boolean hasDoubleResult(String exp) {
//		if(exp.substring(0, 2).equals("%d"))
//			return true;
//		return false;
//	}

//	private void initializeInstanceVars() {
//		isCorrect = new ArrayList<>();
//		numCorrect = 0;
//		numTotal = 0;
//	}

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
	/**
	 * Returns a summary of this test case showing expected and actual results for each part as well
	 * as correctness, number correct, and points earned.
	 * 
	 * LOOKS LIKE HEAVY duplication of code here. Why calling hasDoubleResult, already called when assesing
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(String.format("%s\n", this.description));

		for(int i=0; i<expectedOutput.size(); i++) {
			String correct = isCorrect.get(i) ? "Correct" : "Incorrect";
			String exp = expectedOutput.get(i);
			String act = actualOutput.get(i);
			if(hasDouble.get(i)) {
				// Find 4th space
//				double errorAct = getActError(exp,act);
//				double errorMax = getMaxError(exp);
				exp = stripDouble(exp);
				act = stripDouble(act);
				res.append(String.format("%s - Expected: %s | Actual: %s\n", correct, exp, act));
				String strAct = "Actual Error=" + actErrors.get(i);
				String strMax = "" + maxErrors.get(i) + "=Max Error";
				String strError = isCorrect.get(i) ? strAct + "<" + strMax : strAct + ">" + strMax;
				res.append("  "+ strError + "\n");
			}
			else {
				res.append(String.format("%s - Expected: %s | Actual: %s\n", correct, exp, act));
			}
		}

		res.append(String.format("Summary: %d out of %d answers correct : %.1f points out of %.1f\n",
				numAnswersCorrect, numAnswers, pointsEarned, pointsMax));

		return res.toString();
	}
}

class Grader {

	public Grader() {
	}

//	private String studentName;
//	
//	public Grader(String studentName) {
//		this.studentName = studentName;
//	}

//	public GradeReport gradeTestSuite(TestResultsSuite testSuite) {
	public GradeReport gradeTestSuite(String studentName, TestResultsSuite testSuite) {
		GradeReport gradeReport = new GradeReport(studentName, testSuite.getAutomatedTests(),testSuite.getManualTests());
			
		for(int i=0; i<gradeReport.getNumAutomatedTests(); i++) {
			AutomatedTest test = gradeReport.getAutomatedTest(i);
			assess(test);
			gradeReport.incrementPointsMax(test.getPointsMax());
			gradeReport.incrementPointsEarned(test.getPointsEarned());
			gradeReport.incrementNumTestsCorrect(test.getNumAnswersCorrect());
			gradeReport.incrementNumTestsTotal(test.getNumAnswers());
		}
		
		double ppc = gradeReport.getPointsEarned()/gradeReport.getPointsMax() * 100.0;
		gradeReport.setPointsPercentCorrect(ppc);
		double ntpc = gradeReport.getNumTestsCorrect()/(gradeReport.getNumTestsTotal()*1.0)*100.0;
		gradeReport.setNumTestsPercentCorrect(ntpc);
		
		return gradeReport;
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
	private void assess(AutomatedTest test) {
		compareExpAndActOutputSizes(test);
		for(int i=0; i<test.getExpectedNumAnswers(); i++) {
			String exp = test.getExpectedAnswer(i);
			String act = test.getActualAnswer(i);
			if(hasDoubleResult(exp)) {
				test.setHasDouble(i, true);
				double[] errors = assessDouble(test,exp,act);
				test.setActualError(i, errors[0]);
				test.setMaxError(i, errors[1]);
			}
			else {
				test.setHasDouble(i, false); // unnecessary, init'd to all false
				assessString(test,exp,act);
			}
			test.incrementNumAnswers();
		}
		double pointsEarned = (double)test.getNumAnswersCorrect()/test.getNumAnswers() * test.getPointsMax();
		test.setPointsEarned(pointsEarned);
	}
	
	/**
	 * Compares an expected double result to an actual double result using a tolerance
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
	private double[] assessDouble(AutomatedTest test, String exp, String act) {
		double[] errors = new double[2];
		double errorAct = getActError(exp, act);
		double errorMax = getMaxError(exp);
		errors[0] = errorAct;
		errors[1] = errorMax;
		if(errorAct<=errorMax) {
			test.incrementNumAnswersCorrect();
			test.setIsCorrect(true);
		}
		else {
			test.setIsCorrect(false);
		}
		return errors;
	}

	/**
	 * Compares the expected and actual output of a part of a test case using string equality.
	 * @param exp The expected output for a part of a test case.
	 * @param act The actual output for a part of a test case.
	 */
	private void assessString(AutomatedTest test, String exp, String act) {
		if(exp.equals(act)) {
			test.incrementNumAnswersCorrect();
			test.setIsCorrect(true);
		}
		else {
			test.setIsCorrect(false);
		}
	}

	private void compareExpAndActOutputSizes(AutomatedTest test) {
//		if(test.expectedOutput.size()!=test.actualOutput.size()) {
		if(test.getExpectedNumAnswers() != test.getActualNumAnswers()) {
			System.out.println(test.getDescription());
			System.out.println(test.getExpectedOutput());
			System.out.println("expected num answers=" + test.getExpectedNumAnswers() +
					", actual num answers=" + test.getActualNumAnswers());
			throw new RuntimeException("expectedOutput and actualOutput not the same size");
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

	private boolean hasDoubleResult(String exp) {
		if(exp.substring(0, 2).equals("%d"))
			return true;
		return false;
	}
}

/**
 * This class holds the collection of test cases, <code>tests</code> and a summary of the assessment
 * results.
 * @author dgibson
 *
 */
class GradeReport {

	private String studentName;
	private List<AutomatedTest> automatedTests = new ArrayList<AutomatedTest>();
	private List<ManualTest> manualTests = new ArrayList<ManualTest>();
	private double pointsMax=0.0;
	private double pointsEarned=0.0;
	private double pointsPercentCorrect=0.0;
	private int numTestsCorrect=0;
	private int numTestsTotal=0;
	private double numTestsPercentCorrect=0.0;

	public GradeReport() {}

	public GradeReport(String studentName, List<AutomatedTest> automatedTests, List<ManualTest> manualTests) {
		this.studentName = studentName;
		this.automatedTests = automatedTests;
		this.manualTests = manualTests;
	}

	public int getNumTestsCorrect() {
		return numTestsCorrect;
	}

	public AutomatedTest getAutomatedTest(int i) {
		return automatedTests.get(i);
	}

	public ManualTest getManualTest(int i) {
		return manualTests.get(i);
	}

	public int getNumAutomatedTests() {
		return automatedTests.size();
	}
	
	public int getNumManualTests() {
		return manualTests.size();
	}
	
	public double getPointsPercentCorrect() {
		return pointsPercentCorrect;
	}

	void setPointsPercentCorrect(double pointsPercentCorrect) {
		this.pointsPercentCorrect = pointsPercentCorrect;
	}

	public double getNumTestsPercentCorrect() {
		return numTestsPercentCorrect;
	}

	void setNumTestsPercentCorrect(double numTestsPercentCorrect) {
		this.numTestsPercentCorrect = numTestsPercentCorrect;
	}

	public String getStudentName() {
		return studentName;
	}

	public double getPointsMax() {
		return pointsMax;
	}

	public double getPointsEarned() {
		return pointsEarned;
	}

	public int getNumTestsTotal() {
		return numTestsTotal;
	}


	void incrementPointsMax(double points) {
		pointsMax += points;
	}
	
	void incrementPointsEarned(double points) {
		pointsEarned += points;
	}

	void incrementNumTestsCorrect(double num) {
		numTestsCorrect += num;
	}

	void incrementNumTestsTotal(int num) {
		numTestsTotal += num;
	}


	/**
	 * Assesses all the test cases, tallying the points earned.
	 */
//	public void assessTests() {
//		for(AutomatedTest test : automatedTests) {
//			test.assess();
//			this.pointsMax += test.pointsMax;
//			this.pointsEarned += test.pointsEarned;
//			this.numTestsCorrect += test.numCorrect;
//			this.numTestsTotal += test.numCorrect;
//		}
//		this.pointsPercentCorrect = pointsEarned/pointsMax * 100.0;
//		this.numTestsPercentCorrect = numTestsCorrect/(numTestsTotal*1.0)*100.0;
//	}

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
//	private String expectedOutput() {
//		StringBuilder res = new StringBuilder();
//		int j=1;
//		for(AutomatedTest test : automatedTests) {
//			res.append("# Test " + (j++) + " Expected Results\n");
//			res.append(test.getDescription() + "\n");
//			res.append(test.getPointsMax() + "\n");
//			res.append(test.getExpectedNumAnswers() + "\n");
//			for(int i=0; i<test.getExpectedNumAnswers(); i++) {
//				String s = test.getExpectedAnswer(i);
//				res.append(s + "\n");
//			}
//		}
//		
//		j=1;
//		for(ManualTest test : manualTests) {
//			res.append("# Manual Test " + " Expected Results\n");
//			res.append(test.getDescription() + "\n");
//			res.append(test.getExpectedOutput() + "\n");
//		}
//		
//		return res.toString();
//	}

	/**
	 * Returns a summary of each test case and an overall summary.
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		int i=1;
		res.append("Student Name: " + studentName + "\n\n");
		for(AutomatedTest test : automatedTests) {
			res.append("Test: " + (i++) + "-" + test + "\n");
		}

		res.append(String.format("Overall Summary: %.1f points out of %.1f (%.1f%%)\n",
				pointsEarned, pointsMax, pointsPercentCorrect));

		res.append(String.format("%d out of %d (%.1f%%) tests succeeded\n",
				numTestsCorrect, numTestsTotal, numTestsPercentCorrect));

		for(ManualTest test : manualTests) {
			res.append("\n");
			res.append("Test: " + (i++) + "-" + test + "\n");
		}

		return res.toString();
	}
}

/**
 * THIS NEEDS UPDATE - NOT CORRECT AT ALL!
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
class ManualTest {
	private String description;
	private String expectedOutput;
	private String actualOutput;

	public ManualTest() {}

	public ManualTest(String description, String actualOutput, String expectedOutput) {
		this.description = description;
		this.expectedOutput = expectedOutput;
		this.actualOutput = actualOutput;
	}

	public String getActualOutput() {
		return actualOutput;
	}

	public String getDescription() {
		return description;
	}

	public String getExpectedOutput() {
		return expectedOutput;
	}

	/**
	 * Returns a summary of this test case showing expected and actual results for each part as well
	 * as correctness, number correct, and points earned.
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(String.format("%s\n", this.description));
		res.append("Expected:\n");
		res.append(expectedOutput + "\n");
		res.append("Actual:\n");
		res.append(actualOutput);
		return res.toString();
	}
}

/**
 * Holds the raw out put from running the test methods, automated and manual. Each test method produces
 * a list of strings, where each string is a line of output from the test methods
 * (e.g. see TestSuite).
 * @author dgibson
 *
 */
class RawTestResults {
	private ArrayList<ArrayList<String>> automated;
	private ArrayList<ArrayList<String>> manual;
	
	public RawTestResults(ArrayList<ArrayList<String>> automated, ArrayList<ArrayList<String>> manual) {
		this.automated = automated;
		this.manual = manual;
	}
	
	public int getNumAutomatedTests( ) {
		return automated.size();
	}

	public int getNumManualTests( ) {
		return manual.size();
	}

	public ArrayList<String> getAutomatedResults(int i) {
		return automated.get(i);
	}

	public ArrayList<String> getManualResults(int i) {
		return manual.get(i);
	}
	
	

}

/**
 * Holds the test method names for all tests, automated and manual.
 * @author dgibson
 *
 */
class TestMethods {
	private ArrayList<String> automatedMethodNames;
	private ArrayList<String> manualMethodNames;
	public TestMethods(ArrayList<String> automatedMethodNames, ArrayList<String> manualMethodNames) {
		this.automatedMethodNames = automatedMethodNames;
		this.manualMethodNames = manualMethodNames;
	}
	public ArrayList<String> getAutomatedMethodNames() {
		return automatedMethodNames;
	}
	public ArrayList<String> getManualMethodNames() {
		return manualMethodNames;
	}
}

/**
 * Holds a list of {@link AutomatedTest} and a list of {@link ManualTest}.
 * Used just to transport the two lists to t
 * @author dgibson
 *
 */
class TestResultsSuite {
	private ArrayList<AutomatedTest> automatedTests = new ArrayList<>();
	private ArrayList<ManualTest> manualTests = new ArrayList<>();

	public ArrayList<AutomatedTest> getAutomatedTests() {
		return automatedTests;
	}

	public ArrayList<ManualTest> getManualTests() {
		return manualTests;
	}

	public void addAutomatedTest(AutomatedTest test) {
		automatedTests.add(test);
	}
	
	public void addManualTest(ManualTest test) {
		manualTests.add(test);
	}

}