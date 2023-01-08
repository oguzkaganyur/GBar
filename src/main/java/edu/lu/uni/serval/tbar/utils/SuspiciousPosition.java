package edu.lu.uni.serval.tbar.utils;

/**
 * Information of detected suspicious code positions.
 * 
 * @author kui.liu
 *
 */
public class SuspiciousPosition implements Comparable<SuspiciousPosition> {
	public String classPath;
	public Integer lineNumber;
	public Double suspiciousValue;

	public SuspiciousPosition(String className, Integer lineNumber, Double suspiciousValue) {
		super();
		this.classPath = className;
		this.lineNumber = lineNumber;
		this.suspiciousValue = suspiciousValue;
	}

	public SuspiciousPosition() {

	}

	@Override
	public int compareTo(SuspiciousPosition suspStmt) {
		int compResult = this.suspiciousValue.compareTo(suspStmt.suspiciousValue);
		if (compResult == 0) {
			compResult = suspStmt.lineNumber.compareTo(this.lineNumber);
		}
		return compResult;
	}

	@Override
	public String toString() {
		return this.classPath + "@" + this.lineNumber + "@" + this.suspiciousValue;
	}
}
