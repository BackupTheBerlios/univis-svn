/*
// $Id: //open/mondrian/src/main/mondrian/util/Format.java#14 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2000-2005 Kana Software, Inc. and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 2 November, 2000
*/

package mondrian.util;
import mondrian.olap.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * <code>Format</code> formats numbers, strings and dates according to the
 * same specification as Visual Basic's
 * <code>format()</code> function.  This function is described in more detail
 * <a href="http://www.apostate.com/programming/vb-format.html">here</a>.  We
 * have made the following enhancements to this specification:<ul>
 *
 * <li>if the international currency symbol (&#x00a4;) occurs in a format
 *   string, it is translated to the locale's currency symbol.</li>
 *
 * <li>the format string "Currency" is translated to the locale's currency
 *   format string. Negative currency values appear in parentheses.</li>
 *
 * <li>the string "USD" (abbreviation for U.S. Dollars) may occur in a format
 *   string.</li>
 *
 * </ul>
 *
 * <p>One format object can be used to format multiple values, thereby
 * amortizing the time required to parse the format string.  Example:</p>
 *
 * <pre><code>
 * double[] values;
 * Format format = new Format("##,##0.###;(##,##0.###);;Nil");
 * for (int i = 0; i < values.length; i++) {
 *   System.out.println("Value #" + i + " is " + format.format(values[i]));
 * }
 * </code></pre>
 *
 * <p>Still to be implemented:<ul>
 *
 * <li>String formatting (upper-case, lower-case, fill from left/right)</li>
 *
 * <li>Use client's timezone for printing times.</li>
 *
 * </ul>
 **/
public class Format {
    private String formatString;
    private BasicFormat format;
    private FormatLocale locale;
    /**
     * Maps (formatString, locale) pairs to Format objects.
     */
    private static Map cache = new HashMap();

    static final char[] digits = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    static final char thousandSeparator_en = ',';
    static final char decimalPlaceholder_en = '.';
    static final String dateSeparator_en = "/";
    static final String timeSeparator_en = ":";
    static final String currencySymbol_en = "$";
    static final String currencyFormat_en = "$#,##0.00";
    static final String[] daysOfWeekShort_en = {
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };
    static final String[] daysOfWeekLong_en = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
        "Saturday"
    };
    static final String[] monthsShort_en = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    static final String[] monthsLong_en = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };
    static final char intlCurrencySymbol = '\u08a4';

    /**
     * Maps strings representing locales (for example, "en_US_Boston",
     * "en_US", "en", or "" for the default) to a {@link FormatLocale}.
     */
    static final Map mapLocaleToFormatLocale = new HashMap();

    /**
     * Locale for US English, also the default for English and for all
     * locales.
     */
    static final FormatLocale locale_US = createLocale(
        '\0', '\0', null, null, null, null, null, null, null, null,
        Locale.US);

    private static LocaleFormatFactory localeFormatFactory;

    public static void main(String[] args)
    {
        PrintWriter pw = new PrintWriter(
            new java.io.OutputStreamWriter(System.out));
/*
        String[] timeZones = TimeZone.getAvailableIDs();
        for (int i = 0; i < timeZones.length; i++) {
            TimeZone tz = TimeZone.getTimeZone(timeZones[i]);
            pw.println(
                "Id=" + tz.getID() +
                ", offset=" + tz.getRawOffset() / (60 * 60 * 1000) +
                ", daylight=" + tz.useDaylightTime());
        }
        pw.flush();
*/
        String[] numberFormats = {
            // format                +6          -6           0           .6          null
            // ===================== =========== ============ =========== =========== =========
            "",                      "6",        "-6",        "0",        "0.6",      "",
            "0",                     "6",        "-6",        "0",        "1",        "",
            "0.00",                  "6.00",     "-6.00",     "0.00",     "0.60",     "",
            "#,##0",                 "6",        "-6",        "0",        "1",        "",
            "#,##0.00;;;Nil",        "6.00",     "-6.00",     "0.00",     "0.60",     "Nil",
            "$#,##0;($#,##0)",       "$6",       "($6)",      "$0",       "$1",       "",
            "$#,##0.00;($#,##0.00)", "$6.00",    "($6.00)",   "$0.00",    "$0.60",    "",
            "0%",                    "600%",     "-600%",     "0%",       "60%",      "",
            "0.00%",                 "600.00%",  "-600.00%",  "0.00%",    "60.00%",   "",
            "0.00E+00",              "6.00E+00", "-6.00E+00", "0.00E+00", "6.00E-01", "",
            "0.00E-00",              "6.00E00",  "-6.00E00",  "0.00E00",  "6.00E-01", "",
            "$#,##0;;\\Z\\e\\r\\o",  "$6",       "$-6",       "Zero",     "$1",       "",
            "#,##0.0 USD",           "6.0 USD",  "-6.0 USD",  "0.0 USD",  "0.6 USD",  "",
            "General Number",        "6",        "-6",        "0",        "0.6",      "",
            "Currency",              "$6.00",    "($6.00)",   "$0.00",    "$0.60",    "",
            "Fixed",                 "6",        "-6",        "0",        "1",        "",
            "Standard",              "6",        "-6",        "0",        "1",      "",
            "Percent",               "600%",     "-600%",     "0%",       "60%",      "",
            "Scientific",            "6.00e+00", "-6.00e+00", "0.00e+00", "6.00e-01", "",
            "True/False",            "True",     "True",      "False",    "True",     "False",
            "On/Off",                "On",       "On",        "Off",      "On",       "Off",
            "Yes/No",                "Yes",      "Yes",       "No",       "Yes",      "No",
        };

        String[] dateFormats = {
            "dd-mmm-yy",
            "h:mm:ss AM/PM",
            "hh:mm",
            "Long Date",
            "Medium Date",
            "Short Date",
            "Long Time",
            "Medium Time",
            "Short Time",
        };

        FormatLocale localeFra = createLocale(
            '.', // thousandSeparator = ',' in en
            ',', // decimalPlaceholder = '.' in en
            "-", // dateSeparator = "/" in en
            "#", // timeSeparator = ":" in en
            "FF", // currencySymbol = "$" in en
            "#.##0-00FF", // currencyFormat = "$#,##0.##" in en
            new String[] {
                "Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"},
            new String[] {
                "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi",
                "Samedi"},
            new String[] {
                "Jan", "Fev", "Mar", "Avr", "Mai", "Jui", "Jui", "Aou", "Sep",
                "Oct", "Nov", "Dec"},
            new String[] {
                "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Aout", "Septembre", "Octobre", "Novembre",
                "Decembre"},
            Locale.FRENCH);


        Object[] numbers = {
            new Double(6), new Double(-6), new Double(0), new Double(.6),
            null,
            new Long(6), new Long(-6), new Long(0)};
        Double d = new Double(3141592.653589793);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(1969, 3, 29, 20, 9, 6); // note that month #3 == April
        java.util.Date date = calendar.getTime();
        calendar.set(2010, 8, 7, 6, 5, 4); // 06:05:04 am, 7th sep 2010
        java.util.Date date2 = calendar.getTime();

        pw.println("Start test of mondrian.util.Format.");

//      testFormat(pw, null, date2, "mm/##/yy", "09/##/10");
//      testFormat(pw, null, new Double(123.45), "E+", "1E2");
//      testFormat(pw, null, new Long(0), "0.00E+00", "0.00E+00");
//      testFormat(pw, null, new Double(0), "0.00E+00", "0.00E+00");
//      testFormat(pw, null, new Double(0), "0.00", "0.00");
//      testFormat(pw, new Double(-5.0), "#,##0.00;;;Nil", "1");
//      testFormat(pw, date, "m", null);
//      testFormat(pw, date, "", null);
//      testFormat(pw, null, new Double(0), "0%", "0%");
//      testFormat(pw, null, new Double(1.2), "" + intlCurrencySymbol + "#", "$1");
//      testFormat(pw, localeFra, d, "Currency", null);


        pw.println();
        pw.println("Exhaustive tests on various numbers.");
        for (int i = 0; i < numberFormats.length / 6; i++) {
            String format = numberFormats[i * 6];
            for (int j = 0; j < numbers.length; j++) {
                int x = (j < 5 ? j + 1 : j - 4);
                String result = numberFormats[i * 6 + x];
                testFormat(pw, null, numbers[j], format, result);
            }
        }

        pw.println();
        pw.println("Numbers in French.");
        for (int i = 0; i < numberFormats.length / 6; i++) {
            String format = numberFormats[i * 6];
            testFormat(pw, localeFra, d, format, null);
        }

        pw.println();
        pw.println("Some tricky numbers.");
        testFormat(pw, null, new Double(40.385), "##0.0#", "40.38");
        testFormat(pw, null, new Double(40.386), "##0.0#", "40.39");
        testFormat(pw, null, new Double(40.384), "##0.0#", "40.38");
        testFormat(pw, null, new Double(40.385), "##0.#", "40.4");
        testFormat(pw, null, new Double(40.38), "##0.0#", "40.38");
        testFormat(pw, null, new Double(-40.38), "##0.0#", "-40.38");
        testFormat(pw, null, new Double(0.040385), "#0.###", "0.04");
        testFormat(pw, null, new Double(0.040385), "#0.000", "0.040");
        testFormat(pw, null, new Double(0.040385), "#0.####", "0.0404");
        testFormat(pw, null, new Double(0.040385), "00.####", "00.0404");
        testFormat(pw, null, new Double(0.040385), ".00#", ".04");
        testFormat(pw, null, new Double(0.040785), ".00#", ".041");
        testFormat(pw, null, new Double(99.9999), "##.####", "99.9999");
        testFormat(pw, null, new Double(99.9999), "##.###", "100");
        testFormat(pw, null, new Double(99.9999), "##.00#", "100.00");
        testFormat(pw, null, new Double(.00099), "#.00", ".00");
        testFormat(pw, null, new Double(.00099), "#.00#", ".001");
        testFormat(pw, null, new Double(12.34), "#.000##", "12.340");

        // "Standard" must use thousands separator, and round
        testFormat(pw, null, new Double(1234567.89), "Standard", "1,234,568");

        // must use correct alternate for 0
        testFormat(pw, null, new Double(0), "$#,##0;;\\Z\\e\\r\\o", "Zero");

        // an existing bug
        pw.println(
            "The following case illustrates an outstanding bug.  " +
            "Should be able to override '.' to '-', " +
            "so result should be '3.141.592-65 FF'.");
        testFormat(pw, localeFra, d, "#.##0-00 FF", null);

        pw.println();
        pw.println("Test several date formats on one date.");
        for (int i = 0; i < dateFormats.length; i++) {
            String format = dateFormats[i];
            testFormat(pw, null, date, format, null);
        }

        pw.println();
        pw.println("Dates in French.");
        for (int i = 0; i < dateFormats.length; i++) {
            String format = dateFormats[i];
            testFormat(pw, localeFra, date, format, null);
        }

        pw.println();
        pw.println("Test all possible tokens.");
        for (int i = 0; i < tokens.length; i++) {
            Token fe = tokens[i];
            Object o;
            if (fe.isNumeric()) {
                o = d;
            } else if (fe.isDate()) {
                o = date;
            } else if (fe.isString()) {
                o = "mondrian";
            } else {
                o = d;
            }
            testFormat(pw, null, o, fe.token, null);
        }

        pw.println();
        pw.println("Some tricky dates.");

        // must not throw exception
        testFormat(pw, null, date2, "mm/##/yy", "09/##/10");

        // must recognize lowercase "dd"
        testFormat(pw, null, date2, "mm/dd/yy", "09/07/10");

        // must print '7' not '07'
        testFormat(pw, null, date2, "mm/d/yy", "09/7/10");

        // must not decrement month by one (cuz java.util.Calendar is 0-based)
        testFormat(pw, null, date2, "mm/dd/yy", "09/07/10");

        // must recognize "mmm"
        testFormat(pw, null, date2, "mmm/dd/yyyy", "Sep/07/2010");

        // "mm" means minute, not month, when following "hh"
        testFormat(pw, null, date2, "hh/mm/ss", "06/05/04");

        // must recognize "Long Date" etc.
        testFormat(pw, null, date2, "Long Date", "Tuesday, September 07, 2010");

        // international currency symbol
        testFormat(pw, null, new Double(1.2), "" + intlCurrencySymbol + "#", "$1");

        pw.println();
        pw.println("End of test.");

        pw.flush();
    }

    static private void testFormat(
        PrintWriter pw, FormatLocale locale, Object o, String f, String result)
    {
        pw.print(
            "format(" + o + "," +
            (f == null ? "null" : "'" + f + "'") +
            ")");
        pw.flush();
        Format format = new Format(f, locale);
        String s = format.format(o);
        pw.print(" --> '" + s + "'");
        if (result == null) {
            pw.println();
        } else if (s.equals(result)) {
            pw.println(" CORRECT");
        } else {
            pw.println(" INCORRECT - should be '" + result + "'");
        }
        pw.flush();
    }

    /**
     * Formats an object using a format string, according to a given locale. If
     * you need to format many objects using the same format string, use {@link
     * #Format(Object,String,Locale)}.
     **/
    static String format(Object o, String formatString, Locale locale)
    {
        Format format = new Format(formatString, locale);
        return format.format(o);
    }

    private static class Token {
        int code;
        int flags;
        String token;

        Token(int code, int flags, String token)
        {
            this.code = code;
            this.flags = flags;
            this.token = token;
        }

        boolean isSpecial()
        {
            return (flags & SPECIAL) == SPECIAL;
        }

        boolean isNumeric()
        {
            return (flags & NUMERIC) == NUMERIC;
        }

        boolean isDate()
        {
            return (flags & DATE) == DATE;
        }

        boolean isString()
        {
            return (flags & STRING) == STRING;
        }

        BasicFormat makeFormat(FormatLocale locale)
        {
            if (isDate()) {
                return new DateFormat(code, token, locale, false);
            } else if (isNumeric()) {
                return new LiteralFormat(code, token);
            } else if (isString()) {
                throw new Error();
            } else {
                return new LiteralFormat(token);
            }
        }
    };

    /**
     * BasicFormat is the interface implemented by the classes which do all
     * the work.  Whereas {@link Format} has only one method for formatting,
     * {@link Format#format(Object)}, this class provides methods for several
     * primitive types.  To make it easy to combine formatting objects, all
     * methods write to a {@link PrintWriter}.
     *
     * The base implementation of most of these methods throws an error, there
     * is no requirement that a derived class implements all of these methods.
     * It is up to {@link Format#parseFormatString} to ensure that, for example,
     * the {@link #format(double,PrintWriter)} method is never called for
     * {@link DateFormat}.
     */
    static class BasicFormat {
        int code;

        BasicFormat() {
            this(0);
        }
        BasicFormat(int code) {
            this.code = code;
        }
        boolean isNumeric() {
            return false;
        }
        boolean isDate() {
            return false;
        }
        boolean isString() {
            return false;
        }
        void format(Object o, PrintWriter pw) {
            if (o == null) {
                formatNull(pw);
            } else if (o instanceof Double) {
                format(((Double) o).doubleValue(), pw);
            } else if (o instanceof Float) {
                format(((Float) o).floatValue(), pw);
            } else if (o instanceof Integer) {
                format(((Integer) o).intValue(), pw);
            } else if (o instanceof Long) {
                format(((Long) o).longValue(), pw);
            } else if (o instanceof Short) {
                format(((Short) o).shortValue(), pw);
            } else if (o instanceof Byte) {
                format(((Byte) o).byteValue(), pw);
            } else if (o instanceof BigDecimal) {
                format(((BigDecimal) o).doubleValue(), pw);
            } else if (o instanceof BigInteger) {
                format(((BigInteger) o).longValue(), pw);
            } else if (o instanceof String) {
                format((String) o, pw);
            } else if (o instanceof java.util.Date) {
                // includes java.sql.Date, java.sql.Time and java.sql.Timestamp
                format((Date) o, pw);
            } else {
                pw.print(o.toString());
            }
        }
        void formatNull(PrintWriter pw) {
            pw.print("(null)");
        }
        void format(double d, PrintWriter pw) {
            throw new Error();
        }
        void format(long n, PrintWriter pw) {
            throw new Error();
        }
        void format(String s, PrintWriter pw) {
            throw new Error();
        }
        void format(Date date, PrintWriter pw) {
            Calendar calendar = Calendar.getInstance(); // todo: use locale
            calendar.setTime(date);
            format(calendar, pw);
        }
        void format(Calendar calendar, PrintWriter pw) {
            throw new Error();
        }
    };

    /**
     * AlternateFormat is an implementation of {@link BasicFormat} which
     * allows a different format to be used for different kinds of values.  If
     * there are 4 formats, purposes are as follows:<ol>
     * <li>positive numbers</li>
     * <li>negative numbers</li>
     * <li>zero</li>
     * <li>null values</li>
     * </ol>
     *
     * If there are fewer than 4 formats, the first is used as a fall-back.
     * See the <a href="http://www.apostate.com/programming/vb-format.html">the
     * visual basic format specification</a> for more details.
     */
    static class AlternateFormat extends BasicFormat {
        BasicFormat[] formats;

        AlternateFormat(BasicFormat[] formats)
        {
            this.formats = formats;
        }

        void formatNull(PrintWriter pw) {
            if (formats.length >= 4) {
                formats[3].format(0, pw);
            } else {
                super.formatNull(pw);
            }
        }
        void format(double n, PrintWriter pw) {
            if (formats.length == 0) {
                pw.print(n);
            } else {
                int i;
                if (n == 0 &&
                    formats.length >= 3 &&
                    formats[2] != null) {
                    i = 2;
                } else if (n < 0 &&
                           formats.length >= 2 &&
                           formats[1] != null) {
                    n = -n;
                    i = 1;
                } else {
                    i = 0;
                }
                formats[i].format(n, pw);
            }
        }
        void format(long n, PrintWriter pw) {
            if (formats.length == 0) {
                pw.print(n);
            } else {
                int i;
                if (n == 0 &&
                    formats.length >= 3 &&
                    formats[2] != null) {
                    i = 2;
                } else if (n < 0 &&
                           formats.length >= 2 &&
                           formats[1] != null) {
                    n = -n;
                    i = 1;
                } else {
                    i = 0;
                }
                formats[i].format(n, pw);
            }
        }
        void format(String s, PrintWriter pw) {
            formats[0].format(s, pw);
        }
        void format(Date date, PrintWriter pw) {
            formats[0].format(date, pw);
        }
        void format(Calendar calendar, PrintWriter pw) {
            formats[0].format(calendar, pw);
        }
    };

    /**
     * LiteralFormat is an implementation of {@link BasicFormat} which prints
     * a constant value, regardless of the value to be formatted.
     *
     * @see CompoundFormat
     */
    static class LiteralFormat extends BasicFormat
    {
        String s;

        LiteralFormat(String s)
        {
            this(FORMAT_LITERAL, s);
        }

        LiteralFormat(int code, String s)
        {
            super(code);
            this.s = s;
        }

        // override Format
        void format(Object o, PrintWriter pw) {
            pw.print(s);
        }
        void format(double d, PrintWriter pw) {
            pw.print(s);
        }
        void format(long n, PrintWriter pw) {
            pw.print(s);
        }
        void format(String s, PrintWriter pw) {
            pw.print(s);
        }
        void format(Date date, PrintWriter pw) {
            pw.print(s);
        }
        void format(Calendar calendar, PrintWriter pw) {
            pw.print(s);
        }
    };

    /**
     * CompoundFormat is an implementation of {@link BasicFormat} where each
     * value is formatted by applying a sequence of format elements.  Each
     * format element is itself a format.
     *
     * @see AlternateFormat
     */
    static class CompoundFormat extends BasicFormat
    {
        BasicFormat[] formats;
        CompoundFormat(BasicFormat[] formats)
        {
            this.formats = formats;
        }

        public void format(Object v, PrintWriter pw)
        {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
        void format(double v, PrintWriter pw) {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
        void format(long v, PrintWriter pw) {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
        void format(String v, PrintWriter pw) {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
        void format(Date v, PrintWriter pw) {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
        void format(Calendar v, PrintWriter pw) {
            for (int i = 0; i < formats.length; i++) {
                formats[i].format(v, pw);
            }
        }
    };

    /**
     * JavaFormat is an implementation of {@link BasicFormat} which prints
     * values using Java's default formatting for their type.
     * <code>null</code> values appear as an empty string.
     */
    static class JavaFormat extends BasicFormat
    {
        JavaFormat()
        {
        }
        // No need to override format(Object,PrintWriter) or
        // format(Date,PrintWriter).
        void format(double d, PrintWriter pw) {
            pw.print(d);
        }
        void format(long n, PrintWriter pw) {
            pw.print(n);
        }
        void format(String s, PrintWriter pw) {
            pw.print(s);
        }
        void format(Calendar calendar, PrintWriter pw) {
            pw.print(calendar.getTime());
        }
    };

    /**
     * FallbackFormat catches un-handled datatypes and prints the original
     * format string.  Better than giving an error.  Abstract base class for
     * NumericFormat and DateFormat.
     */
    static abstract class FallbackFormat extends BasicFormat
    {
        String token;

        FallbackFormat(int code, String token)
        {
            super(code);
            this.token = token;
        }

        private void printToken(PrintWriter pw) {
            pw.print(token);
        }

        void format(double d, PrintWriter pw) {
            printToken(pw);
        }
        void format(long n, PrintWriter pw) {
            printToken(pw);
        }
        void format(String s, PrintWriter pw) {
            printToken(pw);
        }
        void format(Calendar calendar, PrintWriter pw) {
            printToken(pw);
        }
    };

    /**
     * NumericFormat is an implementation of {@link BasicFormat} which prints
     * numbers with a given number of decimal places, leading zeroes, in
     * exponential notation, etc.
     *
     * <p>It is implemented using {@link FloatingDecimal}, which is a
     * barely-modified version of <code>java.lang.FloatingDecimal</code>.
     */
    static class NumericFormat extends FallbackFormat
    {
        FormatLocale locale;
        int digitsLeftOfPoint;
        int zeroesLeftOfPoint;
        int digitsRightOfPoint;
        int zeroesRightOfPoint;
        int digitsRightOfExp;
        int zeroesRightOfExp;

        /** Number of decimal places to shift the number left before formatting
         * it.  2 means multiply by 100; -3 means divide by 1000. */
        int decimalShift;
        char expChar;
        boolean expSign;
        boolean useDecimal; // not used
        boolean useThouSep;

        NumericFormat(
            String token, FormatLocale locale,
            int expFormat,
            int digitsLeftOfPoint, int zeroesLeftOfPoint,
            int digitsRightOfPoint, int zeroesRightOfPoint,
            int digitsRightOfExp, int zeroesRightOfExp,
            boolean useDecimal, boolean useThouSep)
        {
            super(FORMAT_NULL, token);
            this.locale = locale;
            switch (expFormat) {
            case FORMAT_E_MINUS_UPPER:
                this.expChar = 'E';
                this.expSign = false;
                break;
            case FORMAT_E_PLUS_UPPER:
                this.expChar = 'E';
                this.expSign = true;
                break;
            case FORMAT_E_MINUS_LOWER:
                this.expChar = 'e';
                this.expSign = false;
                break;
            case FORMAT_E_PLUS_LOWER:
                this.expChar = 'e';
                this.expSign = true;
                break;
            default:
                this.expChar = 0;
                this.expSign = false;
            }
            this.digitsLeftOfPoint = digitsLeftOfPoint;
            this.zeroesLeftOfPoint = zeroesLeftOfPoint;
            this.digitsRightOfPoint = digitsRightOfPoint;
            this.zeroesRightOfPoint = zeroesRightOfPoint;
            this.digitsRightOfExp = digitsRightOfExp;
            this.zeroesRightOfExp = zeroesRightOfExp;
            this.useDecimal = useDecimal;
            this.useThouSep = useThouSep;
            this.decimalShift = 0; // set later
        }

        void format(Double n, PrintWriter pw)
        {
            format(n.doubleValue(), pw);
        }

        void format(double n, PrintWriter pw)
        {
            mondrian.util.Format.FloatingDecimal fd
                = new mondrian.util.Format.FloatingDecimal(n);
            fd.shift(decimalShift);
            String s = fd.toJavaFormatString(
                zeroesLeftOfPoint,
                locale.decimalPlaceholder,
                zeroesRightOfPoint,
                zeroesRightOfPoint + digitsRightOfPoint,
                expChar,
                expSign,
                zeroesRightOfExp,
                useThouSep ? locale.thousandSeparator : '\0');
            pw.print(s);
        }

        void format(Long n, PrintWriter pw)
        {
            format(n.longValue(), pw);
        }

        void format(long n, PrintWriter pw)
        {
            mondrian.util.Format.FloatingDecimal fd
                = new mondrian.util.Format.FloatingDecimal(n);
            fd.shift(decimalShift);
            String s = fd.toJavaFormatString(
                zeroesLeftOfPoint,
                locale.decimalPlaceholder,
                zeroesRightOfPoint,
                zeroesRightOfPoint + digitsRightOfPoint,
                expChar,
                expSign,
                zeroesRightOfExp,
                useThouSep ? locale.thousandSeparator : '\0');
            pw.print(s);
        }
    };

    /**
     * DateFormat is an element of a {@link CompoundFormat} which has a value
     * when applied to a {@link Calendar} object.  (Values of type {@link Date}
     * are automatically converted into {@link Calendar}s when you call {@link
     * BasicFormat#format(Date, PrintWriter)} calls to format other kinds of
     * values give a runtime error.)
     *
     * In a typical use of this class, a format string such as "m/d/yy" is
     * parsed into DateFormat objects for "m", "d", and "yy", and {@link
     * LiteralFormat} objects for "/".  A {@link CompoundFormat} object is
     * created to bind them together.
     */
    static class DateFormat extends FallbackFormat
    {
        FormatLocale locale;
        boolean twelveHourClock;

        DateFormat(int code, String s, FormatLocale locale, boolean twelveHourClock)
        {
            super(code, s);
            this.locale = locale;
            this.twelveHourClock = twelveHourClock;
        }
        void setTwelveHourClock(boolean twelveHourClock)
        {
            this.twelveHourClock = twelveHourClock;
        }
        void format(Calendar calendar, PrintWriter pw)
        {
            format(code, calendar, pw);
        }
        private void format(int code, Calendar calendar, PrintWriter pw)
        {
            switch (code) {
            case FORMAT_C:
            {
                boolean dateSet = !(
                    calendar.get(Calendar.DAY_OF_YEAR) == 0 &&
                    calendar.get(Calendar.YEAR) == 0);
                boolean timeSet = !(
                    calendar.get(Calendar.SECOND) == 0 &&
                    calendar.get(Calendar.MINUTE) == 0 &&
                    calendar.get(Calendar.HOUR) == 0);
                if (dateSet) {
                    format(FORMAT_DDDDD, calendar, pw);
                }
                if (dateSet && timeSet) {
                    pw.print(" ");
                }
                if (timeSet) {
                    format(FORMAT_TTTTT, calendar, pw);
                }
                break;
            }
            case FORMAT_D:
            {
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                pw.print(d);
                break;
            }
            case FORMAT_DD:
            {
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                if (d < 10)
                    pw.print("0");
                pw.print(d);
                break;
            }
            case FORMAT_DDD:
            {
                int dow = calendar.get(Calendar.DAY_OF_WEEK);
                pw.print(locale.daysOfWeekShort[dow - 1]); // e.g. Sun
                break;
            }
            case FORMAT_DDDD:
            {
                int dow = calendar.get(Calendar.DAY_OF_WEEK);
                pw.print(locale.daysOfWeekLong[dow - 1]); // e.g. Sunday
                break;
            }
            case FORMAT_DDDDD:
            {
                // Officially, we should use the system's short date
                // format. But for now, we always print using the default
                // format, m/d/yy.
                format(FORMAT_M, calendar,pw);
                pw.print(locale.dateSeparator);
                format(FORMAT_D, calendar,pw);
                pw.print(locale.dateSeparator);
                format(FORMAT_YY, calendar,pw);
                break;
            }
            case FORMAT_DDDDDD:
            {
                format(FORMAT_MMMM_UPPER, calendar, pw);
                pw.print(" ");
                format(FORMAT_DD, calendar, pw);
                pw.print(", ");
                format(FORMAT_YYYY, calendar, pw);
                break;
            }
            case FORMAT_W:
            {
                int dow = calendar.get(Calendar.DAY_OF_WEEK);
                pw.print(dow);
                break;
            }
            case FORMAT_WW:
            {
                int woy = calendar.get(Calendar.WEEK_OF_YEAR);
                pw.print(woy);
                break;
            }
            case FORMAT_M:
            {
                int m = calendar.get(Calendar.MONTH) + 1; // 0-based
                pw.print(m);
                break;
            }
            case FORMAT_MM:
            {
                int mm = calendar.get(Calendar.MONTH) + 1; // 0-based
                if (mm < 10)
                    pw.print("0");
                pw.print(mm);
                break;
            }
            case FORMAT_MMM_LOWER:
            case FORMAT_MMM_UPPER:
            {
                int m = calendar.get(Calendar.MONTH) + 1;
                pw.print(locale.monthsShort[m - 1]); // e.g. Jan
                break;
            }
            case FORMAT_MMMM_LOWER:
            case FORMAT_MMMM_UPPER:
            {
                int m = calendar.get(Calendar.MONTH) + 1;
                pw.print(locale.monthsLong[m - 1]); // e.g. January
                break;
            }
            case FORMAT_Q:
            {
                int m = calendar.get(Calendar.MONTH);
                // 0(Jan) -> q1, 1(Feb) -> q1, 2(Mar) -> q1, 3(Apr) -> q2
                int q = m / 3 + 1;
                pw.print(q);
                break;
            }
            case FORMAT_Y:
            {
                int doy = calendar.get(Calendar.DAY_OF_YEAR);
                pw.print(doy);
                break;
            }
            case FORMAT_YY:
            {
                int y = calendar.get(Calendar.YEAR) % 100;
                if (y < 10) {
                    pw.print("0");
                }
                pw.print(y);
                break;
            }
            case FORMAT_YYYY:
            {
                int y = calendar.get(Calendar.YEAR);
                pw.print(y);
                break;
            }
            case FORMAT_H:
            {
                int h = calendar.get(
                    twelveHourClock ? Calendar.HOUR : Calendar.HOUR_OF_DAY);
                pw.print(h);
                break;
            }
            case FORMAT_HH:
            {
                int h = calendar.get(
                    twelveHourClock ? Calendar.HOUR : Calendar.HOUR_OF_DAY);
                if (h < 10)
                    pw.print("0");
                pw.print(h);
                break;
            }
            case FORMAT_N:
            {
                int n = calendar.get(Calendar.MINUTE);
                pw.print(n);
                break;
            }
            case FORMAT_NN:
            {
                int n = calendar.get(Calendar.MINUTE);
                if (n < 10)
                    pw.print("0");
                pw.print(n);
                break;
            }
            case FORMAT_S:
            {
                int s = calendar.get(Calendar.SECOND);
                pw.print(s);
                break;
            }
            case FORMAT_SS:
            {
                int s = calendar.get(Calendar.SECOND);
                if (s < 10)
                    pw.print("0");
                pw.print(s);
                break;
            }
            case FORMAT_TTTTT:
            {
                // Officially, we should use the system's time format. But
                // for now, we always print using the default format, h:mm:ss.
                format(FORMAT_H, calendar,pw);
                pw.print(locale.timeSeparator);
                format(FORMAT_NN, calendar,pw);
                pw.print(locale.timeSeparator);
                format(FORMAT_SS, calendar,pw);
                break;
            }
            case FORMAT_AMPM:
            case FORMAT_UPPER_AM_SOLIDUS_PM:
            {
                boolean isAm = calendar.get(Calendar.AM_PM) == Calendar.AM;
                pw.print(isAm ? "AM" : "PM");
                break;
            }
            case FORMAT_LOWER_AM_SOLIDUS_PM:
            {
                boolean isAm = calendar.get(Calendar.AM_PM) == Calendar.AM;
                pw.print(isAm ? "am" : "pm");
                break;
            }
            case FORMAT_UPPER_A_SOLIDUS_P:
            {
                boolean isAm = calendar.get(Calendar.AM_PM) == Calendar.AM;
                pw.print(isAm ? "A" : "P");
                break;
            }
            case FORMAT_LOWER_A_SOLIDUS_P:
            {
                boolean isAm = calendar.get(Calendar.AM_PM) == Calendar.AM;
                pw.print(isAm ? "a" : "p");
                break;
            }
            default:
                throw new Error();
            }
        }
    };

    /**
     * A FormatLocale contains all information necessary to format objects
     * based upon the locale of the end-user.  Use {@link Format#createLocale}
     * to make one.
     **/
    public static class FormatLocale
    {
        char thousandSeparator;
        char decimalPlaceholder;
        String dateSeparator;
        String timeSeparator;
        String currencySymbol;
        String currencyFormat;
        String[] daysOfWeekShort;
        String[] daysOfWeekLong;
        String[] monthsShort;
        String[] monthsLong;

        private FormatLocale(
            char thousandSeparator,
            char decimalPlaceholder,
            String dateSeparator,
            String timeSeparator,
            String currencySymbol,
            String currencyFormat,
            String[] daysOfWeekShort,
            String[] daysOfWeekLong,
            String[] monthsShort,
            String[] monthsLong)
        {
            if (thousandSeparator == '\0') {
                thousandSeparator = thousandSeparator_en;
            }
            this.thousandSeparator = thousandSeparator;
            if (decimalPlaceholder == '\0') {
                decimalPlaceholder = decimalPlaceholder_en;
            }
            this.decimalPlaceholder = decimalPlaceholder;
            if (dateSeparator == null) {
                dateSeparator = dateSeparator_en;
            }
            this.dateSeparator = dateSeparator;
            if (timeSeparator == null) {
                timeSeparator = timeSeparator_en;
            }
            this.timeSeparator = timeSeparator;
            if (currencySymbol == null) {
                currencySymbol = currencySymbol_en;
            }
            this.currencySymbol = currencySymbol;
            if (currencyFormat == null) {
                currencyFormat = currencyFormat_en;
            }
            this.currencyFormat = currencyFormat;
            if (daysOfWeekShort == null) {
                daysOfWeekShort = daysOfWeekShort_en;
            }
            this.daysOfWeekShort = daysOfWeekShort;
            if (daysOfWeekLong == null) {
                daysOfWeekLong = daysOfWeekLong_en;
            }
            this.daysOfWeekLong = daysOfWeekLong;
            if (monthsShort == null) {
                monthsShort = monthsShort_en;
            }
            this.monthsShort = monthsShort;
            if (monthsLong == null) {
                monthsLong = monthsLong_en;
            }
            this.monthsLong = monthsLong;
            if (daysOfWeekShort.length != 7 ||
                daysOfWeekLong.length != 7 ||
                monthsShort.length != 12 ||
                monthsLong.length != 12) {
                throw new IllegalArgumentException(
                    "Format: day or month array has incorrect length");
            }
        }

//          /**
//           * Get the localized string for day of week, given
//           * an <CODE>int</CODE> day value, with 0 = SUNDAY.
//           */
//          public static String getDayOfWeek( int day )
//          {
//              LocaleResource localeResource = FormatLocale.getResource();
//              switch ( day )
//              {
//              case 0: return localeResource.getsunday();
//              case 1: return localeResource.getmonday();
//              case 2: return localeResource.gettuesday();
//              case 3: return localeResource.getwednesday();
//              case 4: return localeResource.getthursday();
//              case 5: return localeResource.getfriday();
//              case 6: return localeResource.getsaturday();
//              default: throw new IllegalArgumentException();
//              }
//          }

//          /**
//           * Get the localized string for month of year, given
//           * an <CODE>int</CODE> month value, with 0 = JANUARY.
//           */
//          public static String getMonthOfYear( int month )
//          {
//              LocaleResource localeResource = FormatLocale.getResource();
//              switch ( month )
//              {
//              case 0: return localeResource.getjanuary();
//              case 1: return localeResource.getfebruary();
//              case 2: return localeResource.getmarch();
//              case 3: return localeResource.getapril();
//              case 4: return localeResource.getmay();
//              case 5: return localeResource.getjune();
//              case 6: return localeResource.getjuly();
//              case 7: return localeResource.getaugust();
//              case 8: return localeResource.getseptember();
//              case 9: return localeResource.getoctober();
//              case 10: return localeResource.getnovember();
//              case 11: return localeResource.getdecember();
//              default: throw new IllegalArgumentException();
//              }
//          }

//          /**
//           * Get the string representation of the calendar
//           * quarter for a given quarter and year.  Subclasses
//           * should override this method.
//           */
//          public static String getCalendarQuarter( int quarterIn, int yearIn )
//          {
//              Integer year = new Integer (yearIn % 100 );
//              Integer quarter = new Integer( quarterIn );

//              String strYear = ( year.intValue() < 10 )
//                  ? "0" + year.toString() : year.toString();
//              LocaleResource localeResource = FormatLocale.getResource();
//              String ret = localeResource.getcalendarQuarter(quarter.toString(), strYear);

//              return ret;
//          }

//          /**
//           * Get the string representation of the fiscal
//           * quarter for a given quarter and year.  Subclasses
//           * should override this method.
//           */
//          public static String getFiscalQuarter( int quarterIn, int yearIn )
//          {
//              Integer year = new Integer (yearIn % 100 );
//              Integer quarter = new Integer( quarterIn );

//              String strYear = ( year.intValue() < 10 )
//                  ? "0" + year.toString() : year.toString();

//              LocaleResource localeResource = FormatLocale.getResource();
//              String ret = localeResource.getfiscalQuarter(quarter.toString(),
//                                                            strYear);
//              return ret;
//          }

    };

    private static class StringFormat extends BasicFormat
    {
        int stringCase;

        StringFormat(int stringCase) {
            this.stringCase = stringCase;
        }
    };

    /** Values for {@link StringFormat#stringCase}. */
    private static final int CASE_ASIS = 0;
    private static final int CASE_UPPER = 1;
    private static final int CASE_LOWER = 2;

    /** Types of Format. */
    private static final int GENERAL = 0;
    private static final int DATE = 3;
    private static final int NUMERIC = 4;
    private static final int STRING = 5;
    /** A Format is flagged SPECIAL if it needs special processing
     * during parsing. */
    private static final int SPECIAL = 8;

    /** Values for {@link BasicFormat#code}. */
    private static final int FORMAT_NULL = 0;
    private static final int FORMAT_C = 3;
    private static final int FORMAT_D = 4;
    private static final int FORMAT_DD = 5;
    private static final int FORMAT_DDD = 6;
    private static final int FORMAT_DDDD = 7;
    private static final int FORMAT_DDDDD = 8;
    private static final int FORMAT_DDDDDD = 9;
    private static final int FORMAT_W = 10;
    private static final int FORMAT_WW = 11;
    private static final int FORMAT_M = 12;
    private static final int FORMAT_MM = 13;
    private static final int FORMAT_MMM_UPPER = 14;
    private static final int FORMAT_MMMM_UPPER = 15;
    private static final int FORMAT_Q = 16;
    private static final int FORMAT_Y = 17;
    private static final int FORMAT_YY = 18;
    private static final int FORMAT_YYYY = 19;
    private static final int FORMAT_H = 20;
    private static final int FORMAT_HH = 21;
    private static final int FORMAT_N = 22;
    private static final int FORMAT_NN = 23;
    private static final int FORMAT_S = 24;
    private static final int FORMAT_SS = 25;
    private static final int FORMAT_TTTTT = 26;
    private static final int FORMAT_UPPER_AM_SOLIDUS_PM = 27;
    private static final int FORMAT_LOWER_AM_SOLIDUS_PM = 28;
    private static final int FORMAT_UPPER_A_SOLIDUS_P = 29;
    private static final int FORMAT_LOWER_A_SOLIDUS_P = 30;
    private static final int FORMAT_AMPM = 31;
    private static final int FORMAT_0 = 32;
    private static final int FORMAT_POUND = 33;
    private static final int FORMAT_DECIMAL = 34;
    private static final int FORMAT_PERCENT = 35;
    private static final int FORMAT_THOUSEP = 36;
    private static final int FORMAT_TIMESEP = 37;
    private static final int FORMAT_DATESEP = 38;
    private static final int FORMAT_E_MINUS_UPPER = 39;
    private static final int FORMAT_E_PLUS_UPPER = 40;
    private static final int FORMAT_E_MINUS_LOWER = 41;
    private static final int FORMAT_E_PLUS_LOWER = 42;
    private static final int FORMAT_LITERAL = 43;
    private static final int FORMAT_BACKSLASH = 44;
    private static final int FORMAT_QUOTE = 45;
    private static final int FORMAT_CHARACTER_OR_SPACE = 46;
    private static final int FORMAT_CHARACTER_OR_NOTHING = 47;
    private static final int FORMAT_LOWER = 48;
    private static final int FORMAT_UPPER = 49;
    private static final int FORMAT_FILL_FROM_LEFT = 50;
    private static final int FORMAT_SEMI = 51;
    private static final int FORMAT_GENERAL_NUMBER = 52;
    private static final int FORMAT_GENERAL_DATE = 53;
    private static final int FORMAT_INTL_CURRENCY = 54;
    private static final int FORMAT_MMM_LOWER = 55;
    private static final int FORMAT_MMMM_LOWER = 56;
    private static final int FORMAT_USD = 57;

    private static final Token nfe(
        int code, int flags, String token, String purpose, String description)
    {
        Util.discard(purpose);
        Util.discard(description);
        return new Token(code,flags,token);
    }

    private static final Token[] tokens = {
        nfe(FORMAT_NULL                , NUMERIC, null, "No formatting", "Display the number with no formatting."),
        nfe(FORMAT_C                   , DATE, "C", null, "Display the date as ddddd and display the time as t t t t t, in that order. Display only date information if there is no fractional part to the date serial number; display only time information if there is no integer portion."),
        nfe(FORMAT_D                   , DATE, "d", null, "Display the day as a number without a leading zero (1 - 31)."),
        nfe(FORMAT_DD                  , DATE, "dd", null, "Display the day as a number with a leading zero (01 - 31)."),
        nfe(FORMAT_DDD                 , DATE, "Ddd", null, "Display the day as an abbreviation (Sun - Sat)."),
        nfe(FORMAT_DDDD                , DATE, "dddd", null, "Display the day as a full name (Sunday - Saturday)."),
        nfe(FORMAT_DDDDD               , DATE, "ddddd", null, "Display the date as a complete date (including day, month, and year), formatted according to your system's short date format setting. The default short date format is m/d/yy."),
        nfe(FORMAT_DDDDDD              , DATE, "dddddd", null, "Display a date serial number as a complete date (including day, month, and year) formatted according to the long date setting recognized by your system. The default long date format is mmmm dd, yyyy."),
        nfe(FORMAT_W                   , DATE, "w", null, "Display the day of the week as a number (1 for Sunday through 7 for Saturday)."),
        nfe(FORMAT_WW                  , DATE, "ww", null, "Display the week of the year as a number (1 - 53)."),
        nfe(FORMAT_M                   , DATE | SPECIAL, "m", null, "Display the month as a number without a leading zero (1 - 12). If m immediately follows h or hh, the minute rather than the month is displayed."),
        nfe(FORMAT_MM                  , DATE | SPECIAL, "mm", null, "Display the month as a number with a leading zero (01 - 12). If m immediately follows h or hh, the minute rather than the month is displayed."),
        nfe(FORMAT_MMM_LOWER           , DATE, "mmm", null, "Display the month as an abbreviation (Jan - Dec)."),
        nfe(FORMAT_MMMM_LOWER          , DATE, "mmmm", null, "Display the month as a full month name (January - December)."),
        nfe(FORMAT_MMM_UPPER           , DATE, "mmm", null, "Display the month as an abbreviation (Jan - Dec)."),
        nfe(FORMAT_MMMM_UPPER          , DATE, "mmmm", null, "Display the month as a full month name (January - December)."),
        nfe(FORMAT_Q                   , DATE, "q", null, "Display the quarter of the year as a number (1 - 4)."),
        nfe(FORMAT_Y                   , DATE, "y", null, "Display the day of the year as a number (1 - 366)."),
        nfe(FORMAT_YY                  , DATE, "yy", null, "Display the year as a 2-digit number (00 - 99)."),
        nfe(FORMAT_YYYY                , DATE, "yyyy", null, "Display the year as a 4-digit number (100 - 9999)."),
        nfe(FORMAT_H                   , DATE, "h", null, "Display the hour as a number without leading zeros (0 - 23)."),
        nfe(FORMAT_HH                  , DATE, "hh", null, "Display the hour as a number with leading zeros (00 - 23)."),
        nfe(FORMAT_N                   , DATE, "n", null, "Display the minute as a number without leading zeros (0 - 59)."),
        nfe(FORMAT_NN                  , DATE, "nn", null, "Display the minute as a number with leading zeros (00 - 59)."),
        nfe(FORMAT_S                   , DATE, "s", null, "Display the second as a number without leading zeros (0 - 59)."),
        nfe(FORMAT_SS                  , DATE, "ss", null, "Display the second as a number with leading zeros (00 - 59)."),
        nfe(FORMAT_TTTTT               , DATE, "ttttt", null, "Display a time as a complete time (including hour, minute, and second), formatted using the time separator defined by the time format recognized by your system. A leading zero is displayed if the leading zero option is selected and the time is before 10:00 A.M. or P.M. The default time format is h:mm:ss."),
        nfe(FORMAT_UPPER_AM_SOLIDUS_PM , DATE, "AM/PM", null, "Use the 12-hour clock and display an uppercase AM with any hour before noon; display an uppercase PM with any hour between noon and 11:59 P.M."),
        nfe(FORMAT_LOWER_AM_SOLIDUS_PM , DATE, "am/pm", null, "Use the 12-hour clock and display a lowercase AM with any hour before noon; display a lowercase PM with any hour between noon and 11:59 P.M."),
        nfe(FORMAT_UPPER_A_SOLIDUS_P   , DATE, "A/P", null, "Use the 12-hour clock and display an uppercase A with any hour before noon; display an uppercase P with any hour between noon and 11:59 P.M."),
        nfe(FORMAT_LOWER_A_SOLIDUS_P   , DATE, "a/p", null, "Use the 12-hour clock and display a lowercase A with any hour before noon; display a lowercase P with any hour between noon and 11:59 P.M."),
        nfe(FORMAT_AMPM                , DATE, "AMPM", null, "Use the 12-hour clock and display the AM string literal as defined by your system with any hour before noon; display the PM string literal as defined by your system with any hour between noon and 11:59 P.M. AMPM can be either uppercase or lowercase, but the case of the string displayed matches the string as defined by your system settings. The default format is AM/PM."),
        nfe(FORMAT_0                   , NUMERIC | SPECIAL, "0", "Digit placeholder", "Display a digit or a zero. If the expression has a digit in the position where the 0 appears in the format string, display it; otherwise, display a zero in that position. If the number has fewer digits than there are zeros (on either side of the decimal) in the format expression, display leading or trailing zeros. If the number has more digits to the right of the decimal separator than there are zeros to the right of the decimal separator in the format expression, round the number to as many decimal places as there are zeros. If the number has more digits to the left of the decimal separator than there are zeros to the left of the decimal separator in the format expression, display the extra digits without modification."),
        nfe(FORMAT_POUND               , NUMERIC | SPECIAL, "#", "Digit placeholder", "Display a digit or nothing. If the expression has a digit in the position where the # appears in the format string, display it; otherwise, display nothing in that position.  This symbol works like the 0 digit placeholder, except that leading and trailing zeros aren't displayed if the number has the same or fewer digits than there are # characters on either side of the decimal separator in the format expression."),
        nfe(FORMAT_DECIMAL             , NUMERIC | SPECIAL, ".", "Decimal placeholder", "In some locales, a comma is used as the decimal separator. The decimal placeholder determines how many digits are displayed to the left and right of the decimal separator. If the format expression contains only number signs to the left of this symbol, numbers smaller than 1 begin with a decimal separator. If you always want a leading zero displayed with fractional numbers, use 0 as the first digit placeholder to the left of the decimal separator instead. The actual character used as a decimal placeholder in the formatted output depends on the Number Format recognized by your system."),
        nfe(FORMAT_PERCENT             , NUMERIC, "%", "Percent placeholder", "The expression is multiplied by 100. The percent character (%) is inserted in the position where it appears in the format string."),
        nfe(FORMAT_THOUSEP             , NUMERIC | SPECIAL, ",", "Thousand separator", "In some locales, a period is used as a thousand separator. The thousand separator separates thousands from hundreds within a number that has four or more places to the left of the decimal separator. Standard use of the thousand separator is specified if the format contains a thousand separator surrounded by digit placeholders (0 or #). Two adjacent thousand separators or a thousand separator immediately to the left of the decimal separator (whether or not a decimal is specified) means \"scale the number by dividing it by 1000, rounding as needed.\"  You can scale large numbers using this technique. For example, you can use the format string \"##0,,\" to represent 100 million as 100. Numbers smaller than 1 million are displayed as 0. Two adjacent thousand separators in any position other than immediately to the left of the decimal separator are treated simply as specifying the use of a thousand separator. The actual character used as the thousand separator in the formatted output depends on the Number Format recognized by your system."),
        nfe(FORMAT_TIMESEP             , DATE | SPECIAL, ":", "Time separator", "In some locales, other characters may be used to represent the time separator. The time separator separates hours, minutes, and seconds when time values are formatted. The actual character used as the time separator in formatted output is determined by your system settings."),
        nfe(FORMAT_DATESEP             , DATE | SPECIAL, "/", "Date separator", "In some locales, other characters may be used to represent the date separator. The date separator separates the day, month, and year when date values are formatted. The actual character used as the date separator in formatted output is determined by your system settings."),
        nfe(FORMAT_E_MINUS_UPPER       , NUMERIC | SPECIAL, "E-", "Scientific format", "If the format expression contains at least one digit placeholder (0 or #) to the right of E-, E+, e-, or e+, the number is displayed in scientific format and E or e is inserted between the number and its exponent. The number of digit placeholders to the right determines the number of digits in the exponent. Use E- or e- to place a minus sign next to negative exponents. Use E+ or e+ to place a minus sign next to negative exponents and a plus sign next to positive exponents."),
        nfe(FORMAT_E_PLUS_UPPER        , NUMERIC | SPECIAL, "E+", "Scientific format", "See E-."),
        nfe(FORMAT_E_MINUS_LOWER       , NUMERIC | SPECIAL, "e-", "Scientific format", "See E-."),
        nfe(FORMAT_E_PLUS_LOWER        , NUMERIC | SPECIAL, "e+", "Scientific format", "See E-."),
        nfe(FORMAT_LITERAL             , GENERAL, "-", "Display a literal character", "To display a character other than one of those listed, precede it with a backslash (\\) or enclose it in double quotation marks (\" \")."),
        nfe(FORMAT_LITERAL             , GENERAL, "+", "Display a literal character", "See -."),
        nfe(FORMAT_LITERAL             , GENERAL, "$", "Display a literal character", "See -."),
        nfe(FORMAT_LITERAL             , GENERAL, "(", "Display a literal character", "See -."),
        nfe(FORMAT_LITERAL             , GENERAL, ")", "Display a literal character", "See -."),
        nfe(FORMAT_LITERAL             , GENERAL, " ", "Display a literal character", "See -."),
        nfe(FORMAT_BACKSLASH           , GENERAL | SPECIAL, "\\", "Display the next character in the format string", "Many characters in the format expression have a special meaning and can't be displayed as literal characters unless they are preceded by a backslash. The backslash itself isn't displayed. Using a backslash is the same as enclosing the next character in double quotation marks. To display a backslash, use two backslashes (\\).  Examples of characters that can't be displayed as literal characters are the date- and time-formatting characters (a, c, d, h, m, n, p, q, s, t, w, y, and /:), the numeric-formatting characters (#, 0, %, E, e, comma, and period), and the string-formatting characters (@, &, <, >, and !)."),
        nfe(FORMAT_QUOTE               , GENERAL | SPECIAL, "\"", "Display the string inside the double quotation marks", "To include a string in format from within code, you must use Chr(34) to enclose the text (34 is the character code for a double quotation mark)."),
        nfe(FORMAT_CHARACTER_OR_SPACE  , STRING, "@", "Character placeholder", "Display a character or a space. If the string has a character in the position where the @ appears in the format string, display it; otherwise, display a space in that position. Placeholders are filled from right to left unless there is an ! character in the format string. See below."),
        nfe(FORMAT_CHARACTER_OR_NOTHING, STRING, "&", "Character placeholder", "Display a character or nothing. If the string has a character in the position where the & appears, display it; otherwise, display nothing. Placeholders are filled from right to left unless there is an ! character in the format string. See below."),
        nfe(FORMAT_LOWER               , STRING | SPECIAL, "<", "Force lowercase", "Display all characters in lowercase format."),
        nfe(FORMAT_UPPER               , STRING | SPECIAL, ">", "Force uppercase", "Display all characters in uppercase format."),
        nfe(FORMAT_FILL_FROM_LEFT      , STRING | SPECIAL, "!", "Force left to right fill of placeholders", "The default is to fill from right to left."),
        nfe(FORMAT_SEMI                , GENERAL | SPECIAL, ";", "Separates format strings for different kinds of values", "If there is one section, the format expression applies to all values. If there are two sections, the first section applies to positive values and zeros, the second to negative values. If there are three sections, the first section applies to positive values, the second to negative values, and the third to zeros. If there are four sections, the first section applies to positive values, the second to negative values, the third to zeros, and the fourth to Null values."),
        nfe(FORMAT_INTL_CURRENCY       , NUMERIC | SPECIAL, intlCurrencySymbol + "", null, "Display the locale's currency symbol."),
        nfe(FORMAT_USD                 , GENERAL, "USD", null, "Display USD (U.S. Dollars)."),
        nfe(FORMAT_GENERAL_NUMBER      , NUMERIC | SPECIAL, "General Number", null, "Shows numbers as entered."),
        nfe(FORMAT_GENERAL_DATE        , DATE | SPECIAL, "General Date", null, "Shows date and time if expression contains both. If expression is only a date or a time, the missing information is not displayed."),
    };

    static class MacroToken {
        String name;
        String translation;
        String description;

        MacroToken(String name, String translation, String description)
        {
            this.name = name;
            this.translation = translation;
            this.description = description;
        }
    };

    // Named formats.  todo: Supply the translation strings.
    private static final MacroToken[] macroTokens = {
        new MacroToken(
            "Currency", null, "Shows currency values according to the locale's CurrencyFormat.  Negative numbers are inside parentheses."),
        new MacroToken(
            "Fixed", "0", "Shows at least one digit."),
        new MacroToken(
            "Standard", "#,##0", "Uses a thousands separator."),
        new MacroToken(
            "Percent", "0.00%", "Multiplies the value by 100 with a percent sign at the end."),
        new MacroToken(
            "Scientific", "0.00e+00", "Uses standard scientific notation."),
        new MacroToken(
            "Long Date", "dddd, mmmm dd, yyyy", "Uses the Long Date format specified in the Regional Settings dialog box of the Microsoft Windows Control Panel."),
        new MacroToken(
            "Medium Date", "dd-mmm-yy", "Uses the dd-mmm-yy format (for example, 03-Apr-93)"),
        new MacroToken(
            "Short Date", "m/d/yy", "Uses the Short Date format specified in the Regional Settings dialog box of the Windows Control Panel."),
        new MacroToken(
            "Long Time", "h:mm:ss AM/PM", "Shows the hour, minute, second, and \"AM\" or \"PM\" using the h:mm:ss format."),
        new MacroToken(
            "Medium Time", "h:mm AM/PM", "Shows the hour, minute, and \"AM\" or \"PM\" using the \"hh:mm AM/PM\" format."),
        new MacroToken(
            "Short Time", "hh:mm", "Shows the hour and minute using the hh:mm format."),
        new MacroToken(
            "Yes/No", "\\Y\\e\\s;\\Y\\e\\s;\\N\\o;\\N\\o", "Any nonzero numeric value (usually - 1) is Yes. Zero is No."),
        new MacroToken(
            "True/False", "\\T\\r\\u\\e;\\T\\r\\u\\e;\\F\\a\\l\\s\\e;\\F\\a\\l\\s\\e", "Any nonzero numeric value (usually - 1) is True. Zero is False."),
        new MacroToken(
            "On/Off", "\\O\\n;\\O\\n;\\O\\f\\f;\\O\\f\\f", "Any nonzero numeric value (usually - 1) is On. Zero is Off."),

    };

    /**
     * Constructs a <code>Format</code> in the default locale.
     *
     * @deprecated use {@link Format#Format(String, Format.FormatLocale)} for
     *   locale-specific behavior.
     */
    public Format(String formatString)
    {
        this(formatString, getBestFormatLocale(null));
    }

    /**
     * Constructs a <code>Format</code> in a specific locale.
     *
     * @param formatString the format string; see
     *   <a href="http://www.apostate.com/programming/vb-format.html">this
     *   description</a> for more details
     * @param locale The locale
     **/
    public Format(String formatString, Locale locale)
    {
        this(formatString, getBestFormatLocale(locale));
    }

    /**
     * Constructs a <code>Format</code> in a specific locale.
     *
     * @see FormatLocale
     * @see #createLocale
     */
    public Format(String formatString, FormatLocale locale)
    {
        if (formatString == null) {
            formatString = "";
        }
        this.formatString = formatString;
        if (locale == null) {
            locale = locale_US;
        }
        this.locale = locale;

        ArrayList alternateFormatList = new ArrayList();
        while (formatString.length() > 0) {
            formatString = parseFormatString(
                formatString, alternateFormatList);
        }

        BasicFormat[] alternateFormats = (BasicFormat[])
            alternateFormatList.toArray(new BasicFormat[alternateFormatList.size()]);
        if (alternateFormats.length == 0) {
            format = new JavaFormat();
        } else {
            if (alternateFormats[0] == null) {
                // Later entries in the formats list default to the first (e.g.
                // "#.00;;Nil"), but the first entry must be set.
                alternateFormats[0] = new JavaFormat();
            }
            format = new AlternateFormat(alternateFormats);
        }
    }

    /**
     * Constructs a <code>Format</code> in a specific locale, or retrieves
     * one from the cache if one already exists.
     *
     *   * @param formatString the format string; see
     *   <a href="http://www.apostate.com/programming/vb-format.html">this
     *   description</a> for more details

     */
    public static Format get(String formatString, Locale locale) {
        String key = formatString + "@@@" + locale;
        Format format = (Format) cache.get(key);
        if (format == null) {
            synchronized (cache) {
                format = (Format) cache.get(key);
                if (format == null) {
                    format = new Format(formatString, locale);
                    cache.put(key, format);
                    Util.assertTrue(
                        cache.size() < 100,
                        "todo: implement format cache flushing");
                }
            }
        }
        return format;
    }

    /**
     * Create a {@link FormatLocale} object characterized by the given
     * proeprties.
     *
     * @param thousandSeparator the character used to separate thousands in
     *   numbers, or ',' by default.  For example, 12345 is '12,345 in English,
     *   '12.345 in French.
     * @param decimalPlaceholder the character placed between the integer and
     *   the fractional part of decimal numbers, or '.' by default.  For
     *   example, 12.34 is '12.34' in English, '12,34' in French.
     * @param dateSeparator the character placed between the year, month and
     *   day of a date such as '12/07/2001', or '/' by default.
     * @param timeSeparator the character placed between the hour, minute and
     *   second value of a time such as '1:23:45 AM', or ':' by default.
     * @param daysOfWeekShort for example {"Mon", ..., "Sunday"}.
     * @param daysOfWeekLong for example {"Monday", ..., "Sunday"}.
     * @param monthsShort for example {"Jan", ..., "Dec"}.
     * @param monthsLong for example {"January", ..., "December"}.
     * @param locale if this is not null, register that the constructed
     *     <code>FormatLocale</code> is the default for <code>locale</code>
     */
    public static FormatLocale createLocale(
        char thousandSeparator,
        char decimalPlaceholder,
        String dateSeparator,
        String timeSeparator,
        String currencySymbol,
        String currencyFormat,
        String[] daysOfWeekShort,
        String[] daysOfWeekLong,
        String[] monthsShort,
        String[] monthsLong,
        Locale locale)
    {
        FormatLocale formatLocale = new FormatLocale(
            thousandSeparator, decimalPlaceholder, dateSeparator,
            timeSeparator, currencySymbol, currencyFormat, daysOfWeekShort,
            daysOfWeekLong, monthsShort, monthsLong);
        if (locale != null) {
            registerFormatLocale(formatLocale, locale);
        }
        return formatLocale;
    }

    /**
     * Returns the {@link FormatLocale} which precisely matches {@link Locale},
     * if any, or null if there is none.
     **/
    public static FormatLocale getFormatLocale(Locale locale)
    {
        if (locale == null) {
            locale = Locale.US;
        }
        String key = locale.toString();
        return (FormatLocale) mapLocaleToFormatLocale.get(key);
    }

    /**
     * Returns the best {@link FormatLocale} for a given {@link Locale}.
     * Never returns null, even if <code>locale</code> is null.
     */
    public static synchronized FormatLocale getBestFormatLocale(Locale locale)
    {
        FormatLocale formatLocale;
        if (locale == null) {
            return locale_US;
        }
        String key = locale.toString();
        // Look in the cache first.
        formatLocale = (FormatLocale) mapLocaleToFormatLocale.get(key);
        if (formatLocale == null) {
            // Not in the cache, so ask the factory.
            formatLocale = getFormatLocaleUsingFactory(locale);
            if (formatLocale == null) {
                formatLocale = locale_US;
            }
            // Add to cache.
            mapLocaleToFormatLocale.put(key, formatLocale);
        }
        return formatLocale;
    }

    private static FormatLocale getFormatLocaleUsingFactory(Locale locale)
    {
        LocaleFormatFactory factory = getLocaleFormatFactory();
        if (factory == null) {
            return null;
        }
        FormatLocale formatLocale;
        // Lookup full locale, e.g. "en-US-Boston"
        if (!locale.getVariant().equals("")) {
            formatLocale = factory.get(locale);
            if (formatLocale != null) {
                return formatLocale;
            }
            locale = new Locale(locale.getLanguage(), locale.getCountry());
        }
        // Lookup language and country, e.g. "en-US"
        if (!locale.getCountry().equals("")) {
            formatLocale = factory.get(locale);
            if (formatLocale != null) {
                return formatLocale;
            }
            locale = new Locale(locale.getLanguage());
        }
        // Lookup language, e.g. "en"
        formatLocale = factory.get(locale);
        if (formatLocale != null) {
            return formatLocale;
        }
        return null;
    }

    /**
     * Registers a {@link FormatLocale} to a given {@link Locale}. Returns the
     * previous mapping.
     **/
    public static FormatLocale registerFormatLocale(
        FormatLocale formatLocale, Locale locale)
    {
        String key = locale.toString(); // e.g. "en_us_Boston"
        FormatLocale previous = (FormatLocale) mapLocaleToFormatLocale.put(
            key, formatLocale);
        key = locale.getLanguage() + "_" + locale.getCountry(); // e.g. "en_us"
        if (mapLocaleToFormatLocale.get(key) == null) {
            mapLocaleToFormatLocale.put(key, formatLocale);
        }
        key = locale.getLanguage(); // e.g. "en"
        if (mapLocaleToFormatLocale.get(key) == null) {
            mapLocaleToFormatLocale.put(key, formatLocale);
        }
        key = ""; // special key for the 'default' locale
        if (mapLocaleToFormatLocale.get(key) == null) {
            mapLocaleToFormatLocale.put(key, formatLocale);
        }
        return previous;
    }

    // Values for variable numberState below.
    static final int NOT_IN_A_NUMBER = 0;
    static final int LEFT_OF_POINT = 1;
    static final int RIGHT_OF_POINT = 2;
    static final int RIGHT_OF_EXP = 3;

    /**
     * Reads formatString up to the first semi-colon, or to the end if there
     * are no semi-colons.  Adds a format to alternateFormatList, and returns
     * the remains of formatString.
     */
    private String parseFormatString(
        String formatString, ArrayList alternateFormatList)
    {
        // Where we are in a numeric format.
        int numberState = NOT_IN_A_NUMBER;
        String ignored = "", prevIgnored = null;
        boolean haveSeenNumber = false;
        int digitsLeftOfPoint = 0,
            digitsRightOfPoint = 0,
            digitsRightOfExp = 0,
            zeroesLeftOfPoint = 0,
            zeroesRightOfPoint = 0,
            zeroesRightOfExp = 0;
        int stringCase = CASE_ASIS;
        boolean useDecimal = false,
                useThouSep = false,
                fillFromRight = true;

        /** Whether to print numbers in decimal or exponential format.  Valid
         * values are FORMAT_NULL, FORMAT_E_PLUS_LOWER, FORMAT_E_MINUS_LOWER,
         * FORMAT_E_PLUS_UPPER, FORMAT_E_MINUS_UPPER. */
        int expFormat = FORMAT_NULL;

        // todo: Parse the string for ;s

        // Look for the format string in the table of named formats.
        for (int i = 0; i < macroTokens.length; i++) {
            if (formatString.equals(macroTokens[i].name)) {
                if (macroTokens[i].translation == null) {
                    // this macro requires special-case code
                    if (macroTokens[i].name.equals("Currency")) {
                        // e.g. "$#,##0.00;($#,##0.00)"
                        formatString = locale.currencyFormat
                            + ";(" +locale.currencyFormat + ")";
                    } else {
                        throw new Error(
                            "Format: internal: token " + macroTokens[i].name +
                            " should have translation");
                    }
                } else {
                    formatString = macroTokens[i].translation;
                }
                break;
            }
        }

        // Add a semi-colon to the end of the string so the end of the string
        // looks like the end of an alternate.
        if (!formatString.endsWith(";")) {
            formatString = formatString + ";";
        }

        // Scan through the format string for format elements.
        ArrayList formatList = new ArrayList();
loop:
        while (formatString.length() > 0) {
            BasicFormat format = null;
            String newFormatString = null;
            boolean ignoreToken = false;
            for (int i = tokens.length - 1; i > 0; i--) {
                Token token = tokens[i];
                if (formatString.startsWith(token.token)) {
                    // Derive the string we will be looking at next time
                    // around, by chewing the token off the front of the
                    // string.  Special-case code below can change this string,
                    // if it likes.
                    String matched = token.token;
                    newFormatString = formatString.substring(matched.length());
                    if (token.isSpecial()) {
                        switch (token.code) {
                        case FORMAT_SEMI:
                            formatString = newFormatString;
                            break loop;

                        case FORMAT_POUND:
                            switch (numberState) {
                            case NOT_IN_A_NUMBER:
                                numberState = LEFT_OF_POINT;
                                // fall through
                            case LEFT_OF_POINT:
                                digitsLeftOfPoint++;
                                break;
                            case RIGHT_OF_POINT:
                                digitsRightOfPoint++;
                                break;
                            case RIGHT_OF_EXP:
                                digitsRightOfExp++;
                                break;
                            default:
                                throw new Error();
                            }
                            break;

                        case FORMAT_0:
                            switch (numberState) {
                            case NOT_IN_A_NUMBER:
                                numberState = LEFT_OF_POINT;
                                // fall through
                            case LEFT_OF_POINT:
                                zeroesLeftOfPoint++;
                                break;
                            case RIGHT_OF_POINT:
                                zeroesRightOfPoint++;
                                break;
                            case RIGHT_OF_EXP:
                                zeroesRightOfExp++;
                                break;
                            default:
                                throw new Error();
                            }
                            break;

                        case FORMAT_M:
                        case FORMAT_MM:
                        {
                            // "m" and "mm" mean minute if immediately after
                            // "h" or "hh"; month otherwise.
                            boolean theyMeantMinute = false;
                            int j = formatList.size() - 1;
                            while (j >= 0) {
                                BasicFormat prevFormat = (BasicFormat)
                                    formatList.get(j);
                                if (prevFormat instanceof LiteralFormat) {
                                    // ignore boilerplate
                                    j--;
                                } else if (prevFormat.code == FORMAT_H ||
                                           prevFormat.code == FORMAT_HH) {
                                    theyMeantMinute = true;
                                    break;
                                } else {
                                    theyMeantMinute = false;
                                    break;
                                }
                            }
                            if (theyMeantMinute) {
                                format = new DateFormat(
                                    (token.code == FORMAT_M
                                     ? FORMAT_N
                                     : FORMAT_NN),
                                    matched,
                                    locale,
                                    false);
                            } else {
                                format = token.makeFormat(locale);
                            }
                            break;
                        }

                        case FORMAT_DECIMAL:
                        {
                            numberState = RIGHT_OF_POINT;
                            useDecimal = true;
                            break;
                        }

                        case FORMAT_THOUSEP:
                        {
                            if (numberState == LEFT_OF_POINT) {
                                // e.g. "#,##"
                                useThouSep = true;
                            } else {
                                // e.g. "ddd, mmm dd, yyy"
                                format = token.makeFormat(locale);
                            }
                            break;
                        }

                        case FORMAT_TIMESEP:
                        {
                            format = new LiteralFormat(locale.timeSeparator);
                            break;
                        }

                        case FORMAT_DATESEP:
                        {
                            format = new LiteralFormat(locale.dateSeparator);
                            break;
                        }

                        case FORMAT_BACKSLASH:
                        {
                            // Display the next character in the format string.
                            String s = "";
                            if (formatString.length() == 1) {
                                // Backslash is the last character in the
                                // string.
                                s = "";
                                newFormatString = "";
                            } else {
                                s = formatString.substring(1,2);
                                newFormatString = formatString.substring(2);
                            }
                            format = new LiteralFormat(s);
                            break;
                        }

                        case FORMAT_E_MINUS_UPPER:
                        case FORMAT_E_PLUS_UPPER:
                        case FORMAT_E_MINUS_LOWER:
                        case FORMAT_E_PLUS_LOWER:
                        {
                            numberState = RIGHT_OF_EXP;
                            expFormat = token.code;
                            if (zeroesLeftOfPoint == 0 &&
                                zeroesRightOfPoint == 0) {
                                // We need a mantissa, so that format(123.45,
                                // "E+") gives "1E+2", not "0E+2" or "E+2".
                                zeroesLeftOfPoint = 1;
                            }
                            break;
                        }

                        case FORMAT_QUOTE:
                        {
                            // Display the string inside the double quotation
                            // marks.
                            String s;
                            int j = formatString.indexOf("\"", 1);
                            if (j == -1) {
                                // The string did not contain a closing quote.
                                // Use the whole string.
                                s = formatString.substring(1);
                                newFormatString = "";
                            } else {
                                // Take the string inside the quotes.
                                s = formatString.substring(1, j);
                                newFormatString = formatString.substring(
                                    j + 1);
                            }
                            format = new LiteralFormat(s);
                            break;
                        }

                        case FORMAT_UPPER:
                        {
                            stringCase = CASE_UPPER;
                            break;
                        }

                        case FORMAT_LOWER:
                        {
                            stringCase = CASE_LOWER;
                            break;
                        }

                        case FORMAT_FILL_FROM_LEFT:
                        {
                            fillFromRight = false;
                            break;
                        }

                        case FORMAT_GENERAL_NUMBER:
                        {
                            format = new JavaFormat();
                            break;
                        }

                        case FORMAT_GENERAL_DATE:
                        {
                            format = new JavaFormat();
                            break;
                        }

                        case FORMAT_INTL_CURRENCY:
                        {
                            format = new LiteralFormat(locale.currencySymbol);
                            break;
                        }

                        default:
                            throw new Error();
                        }
                        if (format == null) {
                            // If the special-case code does not set format,
                            // we should not create a format element.  (The
                            // token probably caused some flag to be set.)
                            ignoreToken = true;
                            ignored += matched;
                        } else {
                            prevIgnored = ignored;
                            ignored = "";
                        }
                    } else {
                        format = token.makeFormat(locale);
                    }
                    break;
                }
            }

            if (format == null && !ignoreToken) {
                // None of the standard format elements matched.  Make the
                // current character into a literal.
                format = new LiteralFormat(
                    formatString.substring(0,1));
                newFormatString = formatString.substring(1);
            }

            if (format != null) {
                if (numberState != NOT_IN_A_NUMBER) {
                    // Having seen a few number tokens, we're looking at a
                    // non-number token.  Create the number first.
                    NumericFormat numericFormat = new NumericFormat(
                        prevIgnored, locale, expFormat, digitsLeftOfPoint,
                        zeroesLeftOfPoint, digitsRightOfPoint,
                        zeroesRightOfPoint, digitsRightOfExp, zeroesRightOfExp,
                        useDecimal, useThouSep);
                    formatList.add(numericFormat);
                    numberState = NOT_IN_A_NUMBER;
                    haveSeenNumber = true;
                }

                formatList.add(format);
            }

            formatString = newFormatString;
        }

        if (numberState != NOT_IN_A_NUMBER) {
            // We're still in a number.  Create a number format.
            NumericFormat numericFormat = new NumericFormat(
                prevIgnored, locale, expFormat, digitsLeftOfPoint,
                zeroesLeftOfPoint, digitsRightOfPoint, zeroesRightOfPoint,
                digitsRightOfExp, zeroesRightOfExp, useDecimal, useThouSep);
            formatList.add(numericFormat);
            numberState = NOT_IN_A_NUMBER;
            haveSeenNumber = true;
        }

        // The is the end of an alternate - or of the whole format string.
        // Push the current list of formats onto the list of alternates.
        BasicFormat[] formats = (BasicFormat[])
            formatList.toArray(new BasicFormat[formatList.size()]);

        // If they used some symbol like 'AM/PM' in the format string, tell all
        // date formats to use twelve hour clock.  Likewise, figure out the
        // multiplier implied by their use of "%" or ",".
        boolean twelveHourClock = false;
        int decimalShift = 0;
        for (int i = 0; i < formats.length; i++) {
            switch (formats[i].code) {
            case FORMAT_UPPER_AM_SOLIDUS_PM:
            case FORMAT_LOWER_AM_SOLIDUS_PM:
            case FORMAT_UPPER_A_SOLIDUS_P:
            case FORMAT_LOWER_A_SOLIDUS_P:
            case FORMAT_AMPM:
                twelveHourClock = true;
                break;

            case FORMAT_PERCENT:
                // If "%" occurs, the number should be multiplied by 100.
                decimalShift += 2;
                break;

            case FORMAT_THOUSEP:
                // If there is a thousands separator (",") immediately to the
                // left of the point, or at the end of the number, divide the
                // number by 1000.  (Or by 1000^n if there are more than one.)
                if (haveSeenNumber &&
                    i + 1 < formats.length &&
                    formats[i + 1].code != FORMAT_THOUSEP &&
                    formats[i + 1].code != FORMAT_0 &&
                    formats[i + 1].code != FORMAT_POUND) {
                    for (int j = i;
                         j >= 0 && formats[j].code == FORMAT_THOUSEP;
                         j--) {
                        decimalShift -= 3;
                        formats[j] = new LiteralFormat(""); // ignore
                    }
                }
                break;

            default:
            }
        }

        if (twelveHourClock) {
            for (int i = 0; i < formats.length; i++) {
                if (formats[i] instanceof DateFormat) {
                    ((DateFormat) formats[i]).setTwelveHourClock(true);
                }
            }
        }

        if (decimalShift != 0) {
            for (int i = 0; i < formats.length; i++) {
                if (formats[i] instanceof NumericFormat) {
                    ((NumericFormat) formats[i]).decimalShift = decimalShift;
                }
            }
        }

        // Create a CompoundFormat containing all of the format elements.
        BasicFormat alternateFormat =
            formats.length > 0
            ? new CompoundFormat(formats)
            : null;
        alternateFormatList.add(alternateFormat);
        return formatString;
    }

    public String format(Object o)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        format.format(o, pw);
        pw.flush();
        return sw.toString();
    }

    public String getFormatString()
    {
        return formatString;
    }

    /**
     * Defines the factory used to create a {@link FormatLocale} for locales.
     *
     * <p>Also clears the cache, so that locales from the previous factory (if
     * any) are no longer used.
     */
    public static void setLocaleFormatFactory(LocaleFormatFactory factory) {
        localeFormatFactory = factory;
        mapLocaleToFormatLocale.clear(); // clear cache
    }

    /**
     * Returns the factory used to create a {@link FormatLocale} for locales.
     */
    public static LocaleFormatFactory getLocaleFormatFactory() {
        return localeFormatFactory;
    }

    /**
     * Locates a {@link FormatLocale} for a given locale.
     */
    public interface LocaleFormatFactory {
        FormatLocale get(Locale locale);
    }

/**
 * Copied from <code>java.lang.FloatingDecimal</code>.
 */
static class FloatingDecimal {
    boolean     isExceptional;
    boolean     isNegative;
    int         decExponent;
    char        digits[];
    int         nDigits;

    /*
     * Constants of the implementation
     * Most are IEEE-754 related.
     * (There are more really boring constants at the end.)
     */
    static final long   signMask = 0x8000000000000000L;
    static final long   expMask  = 0x7ff0000000000000L;
    static final long   fractMask= ~(signMask|expMask);
    static final int    expShift = 52;
    static final int    expBias  = 1023;
    static final long   fractHOB = ( 1L<<expShift ); // assumed High-Order bit
    static final long   expOne   = ((long)expBias)<<expShift; // exponent of 1.0
    static final int    maxSmallBinExp = 62;
    static final int    minSmallBinExp = -( 63 / 3 );

    static final long   highbyte = 0xff00000000000000L;
    static final long   highbit  = 0x8000000000000000L;
    static final long   lowbytes = ~highbyte;

    static final int    singleSignMask =    0x80000000;
    static final int    singleExpMask  =    0x7f800000;
    static final int    singleFractMask =   ~(singleSignMask|singleExpMask);
    static final int    singleExpShift  =   23;
    static final int    singleFractHOB  =   1<<singleExpShift;
    static final int    singleExpBias   =   127;

    /*
     * count number of bits from high-order 1 bit to low-order 1 bit,
     * inclusive.
     */
    private static int
    countBits( long v ){
        //
        // the strategy is to shift until we get a non-zero sign bit
        // then shift until we have no bits left, counting the difference.
        // we do byte shifting as a hack. Hope it helps.
        //
        if ( v == 0L ) return 0;

        while ( ( v & highbyte ) == 0L ){
            v <<= 8;
        }
        while ( v > 0L ) { // i.e. while ((v&highbit) == 0L )
            v <<= 1;
        }

        int n = 0;
        while (( v & lowbytes ) != 0L ){
            v <<= 8;
            n += 8;
        }
        while ( v != 0L ){
            v <<= 1;
            n += 1;
        }
        return n;
    }

    /*
     * Keep big powers of 5 handy for future reference.
     */
    private static FDBigInt b5p[];

    private static FDBigInt
    big5pow( int p ){
        if ( p < 0 )
            throw new RuntimeException( "Assertion botch: negative power of 5");
        if ( b5p == null ){
            b5p = new FDBigInt[ p+1 ];
        }else if (b5p.length <= p ){
            FDBigInt t[] = new FDBigInt[ p+1 ];
            System.arraycopy( b5p, 0, t, 0, b5p.length );
            b5p = t;
        }
        if ( b5p[p] != null )
            return b5p[p];
        else if ( p < small5pow.length )
            return b5p[p] = new FDBigInt( small5pow[p] );
        else if ( p < long5pow.length )
            return b5p[p] = new FDBigInt( long5pow[p] );
        else {
            // construct the damn thing.
            // recursively.
            int q, r;
            // in order to compute 5^p,
            // compute its square root, 5^(p/2) and square.
            // or, let q = p / 2, r = p -q, then
            // 5^p = 5^(q+r) = 5^q * 5^r
            q = p >> 1;
            r = p - q;
            FDBigInt bigq =  b5p[q];
            if ( bigq == null )
                bigq = big5pow ( q );
            if ( r < small5pow.length ){
                return (b5p[p] = bigq.mult( small5pow[r] ) );
            }else{
                FDBigInt bigr = b5p[ r ];
                if ( bigr == null )
                    bigr = big5pow( r );
                return (b5p[p] = bigq.mult( bigr ) );
            }
        }
    }

    /*
     * This is the easy subcase --
     * all the significant bits, after scaling, are held in lvalue.
     * negSign and decExponent tell us what processing and scaling
     * has already been done. Exceptional cases have already been
     * stripped out.
     * In particular:
     * lvalue is a finite number (not Inf, nor NaN)
     * lvalue > 0L (not zero, nor negative).
     *
     * The only reason that we develop the digits here, rather than
     * calling on Long.toString() is that we can do it a little faster,
     * and besides want to treat trailing 0s specially. If Long.toString
     * changes, we should re-evaluate this strategy!
     */
    private void
    developLongDigits( int decExponent, long lvalue, long insignificant ){
        char digits[];
        int  ndigits;
        int  digitno;
        int  c;
        //
        // Discard non-significant low-order bits, while rounding,
        // up to insignificant value.
        int i;
        for ( i = 0; insignificant >= 10L; i++ )
            insignificant /= 10L;
        if ( i != 0 ){
            long pow10 = long5pow[i] << i; // 10^i == 5^i * 2^i;
            long residue = lvalue % pow10;
            lvalue /= pow10;
            decExponent += i;
            if ( residue >= (pow10>>1) ){
                // round up based on the low-order bits we're discarding
                lvalue++;
            }
        }
        if ( lvalue <= Integer.MAX_VALUE ){
            if ( lvalue <= 0L )
                throw new RuntimeException("Assertion botch: value "+lvalue+" <= 0");

            // even easier subcase!
            // can do int arithmetic rather than long!
            int  ivalue = (int)lvalue;
            digits = new char[ ndigits=10 ];
            digitno = ndigits-1;
            c = ivalue%10;
            ivalue /= 10;
            while ( c == 0 ){
                decExponent++;
                c = ivalue%10;
                ivalue /= 10;
            }
            while ( ivalue != 0){
                digits[digitno--] = (char)(c+'0');
                decExponent++;
                c = ivalue%10;
                ivalue /= 10;
            }
            digits[digitno] = (char)(c+'0');
        } else {
            // same algorithm as above (same bugs, too )
            // but using long arithmetic.
            digits = new char[ ndigits=20 ];
            digitno = ndigits-1;
            c = (int)(lvalue%10L);
            lvalue /= 10L;
            while ( c == 0 ){
                decExponent++;
                c = (int)(lvalue%10L);
                lvalue /= 10L;
            }
            while ( lvalue != 0L ){
                digits[digitno--] = (char)(c+'0');
                decExponent++;
                c = (int)(lvalue%10L);
                lvalue /= 10;
            }
            digits[digitno] = (char)(c+'0');
        }
        char result [];
        ndigits -= digitno;
        if ( digitno == 0 )
            result = digits;
        else {
            result = new char[ ndigits ];
            System.arraycopy( digits, digitno, result, 0, ndigits );
        }
        this.digits = result;
        this.decExponent = decExponent+1;
        this.nDigits = ndigits;
    }

    //
    // add one to the least significant digit.
    // in the unlikely event there is a carry out,
    // deal with it.
    // assert that this will only happen where there
    // is only one digit, e.g. (float)1e-44 seems to do it.
    //
    private void
    roundup(){
        int i;
        int q = digits[ i = (nDigits-1)];
        if ( q == '9' ){
            while ( q == '9' && i > 0 ){
                digits[i] = '0';
                q = digits[--i];
            }
            if ( q == '9' ){
                // carryout! High-order 1, rest 0s, larger exp.
                decExponent += 1;
                digits[0] = '1';
                return;
            }
            // else fall through.
        }
        digits[i] = (char)(q+1);
    }

    /*
     * FIRST IMPORTANT CONSTRUCTOR: DOUBLE
     */
    public FloatingDecimal( double d )
    {
        long    dBits = Double.doubleToLongBits( d );
        long    fractBits;
        int     binExp;
        int     nSignificantBits;

        // discover and delete sign
        if ( (dBits&signMask) != 0 ){
            isNegative = true;
            dBits ^= signMask;
        } else {
            isNegative = false;
        }
        // Begin to unpack
        // Discover obvious special cases of NaN and Infinity.
        binExp = (int)( (dBits&expMask) >> expShift );
        fractBits = dBits&fractMask;
        if ( binExp == (int)(expMask>>expShift) ) {
            isExceptional = true;
            if ( fractBits == 0L ){
                digits =  infinity;
            } else {
                digits = notANumber;
                isNegative = false; // NaN has no sign!
            }
            nDigits = digits.length;
            return;
        }
        isExceptional = false;
        // Finish unpacking
        // Normalize denormalized numbers.
        // Insert assumed high-order bit for normalized numbers.
        // Subtract exponent bias.
        if ( binExp == 0 ){
            if ( fractBits == 0L ){
                // not a denorm, just a 0!
                decExponent = 0;
                digits = zero;
                nDigits = 1;
                return;
            }
            while ( (fractBits&fractHOB) == 0L ){
                fractBits <<= 1;
                binExp -= 1;
            }
            nSignificantBits = expShift + binExp; // recall binExp is  - shift count.
            binExp += 1;
        } else {
            fractBits |= fractHOB;
            nSignificantBits = expShift+1;
        }
        binExp -= expBias;
        // call the routine that actually does all the hard work.
        dtoa( binExp, fractBits, nSignificantBits );
    }

    /*
     * SECOND IMPORTANT CONSTRUCTOR: SINGLE
     */
    public FloatingDecimal( float f )
    {
        int     fBits = Float.floatToIntBits( f );
        int     fractBits;
        int     binExp;
        int     nSignificantBits;

        // discover and delete sign
        if ( (fBits&singleSignMask) != 0 ){
            isNegative = true;
            fBits ^= singleSignMask;
        } else {
            isNegative = false;
        }
        // Begin to unpack
        // Discover obvious special cases of NaN and Infinity.
        binExp = ( (fBits&singleExpMask) >> singleExpShift );
        fractBits = fBits&singleFractMask;
        if ( binExp == (singleExpMask>>singleExpShift) ) {
            isExceptional = true;
            if ( fractBits == 0L ){
                digits =  infinity;
            } else {
                digits = notANumber;
                isNegative = false; // NaN has no sign!
            }
            nDigits = digits.length;
            return;
        }
        isExceptional = false;
        // Finish unpacking
        // Normalize denormalized numbers.
        // Insert assumed high-order bit for normalized numbers.
        // Subtract exponent bias.
        if ( binExp == 0 ){
            if ( fractBits == 0 ){
                // not a denorm, just a 0!
                decExponent = 0;
                digits = zero;
                nDigits = 1;
                return;
            }
            while ( (fractBits&singleFractHOB) == 0 ){
                fractBits <<= 1;
                binExp -= 1;
            }
            nSignificantBits = singleExpShift + binExp; // recall binExp is  - shift count.
            binExp += 1;
        } else {
            fractBits |= singleFractHOB;
            nSignificantBits = singleExpShift+1;
        }
        binExp -= singleExpBias;
        // call the routine that actually does all the hard work.
        dtoa( binExp, ((long)fractBits)<<(expShift-singleExpShift), nSignificantBits );
    }

    private void
    dtoa( int binExp, long fractBits, int nSignificantBits )
    {
        int     nFractBits; // number of significant bits of fractBits;
        int     nTinyBits;  // number of these to the right of the point.
        int     decExp;

        // Examine number. Determine if it is an easy case,
        // which we can do pretty trivially using float/long conversion,
        // or whether we must do real work.
        nFractBits = countBits( fractBits );
        nTinyBits = Math.max( 0, nFractBits - binExp - 1 );
        if ( binExp <= maxSmallBinExp && binExp >= minSmallBinExp ){
            // Look more closely at the number to decide if,
            // with scaling by 10^nTinyBits, the result will fit in
            // a long.
            if ( (nTinyBits < long5pow.length) && ((nFractBits + n5bits[nTinyBits]) < 64 ) ){
                /*
                 * We can do this:
                 * take the fraction bits, which are normalized.
                 * (a) nTinyBits == 0: Shift left or right appropriately
                 *     to align the binary point at the extreme right, i.e.
                 *     where a long int point is expected to be. The integer
                 *     result is easily converted to a string.
                 * (b) nTinyBits > 0: Shift right by expShift-nFractBits,
                 *     which effectively converts to long and scales by
                 *     2^nTinyBits. Then multiply by 5^nTinyBits to
                 *     complete the scaling. We know this won't overflow
                 *     because we just counted the number of bits necessary
                 *     in the result. The integer you get from this can
                 *     then be converted to a string pretty easily.
                 */
                long halfULP;
                if ( nTinyBits == 0 ) {
                    if ( binExp > nSignificantBits ){
                        halfULP = 1L << ( binExp-nSignificantBits-1);
                    } else {
                        halfULP = 0L;
                    }
                    if ( binExp >= expShift ){
                        fractBits <<= (binExp-expShift);
                    } else {
                        fractBits >>>= (expShift-binExp) ;
                    }
                    developLongDigits( 0, fractBits, halfULP );
                    return;
                }
                /*
                 * The following causes excess digits to be printed
                 * out in the single-float case. Our manipulation of
                 * halfULP here is apparently not correct. If we
                 * better understand how this works, perhaps we can
                 * use this special case again. But for the time being,
                 * we do not.
                 * else {
                 *     fractBits >>>= expShift+1-nFractBits;
                 *     fractBits *= long5pow[ nTinyBits ];
                 *     halfULP = long5pow[ nTinyBits ] >> (1+nSignificantBits-nFractBits);
                 *     developLongDigits( -nTinyBits, fractBits, halfULP );
                 *     return;
                 * }
                 */
            }
        }
        /*
         * This is the hard case. We are going to compute large positive
         * integers B and S and integer decExp, s.t.
         *      d = ( B / S ) * 10^decExp
         *      1 <= B / S < 10
         * Obvious choices are:
         *      decExp = floor( log10(d) )
         *      B      = d * 2^nTinyBits * 10^max( 0, -decExp )
         *      S      = 10^max( 0, decExp) * 2^nTinyBits
         * (noting that nTinyBits has already been forced to non-negative)
         * I am also going to compute a large positive integer
         *      M      = (1/2^nSignificantBits) * 2^nTinyBits * 10^max( 0, -decExp )
         * i.e. M is (1/2) of the ULP of d, scaled like B.
         * When we iterate through dividing B/S and picking off the
         * quotient bits, we will know when to stop when the remainder
         * is <= M.
         *
         * We keep track of powers of 2 and powers of 5.
         */

        /*
         * Estimate decimal exponent. (If it is small-ish,
         * we could double-check.)
         *
         * First, scale the mantissa bits such that 1 <= d2 < 2.
         * We are then going to estimate
         *          log10(d2) ~=~  (d2-1.5)/1.5 + log(1.5)
         * and so we can estimate
         *      log10(d) ~=~ log10(d2) + binExp * log10(2)
         * take the floor and call it decExp.
         * FIXME -- use more precise constants here. It costs no more.
         */
        double d2 = Double.longBitsToDouble(
            expOne | ( fractBits &~ fractHOB ) );
        decExp = (int)Math.floor(
            (d2-1.5D)*0.289529654D + 0.176091259 + (double)binExp * 0.301029995663981 );
        int B2, B5; // powers of 2 and powers of 5, respectively, in B
        int S2, S5; // powers of 2 and powers of 5, respectively, in S
        int M2, M5; // powers of 2 and powers of 5, respectively, in M
        int Bbits; // binary digits needed to represent B, approx.
        int tenSbits; // binary digits needed to represent 10*S, approx.
        FDBigInt Sval, Bval, Mval;

        B5 = Math.max( 0, -decExp );
        B2 = B5 + nTinyBits + binExp;

        S5 = Math.max( 0, decExp );
        S2 = S5 + nTinyBits;

        M5 = B5;
        M2 = B2 - nSignificantBits;

        /*
         * the long integer fractBits contains the (nFractBits) interesting
         * bits from the mantissa of d ( hidden 1 added if necessary) followed
         * by (expShift+1-nFractBits) zeros. In the interest of compactness,
         * I will shift out those zeros before turning fractBits into a
         * FDBigInt. The resulting whole number will be
         *      d * 2^(nFractBits-1-binExp).
         */
        fractBits >>>= (expShift+1-nFractBits);
        B2 -= nFractBits-1;
        int common2factor = Math.min( B2, S2 );
        B2 -= common2factor;
        S2 -= common2factor;
        M2 -= common2factor;

        /*
         * HACK!! For exact powers of two, the next smallest number
         * is only half as far away as we think (because the meaning of
         * ULP changes at power-of-two bounds) for this reason, we
         * hack M2. Hope this works.
         */
        if ( nFractBits == 1 )
            M2 -= 1;

        if ( M2 < 0 ){
            // oops.
            // since we cannot scale M down far enough,
            // we must scale the other values up.
            B2 -= M2;
            S2 -= M2;
            M2 =  0;
        }
        /*
         * Construct, Scale, iterate.
         * Some day, we'll write a stopping test that takes
         * account of the assymetry of the spacing of floating-point
         * numbers below perfect powers of 2
         * 26 Sept 96 is not that day.
         * So we use a symmetric test.
         */
        char digits[] = this.digits = new char[18];
        int  ndigit = 0;
        boolean low, high;
        long lowDigitDifference;
        int  q;

        /*
         * Detect the special cases where all the numbers we are about
         * to compute will fit in int or long integers.
         * In these cases, we will avoid doing FDBigInt arithmetic.
         * We use the same algorithms, except that we "normalize"
         * our FDBigInts before iterating. This is to make division easier,
         * as it makes our fist guess (quotient of high-order words)
         * more accurate!
         *
         * Some day, we'll write a stopping test that takes
         * account of the assymetry of the spacing of floating-point
         * numbers below perfect powers of 2
         * 26 Sept 96 is not that day.
         * So we use a symmetric test.
         */
        Bbits = nFractBits + B2 + (( B5 < n5bits.length )? n5bits[B5] : ( B5*3 ));
        tenSbits = S2+1 + (( (S5+1) < n5bits.length )? n5bits[(S5+1)] : ( (S5+1)*3 ));
        if ( Bbits < 64 && tenSbits < 64){
            if ( Bbits < 32 && tenSbits < 32){
                // wa-hoo! They're all ints!
                int b = ((int)fractBits * small5pow[B5] ) << B2;
                int s = small5pow[S5] << S2;
                int m = small5pow[M5] << M2;
                int tens = s * 10;
                /*
                 * Unroll the first iteration. If our decExp estimate
                 * was too high, our first quotient will be zero. In this
                 * case, we discard it and decrement decExp.
                 */
                ndigit = 0;
                q = ( b / s );
                b = 10 * ( b % s );
                m *= 10;
                low  = (b <  m );
                high = (b+m > tens );
                if ( q >= 10 ){
                    // bummer, dude
                    throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
                } else if ( (q == 0) && ! high ){
                    // oops. Usually ignore leading zero.
                    decExp--;
                } else {
                    digits[ndigit++] = (char)('0' + q);
                }
                /*
                 * HACK! Java spec sez that we always have at least
                 * one digit after the . in either F- or E-form output.
                 * Thus we will need more than one digit if we're using
                 * E-form
                 */
                if ( decExp <= -3 || decExp >= 8 ){
                    high = low = false;
                }
                while( ! low && ! high ){
                    q = ( b / s );
                    b = 10 * ( b % s );
                    m *= 10;
                    if ( q >= 10 ){
                        // bummer, dude
                        throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
                    }
                    if ( m > 0L ){
                        low  = (b <  m );
                        high = (b+m > tens );
                    } else {
                        // hack -- m might overflow!
                        // in this case, it is certainly > b,
                        // which won't
                        // and b+m > tens, too, since that has overflowed
                        // either!
                        low = true;
                        high = true;
                    }
                    digits[ndigit++] = (char)('0' + q);
                }
                lowDigitDifference = (b<<1) - tens;
            } else {
                // still good! they're all longs!
                long b = (fractBits * long5pow[B5] ) << B2;
                long s = long5pow[S5] << S2;
                long m = long5pow[M5] << M2;
                long tens = s * 10L;
                /*
                 * Unroll the first iteration. If our decExp estimate
                 * was too high, our first quotient will be zero. In this
                 * case, we discard it and decrement decExp.
                 */
                ndigit = 0;
                q = (int) ( b / s );
                b = 10L * ( b % s );
                m *= 10L;
                low  = (b <  m );
                high = (b+m > tens );
                if ( q >= 10 ){
                    // bummer, dude
                    throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
                } else if ( (q == 0) && ! high ){
                    // oops. Usually ignore leading zero.
                    decExp--;
                } else {
                    digits[ndigit++] = (char)('0' + q);
                }
                /*
                 * HACK! Java spec sez that we always have at least
                 * one digit after the . in either F- or E-form output.
                 * Thus we will need more than one digit if we're using
                 * E-form
                 */
                if ( decExp <= -3 || decExp >= 8 ){
                    high = low = false;
                }
                while( ! low && ! high ){
                    q = (int) ( b / s );
                    b = 10 * ( b % s );
                    m *= 10;
                    if ( q >= 10 ){
                        // bummer, dude
                        throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
                    }
                    if ( m > 0L ){
                        low  = (b <  m );
                        high = (b+m > tens );
                    } else {
                        // hack -- m might overflow!
                        // in this case, it is certainly > b,
                        // which won't
                        // and b+m > tens, too, since that has overflowed
                        // either!
                        low = true;
                        high = true;
                    }
                    digits[ndigit++] = (char)('0' + q);
                }
                lowDigitDifference = (b<<1) - tens;
            }
        } else {
            FDBigInt tenSval;
            int  shiftBias;

            /*
             * We really must do FDBigInt arithmetic.
             * Fist, construct our FDBigInt initial values.
             */
            Bval = new FDBigInt( fractBits  );
            if ( B5 != 0 ){
                if ( B5 < small5pow.length ){
                    Bval = Bval.mult( small5pow[B5] );
                } else {
                    Bval = Bval.mult( big5pow( B5 ) );
                }
            }
            if ( B2 != 0 ){
                Bval.lshiftMe( B2 );
            }
            Sval = new FDBigInt( big5pow( S5 ) );
            if ( S2 != 0 ){
                Sval.lshiftMe( S2 );
            }
            Mval = new FDBigInt( big5pow( M5 ) );
            if ( M2 != 0 ){
                Mval.lshiftMe( M2 );
            }


            // normalize so that division works better
            Bval.lshiftMe( shiftBias = Sval.normalizeMe() );
            Mval.lshiftMe( shiftBias );
            tenSval = Sval.mult( 10 );
            /*
             * Unroll the first iteration. If our decExp estimate
             * was too high, our first quotient will be zero. In this
             * case, we discard it and decrement decExp.
             */
            ndigit = 0;
            q = Bval.quoRemIteration( Sval );
            Mval = Mval.mult( 10 );
            low  = (Bval.cmp( Mval ) < 0);
            high = (Bval.add( Mval ).cmp( tenSval ) > 0 );
            if ( q >= 10 ){
                // bummer, dude
                throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
            } else if ( (q == 0) && ! high ){
                // oops. Usually ignore leading zero.
                decExp--;
            } else {
                digits[ndigit++] = (char)('0' + q);
            }
            /*
             * HACK! Java spec sez that we always have at least
             * one digit after the . in either F- or E-form output.
             * Thus we will need more than one digit if we're using
             * E-form
             */
            if ( decExp <= -3 || decExp >= 8 ){
                high = low = false;
            }
            while( ! low && ! high ){
                q = Bval.quoRemIteration( Sval );
                Mval = Mval.mult( 10 );
                if ( q >= 10 ){
                    // bummer, dude
                    throw new RuntimeException( "Assertion botch: excessivly large digit "+q);
                }
                low  = (Bval.cmp( Mval ) < 0);
                high = (Bval.add( Mval ).cmp( tenSval ) > 0 );
                digits[ndigit++] = (char)('0' + q);
            }
            if ( high && low ){
                Bval.lshiftMe(1);
                lowDigitDifference = Bval.cmp(tenSval);
            } else
                lowDigitDifference = 0L; // this here only for flow analysis!
        }
        this.decExponent = decExp+1;
        this.digits = digits;
        this.nDigits = ndigit;
        /*
         * Last digit gets rounded based on stopping condition.
         */
        if ( high ){
            if ( low ){
                if ( lowDigitDifference == 0L ){
                    // it's a tie!
                    // choose based on which digits we like.
                    if ( (digits[nDigits-1]&1) != 0 ) roundup();
                } else if ( lowDigitDifference > 0 ){
                    roundup();
                }
            } else {
                roundup();
            }
        }
    }

    public String
    toString(){
        // most brain-dead version
        StringBuffer result = new StringBuffer( nDigits+8 );
        if ( isNegative ){ result.append( '-' ); }
        if ( isExceptional ){
            result.append( digits, 0, nDigits );
        } else {
            result.append( "0.");
            result.append( digits, 0, nDigits );
            result.append('e');
            result.append( decExponent );
        }
        return new String(result);
    }

    public String
    toJavaFormatString(){
        char result[] = new char[ nDigits + 10 ];
        int  i = 0;
        if ( isNegative ){ result[0] = '-'; i = 1; }
        if ( isExceptional ){
            System.arraycopy( digits, 0, result, i, nDigits );
            i += nDigits;
        } else {
            if ( decExponent > 0 && decExponent < 8 ){
                // print digits.digits.
                int charLength = Math.min( nDigits, decExponent );
                System.arraycopy( digits, 0, result, i, charLength );
                i += charLength;
                if ( charLength < decExponent ){
                    charLength = decExponent-charLength;
                    System.arraycopy( zero, 0, result, i, charLength );
                    i += charLength;
                    result[i++] = '.';
                    result[i++] = '0';
                } else {
                    result[i++] = '.';
                    if ( charLength < nDigits ){
                        int t = nDigits - charLength;
                        System.arraycopy( digits, charLength, result, i, t );
                        i += t;
                    } else{
                        result[i++] = '0';
                    }
                }
            } else if ( decExponent <=0 && decExponent > -3 ){
                result[i++] = '0';
                result[i++] = '.';
                if ( decExponent != 0 ){
                    System.arraycopy( zero, 0, result, i, -decExponent );
                    i -= decExponent;
                }
                System.arraycopy( digits, 0, result, i, nDigits );
                i += nDigits;
            } else {
                result[i++] = digits[0];
                result[i++] = '.';
                if ( nDigits > 1 ){
                    System.arraycopy( digits, 1, result, i, nDigits-1 );
                    i += nDigits-1;
                } else {
                    result[i++] = '0';
                }
                result[i++] = 'E';
                int e;
                if ( decExponent <= 0 ){
                    result[i++] = '-';
                    e = -decExponent+1;
                } else {
                    e = decExponent-1;
                }
                // decExponent has 1, 2, or 3, digits
                if ( e <= 9 ) {
                    result[i++] = (char)( e+'0' );
                } else if ( e <= 99 ){
                    result[i++] = (char)( e/10 +'0' );
                    result[i++] = (char)( e%10 + '0' );
                } else {
                    result[i++] = (char)(e/100+'0');
                    e %= 100;
                    result[i++] = (char)(e/10+'0');
                    result[i++] = (char)( e%10 + '0' );
                }
            }
        }
        return new String(result, 0, i);
    }

    // jhyde added
    public FloatingDecimal(long n)
    {
        isExceptional = false; // I don't think longs can be exceptional
        if (n < 0) {
            isNegative = true;
            n = -n; // if n == MIN_LONG, oops!
        } else {
            isNegative = false;
        }
        if (n == 0) {
            nDigits = 1;
            digits = new char[] {'0','0','0','0','0','0','0','0'};
            decExponent = 0;
        } else {
            nDigits = 0;
            for (long m = n; m != 0; m = m / 10) {
                nDigits++;
            }
            decExponent = nDigits;
            digits = new char[nDigits];
            int i = nDigits - 1;
            for (long m = n; m != 0; m = m / 10) {
                digits[i--] = (char) ('0' + (m % 10));
            }
        }
    }

    // jhyde added
    public void shift(int i)
    {
        if (isExceptional ||
            nDigits == 1 && digits[0] == '0') {
            ; // don't multiply zero
        } else {
            decExponent += i;
        }
    }

    // jhyde added
    public String toJavaFormatString(
        int minDigitsLeftOfDecimal,
        char decimalChar, // '.' or ','
        int minDigitsRightOfDecimal,
        int maxDigitsRightOfDecimal, // todo: use
        char expChar, // 'E' or 'e'
        boolean expSign, // whether to print '+' if exp is positive
        int minExpDigits, // minimum digits in exponent
        char thousandChar) // ',' or '.', or 0
    {
        // char result[] = new char[nDigits + 10]; // crashes for 1.000.000,00
        // the result length does *not* depend from nDigits
        //  it is : decExponent
        //         +maxDigitsRightOfDecimal
        //         +10  (for decimal point and sign or -Infinity)
        //         +decExponent/3 (for the thousand separators)
        int resultLen = 10 + Math.abs(decExponent)*4/3 + maxDigitsRightOfDecimal;
        char result[] = new char[resultLen];
        int i = toJavaFormatString(
            result, 0, minDigitsLeftOfDecimal, decimalChar,
            minDigitsRightOfDecimal, maxDigitsRightOfDecimal, expChar, expSign,
            minExpDigits, thousandChar);
        return new String(result, 0, i);
    }

    // jhyde added
    private synchronized int toJavaFormatString(
        char result[],
        int i,
        int minDigitsLeftOfDecimal,
        char decimalChar, // '.' or ','
        int minDigitsRightOfDecimal,
        int maxDigitsRightOfDecimal, // todo: use
        char expChar, // 'E' or 'e'
        boolean expSign, // whether to print '+' if exp is positive
        int minExpDigits, // minimum digits in exponent
        char thousandChar) // ',' or '.' or 0
    {
        if (isNegative) {
            result[i++] = '-';
        }
        if (isExceptional) {
            System.arraycopy(digits, 0, result, i, nDigits);
            i += nDigits;
        } else if (expChar == 0) {
            // Build a new array of digits, padded with 0s at either end.  For
            // example, here is the array we would build for 1234.56.
            //
            // |  0     0     1     2     3  .  4     5     6     0     0   |
            // |           |- nDigits=6 -----------------------|            |
            // |           |- decExponent=3 -|                              |
            // |- minDigitsLeftOfDecimal=5 --|                              |
            // |                             |- minDigitsRightOfDecimal=5 --|
            // |- wholeDigits=5 -------------|- fractionDigits=5 -----------|
            // |- totalDigits=10 -------------------------------------------|
            // |                             |- maxDigitsRightOfDecimal=5 --|
            int wholeDigits = Math.max(decExponent, minDigitsLeftOfDecimal),
                fractionDigits = Math.max(
                    nDigits - decExponent, minDigitsRightOfDecimal),
                totalDigits = wholeDigits + fractionDigits;
            char[] digits2 = new char[totalDigits];
            for (int j = 0; j < totalDigits; j++) {
                digits2[j] = '0';
            }
            for (int j = 0; j < nDigits; j++) {
                digits2[wholeDigits - decExponent + j] = digits[j];
            }

            // Now round.  Suppose that we want to round 1234.56 to 1 decimal
            // place (that is, maxDigitsRightOfDecimal = 1).  Then lastDigit
            // initially points to '5'.  We find out that we need to round only
            // when we see that the next digit ('6') is non-zero.
            //
            // |  0     0     1     2     3  .  4     5     6     0     0   |
            // |                             |  ^   |                       |
            // |                                maxDigitsRightOfDecimal=1   |
            int lastDigit = wholeDigits + maxDigitsRightOfDecimal;
            if (lastDigit < totalDigits) {
                // We need to truncate -- also round if the trailing digits are
                // 5000... or greater.
                boolean trailingZeroes = true;
                int m = totalDigits;
                while (true) {
                    m--;
                    if (m < 0) {
                        // The entire number was 9s.  Re-allocate, so we can
                        // prepend a '1'.
                        wholeDigits++;
                        totalDigits++;
                        lastDigit++;
                        char[] old = digits2;
                        digits2 = new char[totalDigits];
                        digits2[0] = '1';
                        System.arraycopy(old, 0, digits2, 1, old.length);
                        break;
                    } else if (m == lastDigit) {
                        char d = digits2[m];
                        digits2[m] = '0';
                        if (d < '5' ||
                            d == '5' && trailingZeroes) {
                            break; // no need to round
                        }
                    } else if (m > lastDigit) {
                        if (digits2[m] > '0') {
                            trailingZeroes = false;
                        }
                        digits2[m] = '0';
                    } else if (digits2[m] == '9') {
                        digits2[m] = '0';
                        // do not break - we have to carry
                    } else {
                        digits2[m]++;
                        break; // nothing to carry
                    }
                }
            }

            // Find the first non-zero digit and the last non-zero digit.
            int firstNonZero = wholeDigits,
                firstTrailingZero = 0;
            for (int j = 0; j < totalDigits; j++) {
                if (digits2[j] != '0') {
                    if (j < firstNonZero) {
                        firstNonZero = j;
                    }
                    firstTrailingZero = j + 1;
                }
            }

            int firstDigitToPrint = firstNonZero;
            if (firstDigitToPrint > wholeDigits - minDigitsLeftOfDecimal) {
                firstDigitToPrint = wholeDigits - minDigitsLeftOfDecimal;
            }
            int lastDigitToPrint = firstTrailingZero;
            if (lastDigitToPrint > wholeDigits + maxDigitsRightOfDecimal) {
                lastDigitToPrint = wholeDigits + maxDigitsRightOfDecimal;
            }
            if (lastDigitToPrint < wholeDigits + minDigitsRightOfDecimal) {
                lastDigitToPrint = wholeDigits + minDigitsRightOfDecimal;
            }

            // Now print the number.
            for (int j = firstDigitToPrint; j < wholeDigits; j++) {
                if (thousandChar != '\0' &&
                    (wholeDigits - j) % 3 == 0 &&
                    j > firstDigitToPrint &&
                    j < wholeDigits - 1) {
                    result[i++] = thousandChar;
                }
                result[i++] = digits2[j];
            }
            for (int j = wholeDigits; j < lastDigitToPrint; j++) {
                if (j == wholeDigits) {
                    result[i++] = decimalChar;
                }
                result[i++] = digits2[j];
            }
        } else {
            // Make a recursive call to print the digits left of the 'E'.
            int oldExp = decExponent;
            decExponent = Math.min(minDigitsLeftOfDecimal, nDigits);
            boolean oldIsNegative = isNegative;
            isNegative = false;
            i = toJavaFormatString(
                result, i, minDigitsLeftOfDecimal, decimalChar,
                minDigitsRightOfDecimal, maxDigitsRightOfDecimal, (char) 0,
                false, minExpDigits, '\0');
            decExponent = oldExp;
            isNegative = oldIsNegative;

            result[i++] = expChar;
            int de = decExponent;
            if (nDigits == 1 && digits[0] == '0') {
                de = 1; // 0's exponent is 0, but that's not convenient here
            }
            int e;
            if ( de <= 0 ){
                result[i++] = '-';
                e = -de+1;
            } else {
                if (expSign) {
                    result[i++] = '+';
                }
                e = de-1;
            }
            // decExponent has 1, 2, or 3, digits
            int nExpDigits = e <= 9 ? 1 : e <= 99 ? 2 : 3;
            for (int j = nExpDigits; j < minExpDigits; j++) {
                result[i++] = '0';
            }
            if ( e <= 9 ) {
                result[i++] = (char)( e+'0' );
            } else if ( e <= 99 ){
                result[i++] = (char)( e/10 +'0' );
                result[i++] = (char)( e%10 + '0' );
            } else {
                result[i++] = (char)(e/100+'0');
                e %= 100;
                result[i++] = (char)(e/10+'0');
                result[i++] = (char)( e%10 + '0' );
            }
        }
        return i;
    }

    private static final int small5pow[] = {
        1,
        5,
        5*5,
        5*5*5,
        5*5*5*5,
        5*5*5*5*5,
        5*5*5*5*5*5,
        5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5*5*5*5*5,
        5*5*5*5*5*5*5*5*5*5*5*5*5
    };

    private static final long long5pow[] = {
        1L,
        5L,
        5L*5,
        5L*5*5,
        5L*5*5*5,
        5L*5*5*5*5,
        5L*5*5*5*5*5,
        5L*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
        5L*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5*5,
    };

    // approximately ceil( log2( long5pow[i] ) )
    private static final int n5bits[] = {
        0,
        3,
        5,
        7,
        10,
        12,
        14,
        17,
        19,
        21,
        24,
        26,
        28,
        31,
        33,
        35,
        38,
        40,
        42,
        45,
        47,
        49,
        52,
        54,
        56,
        59,
        61,
    };

    private static final char infinity[] = { 'I', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
    private static final char notANumber[] = { 'N', 'a', 'N' };
    private static final char zero[] = { '0', '0', '0', '0', '0', '0', '0', '0' };
}

/*
 * A really, really simple bigint package
 * tailored to the needs of floating base conversion.
 */
static class FDBigInt {
    int nWords; // number of words used
    int data[]; // value: data[0] is least significant

    private static boolean debugging = false;

    public static void setDebugging( boolean d ) { debugging = d; }

    public FDBigInt( int v ){
        nWords = 1;
        data = new int[1];
        data[0] = v;
    }

    public FDBigInt( long v ){
        data = new int[2];
        data[0] = (int)v;
        data[1] = (int)(v>>>32);
        nWords = (data[1]==0) ? 1 : 2;
    }

    public FDBigInt( FDBigInt other ){
        data = new int[nWords = other.nWords];
        System.arraycopy( other.data, 0, data, 0, nWords );
    }

    private FDBigInt( int [] d, int n ){
        data = d;
        nWords = n;
    }

    /*
     * Left shift by c bits.
     * Shifts this in place.
     */
    public void
    lshiftMe( int c )throws IllegalArgumentException {
        if ( c <= 0 ){
            if ( c == 0 )
                return; // silly.
            else
                throw new IllegalArgumentException("negative shift count");
        }
        int wordcount = c>>5;
        int bitcount  = c & 0x1f;
        int anticount = 32-bitcount;
        int t[] = data;
        int s[] = data;
        if ( nWords+wordcount+1 > t.length ){
            // reallocate.
            t = new int[ nWords+wordcount+1 ];
        }
        int target = nWords+wordcount;
        int src    = nWords-1;
        if ( bitcount == 0 ){
            // special hack, since an anticount of 32 won't go!
            System.arraycopy( s, 0, t, wordcount, nWords );
            target = wordcount-1;
        } else {
            t[target--] = s[src]>>>anticount;
            while ( src >= 1 ){
                t[target--] = (s[src]<<bitcount) | (s[--src]>>>anticount);
            }
            t[target--] = s[src]<<bitcount;
        }
        while( target >= 0 ){
            t[target--] = 0;
        }
        data = t;
        nWords += wordcount + 1;
        // may have constructed high-order word of 0.
        // if so, trim it
        while ( nWords > 1 && data[nWords-1] == 0 )
            nWords--;
    }

    /*
     * normalize this number by shifting until
     * the MSB of the number is at 0x08000000.
     * This is in preparation for quoRemIteration, below.
     * The idea is that, to make division easier, we want the
     * divisor to be "normalized" -- usually this means shifting
     * the MSB into the high words sign bit. But because we know that
     * the quotient will be 0 < q < 10, we would like to arrange that
     * the dividend not span up into another word of precision.
     * (This needs to be explained more clearly!)
     */
    public int
    normalizeMe() throws IllegalArgumentException {
        int src;
        int wordcount = 0;
        int bitcount  = 0;
        int v = 0;
        for ( src= nWords-1 ; src >= 0 && (v=data[src]) == 0 ; src--){
            wordcount += 1;
        }
        if ( src < 0 ){
            // oops. Value is zero. Cannot normalize it!
            throw new IllegalArgumentException("zero value");
        }
        /*
         * In most cases, we assume that wordcount is zero. This only
         * makes sense, as we try not to maintain any high-order
         * words full of zeros. In fact, if there are zeros, we will
         * simply SHORTEN our number at this point. Watch closely...
         */
        nWords -= wordcount;
        /*
         * Compute how far left we have to shift v s.t. its highest-
         * order bit is in the right place. Then call lshiftMe to
         * do the work.
         */
        if ( (v & 0xf0000000) != 0 ){
            // will have to shift up into the next word.
            // too bad.
            for( bitcount = 32 ; (v & 0xf0000000) != 0 ; bitcount-- )
                v >>>= 1;
        } else {
            while ( v <= 0x000fffff ){
                // hack: byte-at-a-time shifting
                v <<= 8;
                bitcount += 8;
            }
            while ( v <= 0x07ffffff ){
                v <<= 1;
                bitcount += 1;
            }
        }
        if ( bitcount != 0 )
            lshiftMe( bitcount );
        return bitcount;
    }

    /*
     * Multiply a FDBigInt by an int.
     * Result is a new FDBigInt.
     */
    public FDBigInt
    mult( int iv ) {
        long v = iv;
        int r[];
        long p;

        // guess adequate size of r.
        r = new int[ ( v * ((long)data[nWords-1]&0xffffffffL) > 0xfffffffL ) ? nWords+1 : nWords ];
        p = 0L;
        for( int i=0; i < nWords; i++ ) {
            p += v * ((long)data[i]&0xffffffffL);
            r[i] = (int)p;
            p >>>= 32;
        }
        if ( p == 0L){
            return new FDBigInt( r, nWords );
        } else {
            r[nWords] = (int)p;
            return new FDBigInt( r, nWords+1 );
        }
    }

    /*
     * Multiply a FDBigInt by another FDBigInt.
     * Result is a new FDBigInt.
     */
    public FDBigInt
    mult( FDBigInt other ){
        // crudely guess adequate size for r
        int r[] = new int[ nWords + other.nWords ];
        int i;
        // I think I am promised zeros...

        for( i = 0; i < this.nWords; i++ ){
            long v = (long)this.data[i] & 0xffffffffL; // UNSIGNED CONVERSION
            long p = 0L;
            int j;
            for( j = 0; j < other.nWords; j++ ){
                p += ((long)r[i+j]&0xffffffffL) + v*((long)other.data[j]&0xffffffffL); // UNSIGNED CONVERSIONS ALL 'ROUND.
                r[i+j] = (int)p;
                p >>>= 32;
            }
            r[i+j] = (int)p;
        }
        // compute how much of r we actually needed for all that.
        for ( i = r.length-1; i> 0; i--)
            if ( r[i] != 0 )
                break;
        return new FDBigInt( r, i+1 );
    }

    /*
     * Add one FDBigInt to another. Return a FDBigInt
     */
    public FDBigInt
    add( FDBigInt other ){
        int i;
        int a[], b[];
        int n, m;
        long c = 0L;
        // arrange such that a.nWords >= b.nWords;
        // n = a.nWords, m = b.nWords
        if ( this.nWords >= other.nWords ){
            a = this.data;
            n = this.nWords;
            b = other.data;
            m = other.nWords;
        } else {
            a = other.data;
            n = other.nWords;
            b = this.data;
            m = this.nWords;
        }
        int r[] = new int[ n ];
        for ( i = 0; i < n; i++ ){
            c += (long)a[i] & 0xffffffffL;
            if ( i < m ){
                c += (long)b[i] & 0xffffffffL;
            }
            r[i] = (int) c;
            c >>= 32; // signed shift.
        }
        if ( c != 0L ){
            // oops -- carry out -- need longer result.
            int s[] = new int[ r.length+1 ];
            System.arraycopy( r, 0, s, 0, r.length );
            s[i++] = (int)c;
            return new FDBigInt( s, i );
        }
        return new FDBigInt( r, i );
    }

    /*
     * Subtract one FDBigInt from another. Return a FDBigInt
     * Assert that the result is positive.
     */
    public FDBigInt
    sub( FDBigInt other ){
        int r[] = new int[ this.nWords ];
        int i;
        int n = this.nWords;
        int m = other.nWords;
        int nzeros = 0;
        long c = 0L;
        for ( i = 0; i < n; i++ ){
            c += (long)this.data[i] & 0xffffffffL;
            if ( i < m ){
                c -= (long)other.data[i] & 0xffffffffL;
            }
            if ( ( r[i] = (int) c ) == 0 )
                nzeros++;
            else
                nzeros = 0;
            c >>= 32; // signed shift.
        }
        if ( c != 0L )
            throw new RuntimeException("Assertion botch: borrow out of subtract");
        while ( i < m )
            if ( other.data[i++] != 0 )
                throw new RuntimeException("Assertion botch: negative result of subtract");
        return new FDBigInt( r, n-nzeros );
    }

    /*
     * Compare FDBigInt with another FDBigInt. Return an integer
     * >0: this > other
     *  0: this == other
     * <0: this < other
     */
    public int
    cmp( FDBigInt other ){
        int i;
        if ( this.nWords > other.nWords ){
            // if any of my high-order words is non-zero,
            // then the answer is evident
            int j = other.nWords-1;
            for ( i = this.nWords-1; i > j ; i-- )
                if ( this.data[i] != 0 ) return 1;
        }else if ( this.nWords < other.nWords ){
            // if any of other's high-order words is non-zero,
            // then the answer is evident
            int j = this.nWords-1;
            for ( i = other.nWords-1; i > j ; i-- )
                if ( other.data[i] != 0 ) return -1;
        } else{
            i = this.nWords-1;
        }
        for ( ; i > 0 ; i-- )
            if ( this.data[i] != other.data[i] )
                break;
        // careful! want unsigned compare!
        // use brute force here.
        int a = this.data[i];
        int b = other.data[i];
        if ( a < 0 ){
            // a is really big, unsigned
            if ( b < 0 ){
                return a-b; // both big, negative
            } else {
                return 1; // b not big, answer is obvious;
            }
        } else {
            // a is not really big
            if ( b < 0 ) {
                // but b is really big
                return -1;
            } else {
                return a - b;
            }
        }
    }

    /*
     * Compute
     * q = (int)( this / S )
     * this = 10 * ( this mod S )
     * Return q.
     * This is the iteration step of digit development for output.
     * We assume that S has been normalized, as above, and that
     * "this" has been lshift'ed accordingly.
     * Also assume, of course, that the result, q, can be expressed
     * as an integer, 0 <= q < 10.
     */
    public int
    quoRemIteration( FDBigInt S )throws IllegalArgumentException {
        // ensure that this and S have the same number of
        // digits. If S is properly normalized and q < 10 then
        // this must be so.
        if ( nWords != S.nWords ){
            throw new IllegalArgumentException("disparate values");
        }
        // estimate q the obvious way. We will usually be
        // right. If not, then we're only off by a little and
        // will re-add.
        int n = nWords-1;
        long q = ((long)data[n]&0xffffffffL) / (long)S.data[n];
        long diff = 0L;
        for ( int i = 0; i <= n ; i++ ){
            diff += ((long)data[i]&0xffffffffL) -  q*((long)S.data[i]&0xffffffffL);
            data[i] = (int)diff;
            diff >>= 32; // N.B. SIGNED shift.
        }
        if ( diff != 0L ) {
            // damn, damn, damn. q is too big.
            // add S back in until this turns +. This should
            // not be very many times!
            long sum = 0L;
            while ( sum ==  0L ){
                sum = 0L;
                for ( int i = 0; i <= n; i++ ){
                    sum += ((long)data[i]&0xffffffffL) +  ((long)S.data[i]&0xffffffffL);
                    data[i] = (int) sum;
                    sum >>= 32; // Signed or unsigned, answer is 0 or 1
                }
                /*
                 * Originally the following line read
                 * "if ( sum !=0 && sum != -1 )"
                 * but that would be wrong, because of the
                 * treatment of the two values as entirely unsigned,
                 * it would be impossible for a carry-out to be interpreted
                 * as -1 -- it would have to be a single-bit carry-out, or
                 * +1.
                 */
                if ( sum !=0 && sum != 1 )
                    throw new RuntimeException("Assertion botch: "+sum+" carry out of division correction");
                q -= 1;
            }
        }
        // finally, we can multiply this by 10.
        // it cannot overflow, right, as the high-order word has
        // at least 4 high-order zeros!
        long p = 0L;
        for ( int i = 0; i <= n; i++ ){
            p += 10*((long)data[i]&0xffffffffL);
            data[i] = (int)p;
            p >>= 32; // SIGNED shift.
        }
        if ( p != 0L )
            throw new RuntimeException("Assertion botch: carry out of *10");

        return (int)q;
    }

    public long
    longValue(){
        // if this can be represented as a long,
        // return the value
        int i;
        for ( i = this.nWords-1; i > 1 ; i-- ){
            if ( data[i] != 0 ){
                throw new RuntimeException("Assertion botch: value too big");
            }
        }
        switch(i){
        case 1:
            if ( data[1] < 0 )
                throw new RuntimeException("Assertion botch: value too big");
            return ((long)(data[1]) << 32) | ((long)data[0]&0xffffffffL);
        case 0:
            return ((long)data[0]&0xffffffffL);
        default:
            throw new RuntimeException("Assertion botch: longValue confused");
        }
    }

    public String
    toString() {
        StringBuffer r = new StringBuffer(30);
        r.append('[');
        int i = Math.min( nWords-1, data.length-1) ;
        if ( nWords > data.length ){
            r.append( "("+data.length+"<"+nWords+"!)" );
        }
        for( ; i> 0 ; i-- ){
            r.append( Integer.toHexString( data[i] ) );
            r.append( ' ' );
        }
        r.append( Integer.toHexString( data[0] ) );
        r.append( ']' );
        return new String( r );
    }
}
}

// End Format.java
