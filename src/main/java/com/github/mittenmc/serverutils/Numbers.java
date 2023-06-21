package com.github.mittenmc.serverutils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeMap;

/**
 * Contains many useful methods concerning random numbers and number manipulation.
 * @author GavvyDizzle, Maximus1027
 * @version 1.0.3
 * @since 1.0
 */
public class Numbers {

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    /**
     * Converts a number to a roman numeral
     * @param number The number to convert
     * @return The number as a String roman numeral
     * @since 1.0
     */
    public static String toRomanNumeral(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRomanNumeral(number-l);
    }

    /**
     * Rounds a number to the specified number of decimal places
     *
     * @param value The value to round
     * @param places The maximum number of decimal places
     * @return The rounded number
     * @since 1.0
     */
    public static double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Keeps a number within the bounds of two numbers.
     * If the value is less than the minimum, it will return the min value.
     * If the value is greater than the maximum, it will return the max value.
     *
     * @param value The value to constrain
     * @param min The minimum value the result can be
     * @param max The maximum value the result can be
     * @return A number n where min <= n <= max.
     * @since 1.0.1
     */
    public static int constrain(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Keeps a number within the bounds of two numbers.
     * If the value is less than the minimum, it will return the min value.
     * If the value is greater than the maximum, it will return the max value.
     *
     * @param value The value to constrain
     * @param min The minimum value the result can be
     * @param max The maximum value the result can be
     * @return A number n where min <= n <= max.
     * @since 1.0.3
     */
    public static long constrain(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Keeps a number within the bounds of two numbers.
     * If the value is less than the minimum, it will return the min value.
     * If the value is greater than the maximum, it will return the max value.
     *
     * @param value The value to constrain
     * @param min The minimum value the result can be
     * @param max The maximum value the result can be
     * @return A number n where min <= n <= max.
     * @since 1.0.3
     */
    public static double constrain(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Determines if the number is between two numbers
     *
     * @param value The value to check
     * @param min The minimum value, inclusive
     * @param max The maximum value, inclusive
     * @return True if min <= value <= max
     * @since 1.0.1
     */
    public static boolean isWithinRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Determines if the number is between two numbers
     *
     * @param value The value to check
     * @param min The minimum value, inclusive
     * @param max The maximum value, inclusive
     * @return True if min <= value <= max
     * @since 1.0.3
     */
    public static boolean isWithinRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    /**
     * Determines if the number is between two numbers
     *
     * @param value The value to check
     * @param min The minimum value, inclusive
     * @param max The maximum value, inclusive
     * @return True if min <= value <= max
     * @since 1.0.3
     */
    public static boolean isWithinRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Gets a random number min <= x <= max.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return An integer between min and max (both inclusive).
     * @since 1.0
     */
    public static int randomNumber(int min, int max) {
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }

    /**
     * Gets a random number min <= x <= max.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return An integer between min and max (both inclusive).
     * @since 1.0
     */
    public static long randomNumber(long min, long max) {
        return (long) Math.floor(Math.random()*(max-min+1)+min);
    }

    /**
     * Gets a random number min <= x < max.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A double between min (inclusive) and max (exclusive).
     * @since 1.0.3
     */
    public static double randomNumber(double min, double max) {
        return Math.random()*(max-min)+min;
    }

    /**
     * Returns true chance percent of the time.
     *
     * @param chance The percent chance that the method has of returning true (0 = never, 100 = always).
     * @return True chance % of the time on average
     * @since 1.0
     */
    public static boolean percentChance(double chance) {
        return chance / 100 > Math.random();
    }

    /**
     * Returns true on average once every chance calls.
     *
     * @param chance The average number of method calls before this returns true.
     * @return True 1/chance of the calls on average
     * @since 1.0
     */
    public static boolean oneInXChance(double chance) {
        return 1 / chance > Math.random();
    }

    /**
     * Gets the time in seconds formatted like 1d 4h 12m 13s.
     * @param seconds The time in seconds to convert
     * @return The time formatted to years/days/hours/mins/secs. "None" if seconds is negative
     * @since 1.0
     */
    public static String getTimeFormatted(int seconds) {
        if (seconds < 0) {
            return "None";
        }
        else if (seconds == 0) {
            return "0s";
        }

        int years = seconds / 31536000;
        int days = (seconds % 31536000) / 86400;
        int hours = (seconds % 86400) / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;

        String time = "";
        if (years != 0) time += years + "y ";
        if (days != 0) time += days + "d ";
        if (hours != 0) time += hours + "h ";
        if (mins != 0) time += mins + "m ";
        if (secs != 0) time += secs + "s";

        return time.trim();
    }

    /**
     * Gets the time in seconds formatted like 1d 4h 12m 13s.
     * @param seconds The time in seconds to convert
     * @param noTimeRemainingString What will be returned if there is no time remaining
     * @return The time formatted to years/days/hours/mins/secs. noTimeRemainingString if seconds is <= 0
     * @since 1.0
     */
    public static String getTimeFormatted(int seconds, @NotNull String noTimeRemainingString) {
        if (seconds <= 0) {
            return noTimeRemainingString;
        }

        int years = seconds / 31536000;
        int days = (seconds % 31536000) / 86400;
        int hours = (seconds % 86400) / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;

        String time = "";
        if (years != 0) time += years + "y ";
        if (days != 0) time += days + "d ";
        if (hours != 0) time += hours + "h ";
        if (mins != 0) time += mins + "m ";
        if (secs != 0) time += secs + "s";

        return time.trim();
    }

    /**
     * Gets the number nicely formatted to a power of 1,000.
     * Most numbers will be formatted with two decimal places and up to 3 numbers before the decimal.
     * e.g. 10,234,567 -> 10.23M
     * The valid powers are none(<1000^1) k(<1000^2) M(<1000^3) B(<1000^4) T(<1000^5) q(<1000^6) Q(<1000^7)
     *
     * @param count The number to add a suffix to.
     * @return The number with the appropriate suffix.
     * @since 1.0
     */
    public static String withSuffix(long count) {
        if (count < 1000L) {
            return String.valueOf(count);
        }
        int exp = (int)(Math.log(count) / Math.log(1000.0));
        return String.format("%.2f%c", count / Math.pow(1000.0, exp), "kMBTqQ".charAt(exp - 1));
    }

    /**
     * Gets the number nicely formatted to a power of 1,000.
     * If the number is less than 1000, it will have two decimal places (formatted as cents).
     * Most numbers will be formatted with two decimal places and up to 3 numbers before the decimal.
     * e.g. 10,234,567 -> 10.23M
     * The valid powers are none(<1000^1) k(<1000^2) M(<1000^3) B(<1000^4) T(<1000^5) q(<1000^6) Q(<1000^7)
     *
     * @param n The number to add a suffix to.
     * @return The number with the appropriate suffix.
     * @since 1.0
     */
    public static String withSuffix(double n) {
        if (n < 1000L) {
            return String.valueOf(Numbers.round(n, 2));
        }
        return withSuffix((long) n);
    }

    /**
     * Evaluates a mathematical expression.
     * Handles the four basic operations, + - * /,
     * parenthesis, exponents ^ , sqrt,
     * and trig functions, cos, sin, tan
     *
     * @param str The expression to evaluate
     * @return The answer to this expression
     * @see <a href="https://stackoverflow.com/a/26227947/12501280">Stack Overflow</a>
     * @since 1.0
     */
    public static double eval(@NotNull final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
