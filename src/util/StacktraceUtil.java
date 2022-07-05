package util;

public class StacktraceUtil {
	public static String getCurrentCallStack(int level) {
		String result="";
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		int endIndex=2+level;
		if(endIndex>stackTraceElements.length) {
			endIndex=stackTraceElements.length;
		}
		for(int i=2;i<endIndex;i++) {
			result+=stackTraceElements[i].toString()+"\n";
		}
		return result;
	}
	
	private static int testMethodFactorial(int num,int level) {
		if(num==0) {
			System.out.println(getCurrentCallStack(level));
			return 1;
		}
		return num*testMethodFactorial(num-1,level);
	}
	
	public static void main(String[] args) {
		testMethodFactorial(10,5);
	}
}
