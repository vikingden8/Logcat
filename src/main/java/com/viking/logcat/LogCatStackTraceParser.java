package com.viking.logcat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class that can determine if a string matches the exception
 * stack trace pattern, and if so, can provide the java source file
 * and line where the exception occured.
 */
public final class LogCatStackTraceParser {
    /** Regex to match a stack trace line. E.g.:
     *          at com.foo.Class.method(FileName.extension:10)
     *  extension is typically java, but can be anything (java/groovy/scala/..).
     */
    private static final String EXCEPTION_LINE_REGEX =
            "\\s*at\\ (.*)\\((.*)\\..*\\:(\\d+)\\)"; //$NON-NLS-1$

    private static final Pattern EXCEPTION_LINE_PATTERN =
            Pattern.compile(EXCEPTION_LINE_REGEX);

    /**
     * Identify if a input line matches the expected pattern
     * for a stack trace from an exception.
     */
    public boolean isValidExceptionTrace(String line) {
        return EXCEPTION_LINE_PATTERN.matcher(line).find();
    }

    /**
     * Get fully qualified method name that threw the exception.
     * @param line line from the stack trace, must have been validated with
     * {@link LogCatStackTraceParser#isValidExceptionTrace(String)} before calling this method.
     * @return fully qualified method name
     */
    public String getMethodName(String line) {
        Matcher m = EXCEPTION_LINE_PATTERN.matcher(line);
        m.find();
        return m.group(1);
    }

    /**
     * Get source file name where exception was generated. Input line must be first validated with
     * {@link LogCatStackTraceParser#isValidExceptionTrace(String)}.
     */
    public String getFileName(String line) {
        Matcher m = EXCEPTION_LINE_PATTERN.matcher(line);
        m.find();
        return m.group(2);
    }

    /**
     * Get line number where exception was generated. Input line must be first validated with
     * {@link LogCatStackTraceParser#isValidExceptionTrace(String)}.
     */
    public int getLineNumber(String line) {
        Matcher m = EXCEPTION_LINE_PATTERN.matcher(line);
        m.find();
        try {
            return Integer.parseInt(m.group(3));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
