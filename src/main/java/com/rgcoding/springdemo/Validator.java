package com.rgcoding.springdemo;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public interface Validator {
    /**
     * Verify an object is an integer and that it is not None or empty.
     * 
     * @param inputValue The value to check
     * @throws IllegalArgumentException
     */
    public static void validateInteger(int inputValue) throws IllegalArgumentException {
        validateInteger(inputValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    /**
     * Verify an object is an integer and that it is not None or empty. Optionally, this method can verify the value of the number is greater or equal to a minimum. 
     * 
     * @param inputValue The value to check
     * @param minValue The minimum acceptable value
     * @throws IllegalArgumentException
     */
    public static void validateInteger(int inputValue, int minValue) throws IllegalArgumentException {
        validateInteger(inputValue, minValue, Integer.MAX_VALUE);
    }
    
    /**
     * Verify an object is an integer and that it is not None or empty. Optionally, this method can verify the value of the number is greater or equal to a minimum, less than or equal to a maximum, or within a range in value. 
     * 
     * @param inputValue The value to check
     * @param minValue The minimum acceptable value
     * @param maxValue The maximum acceptable value
     * @throws IllegalArgumentException
     */
    public static void validateInteger(int inputValue, int minValue, int maxValue) throws IllegalArgumentException {
        if(minValue > inputValue || inputValue > maxValue) {
            throw new IllegalArgumentException("Invalid integer value.");
        }
    }
    
    /**
     * Verify the input matches the regular expression pattern. Do not submit a compiled pattern; submit a string.
     * 
     * @param inputString The value to check
     * @param pattern The RegEx pattern the value must match
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    public static void validateRegexMatch(String inputString, String pattern) throws IllegalArgumentException, PatternSyntaxException {
        if (!Pattern.matches(pattern, inputString)) {
            throw new IllegalArgumentException(String.format("\'%s\' does not match regex pattern.", inputString));
        }
    }
}
