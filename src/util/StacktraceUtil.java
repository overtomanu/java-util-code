package util;

public class StacktraceUtil {
    /**
     * Gets string of the current call stack.
     *
     * @param callStackFrom - call stack index from which to start, value should be at least 1 which includes the caller of this util method
     * @param callStackTo   - call stack index up to which to retrieve
     * @return string of the current call stack
     */
    public static String getCurrentCallStack(int callStackFrom, int callStackTo) {
        String result = "";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int startIndex = 1 + callStackFrom;
        int endIndex = 2 + callStackTo;
        if (endIndex > stackTraceElements.length) {
            endIndex = stackTraceElements.length;
        }
        for (int i = startIndex; i < endIndex; i++) {
            result += stackTraceElements[i].toString() + "\n";
        }
        return result;
    }

    private static int testMethodFactorial(int num, int level) {
        if (num == 0) {
            System.out.println(getCurrentCallStack(1, 3));
            return 1;
        }
        return num * testMethodFactorial(num - 1, level);
    }

    public static void main(String[] args) {
        testMethodFactorial(10, 5);
    }
}
