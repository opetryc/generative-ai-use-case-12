package com.softserve.myapplication;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code Utils} class offers an extensive suite of string manipulation utilities, designed
 * to assist developers in various string processing tasks without the need to reinvent common routines.
 *
 * <p>This class provides utility methods that span a broad spectrum of string operations:
 * <ul>
 *     <li>Case manipulation methods such as case swapping.</li>
 *     <li>Text abbreviation based on specific constraints.</li>
 *     <li>Extracting initials from a given string based on designated delimiters.</li>
 *     <li>Wrapping long strings by a specified width.</li>
 *     <li>String validation routines for nullity, emptiness, and blankness.</li>
 * </ul>
 * </p>
 *
 * <p>By offering such a wide array of utilities, the {@code Utils} class aims to streamline
 * string operations in Java, enhancing readability and reducing the potential for common mistakes.
 * It's a handy toolkit for developers who want to focus on their business logic and reduce boilerplate code.</p>
 *
 * <p>Most methods in this class are null-safe, returning a default value or the input itself when a null
 * input is provided, ensuring that the calling code remains free of repetitive null checks.</p>
 *
 * <p>Internally, the class employs efficient techniques and algorithms to ensure optimal performance
 * even for larger strings, making it suitable for both lightweight and more demanding applications.</p>
 *
 * <h3>Examples:</h3>
 * <pre>
 * // Abbreviating a string
 * String abbreviated = Utils.abbreviate("This is a long string", 10, 15, "...");
 * System.out.println(abbreviated);  // Outputs: "This is a..."
 *
 * // Converting a string to initials with specific delimiters
 * String initials = Utils.initials("John A. Doe", ' ', '.');
 * System.out.println(initials);  // Outputs: "JAD"
 *
 * // Swapping the case of a string
 * String swapped = Utils.swapCase("Hello World");
 * System.out.println(swapped);  // Outputs: "hELLO wORLD"
 *
 * // Wrapping a long string
 * String wrapped = Utils.wrap("This is a very long string that needs to be wrapped.", 20, "\n", true, " ");
 * System.out.println(wrapped);
 * // Outputs:
 * // This is a very
 * // long string that
 * // needs to be wrapped.
 * </pre>
 *
 * @author smelfungus
 * @version 1.0
 * @since 2023-09-10
 */
public class Utils {

    /**
     * A constant representing an index value indicating that a particular item or character was not found.
     */
    private static final int INDEX_NOT_FOUND = -1;
    /**
     * A constant representing an empty string.
     */
    private static final String EMPTY = "";

    /**
     * Abbreviates a {@code String} using provided lower and upper limits and appends a specified string to the end.
     * <p>
     * This method abbreviates the input string based on the specified lower and upper bounds. If the abbreviation
     * point is within a word, the string will be abbreviated at the word boundary (space character). If the string
     * gets abbreviated, the provided {@code appendToEnd} string will be added to the end of the result.
     * </p>
     * <p>
     * The {@link #isEmpty(CharSequence)} method is used to check if the input string is empty. If it is, the
     * string itself is returned. The method also uses the {@link #indexOf(CharSequence, CharSequence, int)}
     * utility function to locate the position of the space character within the bounds and the
     * {@link #defaultString(String)} method to ensure the {@code appendToEnd} string is not {@code null}.
     * </p>
     *
     * <h3>Usage Examples:</h3>
     * <pre>
     * abbreviate("Hello World", 0, 5, "...") → "Hello..."
     * abbreviate("Hello", 0, 10, "...") → "Hello"
     * abbreviate("This is a longer sentence", 0, 15, "...") → "This is a..."
     * abbreviate("Short", 0, 10, "...") → "Short"
     * abbreviate("Abbreviate at space", 0, 12, "..") → "Abbreviate.."
     * abbreviate(null, 0, 5, "...") → null
     * abbreviate("Boundary", 0, -1, "!!") → "Boundary!!"
     * </pre>
     *
     * @param str The input string to be abbreviated. If {@code null}, the method will return {@code null}. If empty or whitespace-only,
     *            it will return the string as is without abbreviation.
     * @param lower The lower limit used for abbreviation. Represents the minimum length up to which the string should not be abbreviated.
     *              If the string's length is less than this value, it will not be abbreviated.
     * @param upper The upper limit after which the string will be abbreviated. If the string's length exceeds this value, it will be truncated
     *              at this limit or the nearest preceding word boundary. If set to -1, the upper limit defaults to the string's length, effectively
     *              appending the {@code appendToEnd} string to the original string.
     * @param appendToEnd The string to append to the end of the abbreviated string. If {@code null}, an empty
     *                    string will be used.
     * @return The abbreviated string with the specified append string at the end if the string was abbreviated.
     * @throws IllegalArgumentException If the provided upper and lower bounds are inconsistent.
     */
    public static String abbreviate(final String str, int lower, int upper, final String appendToEnd) {
        isTrue(upper >= -1, "upper value cannot be less than -1");
        isTrue(upper >= lower || upper == -1, "upper value is less than lower value");
        if (isEmpty(str)) {
            return str;
        }

        if (lower > str.length()) {
            lower = str.length();
        }

        if (upper == -1 || upper > str.length()) {
            upper = str.length();
        }

        final StringBuilder result = new StringBuilder();
        final int index = indexOf(str, " ", lower);
        if (index == -1) {
            result.append(str, 0, upper);
            if (upper != str.length()) {
                result.append(defaultString(appendToEnd));
            }
        } else {
            result.append(str, 0, Math.min(index, upper));
            result.append(defaultString(appendToEnd));
        }

        return result.toString();
    }

    /**
     * Generates a string representing the initials of the given {@code String} using specified delimiters.
     * <p>
     * This method extracts the initials from the input string based on the delimiters provided. If an empty array of delimiters is provided,
     * the method will return an empty string. If no delimiters are specified (i.e., null), the method defaults to using whitespace as the delimiter.
     * Each initial is the first character after a delimiter or the start of the string.
     * </p>
     * <p>
     * The method uses the utility functions {@link #isEmpty(CharSequence)} to check if the input string is empty,
     * and {@link #generateDelimiterSet(char[])} to create a set of code points representing the provided delimiters for efficient lookup.
     * </p>
     *
     * <h3>Usage Examples:</h3>
     * <pre>
     * initials("John Doe", ' ') → "JD"
     * initials("Hello-World", '-') → "HW"
     * initials("Multiple delimiters", ' ', 'i') → "Md"
     * initials("NoDelimitersHere") → "N"
     * initials("NoDelimitersHere", new char[0]) → ""
     * initials(null, ' ') → null
     * </pre>
     *
     * @param str The input string from which initials are to be extracted. If {@code null} or empty, the method will return
     *            the string as is.
     * @param delimiters Varargs parameter containing characters used as delimiters. Each character represents a delimiter
     *                   that separates words in the input string. If no delimiters are provided (i.e., null), the method defaults to using
     *                   whitespace. If an empty array is provided, the method returns the {@link #EMPTY} constant.
     * @return A string representing the initials from the input string based on the provided delimiters.
     */
    public static String initials(final String str, final char... delimiters) {
        if (isEmpty(str)) {
            return str;
        }
        if (delimiters != null && delimiters.length == 0) {
            return EMPTY;
        }
        final Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen / 2 + 1];
        int count = 0;
        boolean lastWasGap = true;
        for (int i = 0; i < strLen; ) {
            final int codePoint = str.codePointAt(i);

            if (delimiterSet.contains(codePoint) || delimiters == null && Character.isWhitespace(codePoint)) {
                lastWasGap = true;
            } else if (lastWasGap) {
                newCodePoints[count++] = codePoint;
                lastWasGap = false;
            }

            i += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, count);
    }

    /**
     * Converts all the lowercase characters in a {@code String} to uppercase, and all the uppercase
     * characters to lowercase, swapping the case of each character. For characters that have title
     * case in some languages (like certain ligatures and digraphs), they are converted to lowercase.
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>{@code swapCase(null)} returns {@code null}.</li>
     *   <li>{@code swapCase("")} returns {@code ""} (an empty string).</li>
     *   <li>{@code swapCase("The Quick Brown FOX")} returns {@code "tHE qUICK bROWN fox"}.</li>
     *   <li>{@code swapCase("12345")} returns {@code "12345"} (numbers are unaffected).</li>
     *   <li>{@code swapCase("HELLO world 123")} returns {@code "hello WORLD 123"}.</li>
     * </ul>
     *
     * <p>Note: The method observes title case conversion only after a whitespace. In other cases,
     * lowercase characters are simply converted to uppercase.</p>
     *
     * @param str the input {@code String} to be swapped, may be null.
     * @return a new swapped case {@code String}, or the original string if it's empty or null.
     * @see Character#isWhitespace
     * @see Character#isUpperCase
     * @see Character#isLowerCase
     * @see Character#toTitleCase
     * @see Character#toLowerCase
     * @see Character#toUpperCase
     */
    public static String swapCase(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean whitespace = true;
        for (int index = 0; index < strLen; ) {
            final int oldCodepoint = str.codePointAt(index);
            final int newCodePoint;
            if (Character.isUpperCase(oldCodepoint) || Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
                whitespace = false;
            } else if (Character.isLowerCase(oldCodepoint)) {
                if (whitespace) {
                    newCodePoint = Character.toTitleCase(oldCodepoint);
                    whitespace = false;
                } else {
                    newCodePoint = Character.toUpperCase(oldCodepoint);
                }
            } else {
                whitespace = Character.isWhitespace(oldCodepoint);
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset++] = newCodePoint;
            index += Character.charCount(newCodePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    /**
     * Wraps the provided string to a specified length.
     *
     * <p>This method will attempt to identify suitable breakpoints within the provided
     * string, such as spaces, upon which to wrap the text. If a single word exceeds
     * the wrap length and the <code>wrapLongWords</code> flag is true, it will break
     * the word across multiple lines. If a suitable breakpoint cannot be found,
     * the string will be wrapped at the specified length.</p>
     *
     * <p>For more control over the wrap process, a custom delimiter can be specified
     * upon which to split the string. This is useful when wrapping text that uses
     * delimiters other than space.</p>
     *
     * <p><strong>Examples:</strong></p>
     * <ul>
     *   <li><code>wrap("abcdefghij", 3, "\n", true, " ")</code> returns <code>"abc\ndef\nghi\nj"</code></li>
     *   <li><code>wrap("abcdefgh ij", 3, "\n", true, " ")</code> returns <code>"abc\ndef\ngh\n ij"</code></li>
     *   <li><code>wrap("abcdefgh ij", 3, "\n", false, " ")</code> returns <code>"abc\ndefgh\n ij"</code></li>
     *   <li><code>wrap("abcdefgh,ij", 3, "\n", true, ",")</code> returns <code>"abcdefgh,\nij"</code></li>
     *   <li><code>wrap("hello world", 8, "\n", true, " ")</code> returns <code>"hello\nworld"</code></li>
     *   <li><code>wrap("The quick brown fox", 5, "\n", false, " ")</code> returns <code>"The\nquick\nbrown\nfox"</code></li>
     *   <li><code>wrap("The quick brown fox", 20, "\n", true, " ")</code> returns <code>"The quick brown fox"</code></li>
     *   <li><code>wrap("A very long word: Supercalifragilisticexpialidocious", 15, "\n", true, " ")</code> returns <code>"A very long\nword:\nSupercalifragil\nisticexpialido\ncious"</code></li>
     *   <li><code>wrap("A very long word: Supercalifragilisticexpialidocious", 15, "\n", false, " ")</code> returns <code>"A very long\nword: Supercalif\nragilisticexpia\nlidocious"</code></li>
     * </ul>
     *
     * @param str The string to wrap. If null, returns null.
     * @param wrapLength The maximum length for a line. If less than 1, defaults to 1.
     * @param newLineStr The string to use for line breaks. If null, defaults to the system line separator.
     * @param wrapLongWords If true, will wrap long words at <code>wrapLength</code>. If false, will attempt to
     *                      find a space upon which to wrap.
     * @param wrapOn A string which specifies a regex pattern on which to break the string.
     *               Defaults to a space if null or empty.
     *
     * @return A new string with lines wrapped as per the provided parameters.
     *
     * @see Pattern#compile(String)
     */
    public static String wrap(final String str,
                              int wrapLength,
                              String newLineStr,
                              final boolean wrapLongWords,
                              String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (isBlank(wrapOn)) {
            wrapOn = " ";
        }
        final Pattern patternToWrapOn = Pattern.compile(wrapOn);
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);
        int matcherSize = -1;

        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset,
                    Math.min((int) Math.min(Integer.MAX_VALUE, offset + wrapLength + 1L), inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    matcherSize = matcher.end();
                    if (matcherSize != 0) {
                        offset += matcher.end();
                        continue;
                    }
                    offset += 1;
                }
                spaceToWrapAt = matcher.start() + offset;
            }

            if (inputLineLength - offset <= wrapLength) {
                break;
            }

            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }

            if (spaceToWrapAt >= offset) {
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
            } else if (wrapLongWords) {
                if (matcherSize == 0) {
                    offset--;
                }
                wrappedLine.append(str, offset, wrapLength + offset);
                wrappedLine.append(newLineStr);
                offset += wrapLength;
                matcherSize = -1;
            } else {
                matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                if (matcher.find()) {
                    matcherSize = matcher.end() - matcher.start();
                    spaceToWrapAt = matcher.start() + offset + wrapLength;
                }

                if (spaceToWrapAt >= 0) {
                    if (matcherSize == 0 && offset != 0) {
                        offset--;
                    }
                    wrappedLine.append(str, offset, spaceToWrapAt);
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                } else {
                    if (matcherSize == 0 && offset != 0) {
                        offset--;
                    }
                    wrappedLine.append(str, offset, str.length());
                    offset = inputLineLength;
                    matcherSize = -1;
                }
            }
        }

        if (matcherSize == 0 && offset < inputLineLength) {
            offset--;
        }

        wrappedLine.append(str, offset, str.length());

        return wrappedLine.toString();
    }

    /**
     * Converts an array of delimiter characters to a set of their corresponding code points.
     * <p>
     * This utility method assists in the transformation of an array of characters into a set of integer
     * code points, which represent these characters. If the provided array is null, the set
     * will contain the code point for a space character (' '). However, if the array is empty,
     * an empty set will be returned. This is typically used to help speed up character look-up
     * operations in scenarios where multiple delimiters might be in play.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * generateDelimiterSet(new char[]{'a', 'b'}) → [97, 98] (Where 97 and 98 are the code points for 'a' and 'b' respectively)
     * generateDelimiterSet(null) → [32] (Where 32 is the code point for ' ')
     * generateDelimiterSet(new char[]{}) → [] (Empty set)
     * </pre>
     *
     * @param delimiters The array of delimiter characters to be converted.
     * @return A {@link Set} of integer code points representing the provided delimiter characters. If the input
     *         is null, the set will contain the code point for the space character. If the input is empty,
     *         the returned set will also be empty.
     */
    private static Set<Integer> generateDelimiterSet(final char[] delimiters) {
        final Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            }

            return delimiterHashSet;
        }

        for (int index = 0; index < delimiters.length; index++) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }

    /**
     * Validates that the provided boolean expression is {@code true} and throws an {@link IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     * <p>
     * This utility method aids in asserting the truth of a certain condition. If the condition (expressed
     * as a boolean) is not met, an exception is thrown with a provided message. This is commonly used for
     * parameter validation and ensuring that certain invariants hold true within a method.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * isTrue(1 < 2, "Numbers are not in order");  // No exception thrown
     * isTrue(2 < 1, "Numbers are not in order");  // Throws IllegalArgumentException with the provided message
     * </pre>
     *
     * @param expression The boolean expression to be validated.
     * @param message The exception message to use if the check fails.
     * @throws IllegalArgumentException if {@code expression} is {@code false}.
     */
    private static void isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Returns the length of the given {@code CharSequence} or 0 if the sequence is null.
     * <p>
     * This utility method provides a null-safe way to retrieve the length of a given {@code CharSequence}.
     * Instead of risking a {@code NullPointerException} by invoking {@code length()} on a potentially null
     * sequence, this method ensures that null values are safely handled by returning a length of 0.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * length("Hello") → 5
     * length(null) → 0
     * </pre>
     *
     * @param cs The sequence whose length is to be checked; can be any implementation of {@code CharSequence} or null.
     * @return The length of the provided sequence, or 0 if the sequence is null.
     */
    private static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Determines if the given {@code CharSequence} is either null or has a length of zero.
     * <p>
     * This utility method provides a concise way to check if a given {@code CharSequence} is
     * considered "empty". A sequence is deemed empty if it is null or if its length is 0.
     * This method offers a null-safe approach to such a check, preventing potential issues
     * like {@code NullPointerException} that can arise from directly querying the length of
     * a potentially null sequence.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * isEmpty("Hello") → false
     * isEmpty("") → true
     * isEmpty(null) → true
     * </pre>
     *
     * @param cs The sequence to be checked; can be any implementation of {@code CharSequence} or null.
     * @return {@code true} if the sequence is null or has a length of 0, {@code false} otherwise.
     */
    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Determines if the given {@code CharSequence} is either null, has a length of zero, or contains only whitespace characters.
     * <p>
     * This utility method evaluates if a given {@code CharSequence} is considered "blank". A sequence is
     * deemed blank if it is null, its length is 0, or it exclusively consists of whitespace characters
     * (as determined by {@link Character#isWhitespace(char)}).
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * isBlank("Hello") → false
     * isBlank("   ") → true
     * isBlank("") → true
     * isBlank(null) → true
     * </pre>
     *
     * @param cs The sequence to be checked; can be any implementation of {@code CharSequence} or null.
     * @return {@code true} if the sequence is null, has a length of 0, or consists only of whitespace characters;
     *         {@code false} otherwise.
     */
    private static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the given string if it's non-null, or the default {@link #EMPTY} string if the provided string is null.
     * <p>
     * This utility method offers a straightforward way to ensure that a string is non-null, providing a default value
     * (an empty string) when the input string is null. It leverages Java's {@link Objects#toString(Object, String)}
     * to achieve this.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * defaultString("Hello") → "Hello"
     * defaultString(null) → ""
     * </pre>
     *
     * @param str The string to be checked; can be null.
     * @return The original string if it's non-null, or the {@link #EMPTY} string if the input is null.
     */
    private static String defaultString(final String str) {
        return Objects.toString(str, EMPTY);
    }

    /**
     * Returns the index within a given {@code CharSequence} of the first occurrence of a specified {@code CharSequence},
     * starting the search at a specified position.
     * <p>
     * This method searches for the specified {@code searchSeq} within the provided {@code seq} starting from
     * the {@code startPos}. It provides support for multiple {@code CharSequence} implementations such as
     * {@link String}, {@link StringBuilder}, and {@link StringBuffer}. If either of the provided sequences is null,
     * {@link #INDEX_NOT_FOUND} is returned.
     * </p>
     *
     * <h3>Examples:</h3>
     * <pre>
     * indexOf("Hello World", "World", 0) → 6
     * indexOf(new StringBuilder("Hello World"), "World", 0) → 6
     * indexOf("Hello World", "XYZ", 0) → -1
     * </pre>
     *
     * @param seq The sequence to search in; can be any {@code CharSequence} implementation like {@link String},
     *        {@link StringBuilder}, or {@link StringBuffer}.
     * @param searchSeq The sequence to search for; can be any {@code CharSequence}.
     * @param startPos The starting position for the search.
     * @return The position of the first occurrence of the specified {@code searchSeq} in the {@code seq} after
     *         the {@code startPos}, or {@link #INDEX_NOT_FOUND} if the sequence is not found or if either of
     *         the sequences is null.
     */
    private static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        if (seq instanceof String) {
            return ((String) seq).indexOf(searchSeq.toString(), startPos);
        }
        if (seq instanceof StringBuilder) {
            return ((StringBuilder) seq).indexOf(searchSeq.toString(), startPos);
        }
        if (seq instanceof StringBuffer) {
            return ((StringBuffer) seq).indexOf(searchSeq.toString(), startPos);
        }
        return seq.toString().indexOf(searchSeq.toString(), startPos);
    }
}