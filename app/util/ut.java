package util;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import util.lang.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static util.MapperUtil.map;

public class ut {
    final public static Object nil = null;

    public static String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        return result;
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static String unescapeUnicode(String s) {
        int i = 0, len = s.length();
        char c;
        StringBuffer sb = new StringBuffer(len);
        while (i < len) {
            c = s.charAt(i++);
            if (c == '\\') {
                if (i < len) {
                    c = s.charAt(i++);
                    if (c == 'u') {
                        // : check that 4 more chars exist and are all hex digits
                        c = (char) Integer.parseInt(s.substring(i, i + 4), 16);
                        i += 4;
                    } // add other cases here as desired...
                }
            } // fall through: \ escapes itself, quotes any character but u
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean anyEmpty(Object... input) {
        for (Object o : input) {
            if (empty(input)) {
                return true;
            }
        }
        return false;
    }

    public static boolean notInList(Object testSubject, Object... input) {
        return !inList(testSubject, input);
    }

    public static boolean inList(Object testSubject, Object... input) {
        for (Object o : input) {
            if (testSubject.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean allNotEmpty(Object... input) {
        for (Object o : input) {
            if (empty(input)) {
                return false;
            }
        }
        return true;
    }

    public static boolean notEmpty(Object input) {
        if (input == null) {
            return false;
        }
        if (input instanceof Collection) {
            return ((Collection) input).size() > 0;
        }
        if (input instanceof String) {
            if (input.equals("null")) {
                return false;
            }
            boolean empty = ((String) input).trim().equals("");
            return !empty;
        }
        if (input instanceof Long) {
            return !input.equals(0L);
        }
        if (input instanceof Integer) {
            return !input.equals(0);
        }
        if (input instanceof Double) {
            return !(Math.abs((Double) input) < 0.00000000001);
        }
        if (input instanceof Boolean) {
            return !input.equals(false);
        }
        if (input instanceof List) {
            return !(((List) input).size() == 0);
        }
        return true;
    }

    public static boolean empty(Object input) {
        return !notEmpty(input);
    }

    public static Date getTime(long l) {
        Date res = trim(getTimeJ(l));

        return res;
    }

    public static Date trimMinS(Date clone) {
        clone.setTime(clone.getTime() - clone.getTime() % 1000);
        return clone;
    }

    public static Date floorSlot(Date clone) {
        // trim millisecond
        clone.setTime(clone.getTime() - clone.getTime() % 1000);
        clone.setMinutes(clone.getMinutes() - clone.getMinutes() % 15);
        clone.setSeconds(0);
        return clone;
    }

    public static Date trim(Date input) {
        if (input == null) {
            return null;
        }
        Date clone = newDateJ(input.getTime());
        // trim millisecond
        clone.setTime(input.getTime() - input.getTime() % 1000);
        return clone;
    }

    public static boolean sameWeek(Date lastDate, Date newDate) {
        if (lastDate == null || newDate == null) {
            return false;
        }
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(lastDate);
        GregorianCalendar endCal = new GregorianCalendar();
        endCal.setTime(newDate);
        return lastDate.getYear() == newDate.getYear() && lastDate.getMonth() == newDate.getMonth() &&
                startCal.get(startCal.WEEK_OF_YEAR) ==
                        endCal.get(endCal.WEEK_OF_YEAR);
    }

    public static boolean sameMonth(Date lastDate, Date newDate) {
        if (lastDate == null || newDate == null) {
            return false;
        }
        return lastDate.getYear() == newDate.getYear() && lastDate.getMonth() == newDate.getMonth();
    }

    public static String GetOrSpace(String input) {
        return input == null ? "" : input;
    }

    public static String GetOrNull(Object input) {
        return input == null ? "NULL" : input.toString();
    }

    public static String generateKey(final int keyLength) {
        String generateKey = "";
        for (int i = 0; i < keyLength; i++) {
            final int k = (int) Math.floor(Math.random() * 62);
            if (k < 10) {
                generateKey += (char) (k + 48);
            } else if (k < 36) {
                generateKey += (char) (k + 55);
            } else {
                generateKey += (char) (k + 61);
            }
        }
        return generateKey.toUpperCase();
    }

    public static Date trimToDayStart(Date startTime) throws ParseException {
        Date result = newDateInGTM(startTime, "+8");
        result = trimToHourStart(result);
        result.setHours(0);
        return result;
    }

    public static Date trimToMonthStart(Date startTime) throws ParseException {
        Date result = newDateInGTM(startTime, "+8");
        result = trimToDayStart(result);
        result.setDate(1);
        return result;
    }

    public static Date newDateInGTM(Date date, String gmt) throws ParseException {
        Date result = new Date(date.getTime());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //df.setTimeZone(TimeZone.getTimeZone("GMT" + gmt));
        System.out.println("Date and time:" + df.format(result));
        result = df.parse(df.format(result));
        return result;
    }

    public static Date trimToHourStart(Date startTime) throws ParseException {
        Date result = newDateInGTM(startTime, "+8");
        result = trimMinS(result);
        result.setMinutes(0);
        result.setSeconds(0);
        result.setTime(result.getTime() + 1);
        return result;
    }

    public static Date trimToDayEnd(Date startTime) throws ParseException {
        Date result = newDateJ(startTime.getTime());
        result = trimToDayStart(result);
        result.setTime(result.getTime() + DAY - 2);
        return result;
    }

    public static HashMap<String, String> handleQueryString(String queryString2) {
        HashMap<String, String> queryMap = new HashMap<String, String>();
        if (empty(queryString2)) {
            return queryMap;
        }
        queryString2 = queryString2.replace("?", "");
        queryString2 = queryString2.replace("#", "");
        queryString2 = queryString2.replace("&amp;", "&");
        final String[] queries = queryString2.split("&");
        for (final String query : queries) {
            if (empty(query)) {
                continue;
            }
            String[] splitArray = query.split("=");
            final String key = splitArray[0];
            if (splitArray != null && splitArray.length > 1) {
                final String value = splitArray[1];
                queryMap.put(key.toLowerCase(), value);
            } else {
                queryMap.put(key.toLowerCase(), null);
            }
        }
        return queryMap;
    }

    public static HashMap<String, String> parseQueryString(String string) {
        string = trimQueryHeader(string);
        if (empty(string)) {
            return new HashMap<String, String>();
        }
        final String[] sets = string.split("&");
        final HashMap<String, String> map = new HashMap<String, String>(
                sets.length);
        for (final String set : Arrays.asList(sets)) {
            final String[] pair = set.split("=");
            if (pair.length < 2) {
                map.put(pair[0], null);
            } else {
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }

    public static String trimQueryHeader(final String startUpHistory) {
        String temp = startUpHistory;
        while (!isBlank(temp)
                && (temp.startsWith("?") || temp.startsWith("#"))) {
            temp = temp.substring(1, temp.length());
        }
        return temp;
    }

    public static boolean isBlank(final String e) {
        return e == null || e.trim().equals("");
    }

    public static ArrayList<String> split(List<String> split1, String s) {
        HashSet<String> out = new HashSet<String>();
        for (String s1 : split1) {
            String[] temp = s1.split(s);
            out.addAll(Arrays.asList(temp));
        }
        return new ArrayList<String>(out);
    }


    public static List<Long> decodeLongString(String merchantIdRaw) {
        if (empty(merchantIdRaw)) {
            return new ArrayList<Long>();
        }
        String[] array = merchantIdRaw.split(",");
        ArrayList<Long> ids = new ArrayList<Long>();
        for (String s : array) {
            if (notEmpty(s)) {
                ids.add(Long.parseLong(s));
            }
        }
        return ids;
    }

    public static String encodeLongString(List<Long> merchantIdRaw) {
        if (empty(merchantIdRaw)) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (Long aLong : merchantIdRaw) {
            buffer.append(aLong).append(",");
        }
        String stringValue = buffer.toString();
        if (stringValue.length() > 1) {
            return stringValue.substring(0, stringValue.length() - 1);
        }
        return stringValue;
    }

    public static Set<Long> decodeLongStringInSet(String merchantIdRaw) {
        if (empty(merchantIdRaw)) {
            return new HashSet<Long>();
        }
        String[] array = merchantIdRaw.split(",");
        HashSet<Long> ids = new HashSet<Long>();
        for (String s : array) {
            if (notEmpty(s)) {
                ids.add(Long.parseLong(s));
            }
        }
        return ids;
    }

    public static String encodeLongStringInSet(List<Long> merchantIdRaw) {
        if (empty(merchantIdRaw)) {
            return "";
        }
        HashSet<Long> longs = new HashSet<Long>(merchantIdRaw);
        return encodeLongStringInSet(longs);
    }

    public static String encodeLongStringInSet(Set<Long> merchantIdRaw) {
        if (empty(merchantIdRaw)) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (Long aLong : merchantIdRaw) {
            buffer.append(aLong).append(",");
        }
        String stringValue = buffer.toString();
        if (stringValue.length() > 1) {
            return stringValue.substring(0, stringValue.length() - 1);
        }
        return stringValue;
    }


    public static <T extends Collection> String mkString(T viewedString, String seperator) {
        if (viewedString == null || viewedString.size() == 0) {
            return "";
        }
        String temp = "";
        for (Object s : viewedString) {
            temp = temp + seperator + s.toString();
        }
        temp = temp.substring(seperator.length(), temp.length());
        return temp;
    }


    public static String formatDouble(Double inputPrecent) {
        if (empty(inputPrecent)) {
            return "0";
        }
        return (((int) (inputPrecent * 100)) + "");
    }


    public static String formatDateToRange(Date startTime, Date endTime) {
        if (sameDay(startTime, endTime)) {
            return strings.format("%s %s - %s",
                    DateToYYMMDD(startTime),
                    DateToHHMMA(startTime),
                    DateToHHMMA(endTime)
            );
        } else {
            return strings.format("%s - %s",
                    formatDateToYYMMDD_HHMMA(startTime),
                    formatDateToYYMMDD_HHMMA(endTime)
            );
        }
    }

    public static String DateToYYMMDD(final Date date) {
        return (date.getYear() + 1900) + "/" + date.getMonth() + 1 + "/"
                + date.getDate();
    }


    public static String DateToHHMMA(final Date startTime) {
        String amPm = "AM";
        if (startTime.getHours() > 12) {
            amPm = "PM";
        }
        return strings.format("%s:%s %s %s/%s/%s",
                startTime.getHours(),
                startTime.getMinutes(),
                amPm
        );
    }

    public static String formatDateToYYMMDD_HHMMA(final Date startTime) {
        String amPm = "AM";
        if (startTime.getHours() > 12) {
            amPm = "PM";
        }
        return strings.format("%s:%s %s %s/%s/%s",
                startTime.getHours(),
                startTime.getMinutes(),
                amPm,
                startTime.getDate(),
                startTime.getMonth() + 1,
                startTime.getYear()
        );
    }

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mma", Locale.US);

    public static String formatDateToYYYYMMDD_HHMM(final Date startTime) {
        if (startTime == null) {
            return "";
        }
        return simpleDateFormat.format(startTime);
    }

    public static String formatDateToHHMM(final Date startTime) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm", Locale.US);
        return simpleDateFormat1.format(startTime);
    }

    public static String formatDateToHHMMa(final Date startTime) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a", Locale.US);
        return simpleDateFormat1.format(startTime);
    }

    public static String formatDateToDDMMYY(final Date startTime) {
        Date startTime2 = startTime;
        if (startTime == null) {
            startTime2 = newDateJ();
        }
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy", Locale.US);
        return simpleDateFormat1.format(startTime2);
    }

    public static String formatDateToDD_MM_YY(final Date startTime) {
        Date startTime2 = startTime;
        if (startTime == null) {
            startTime2 = newDateJ();
        }
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd_MM_yy", Locale.US);
        return simpleDateFormat1.format(startTime2);
    }

    public static String formatDateToDDMMMYYYY(final Date startTime) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        if (startTime == nil) {
            return simpleDateFormat1.format(newDateJ());
        }
        return simpleDateFormat1.format(startTime);
    }

    public static String formatDateToDDMMMYYYYhmma(final Date startTime) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMM yyyy (h:mm a)", Locale.US);
        return simpleDateFormat1.format(startTime);
    }

    public static String formatDateToMMMYYYY(final Date startTime) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM YYYY", Locale.US);
        return simpleDateFormat1.format(startTime);
    }

    public static String formatDateToYYYYMMDD(final Date startTime) {
        if (startTime == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(startTime);
    }

    public static Date formatYYYYMMDDhhmmStringToDate(String dateString) {
        return formatStringToDate("yyyy-MM-dd HH:mm", dateString);
    }

    public static Date formatYYYYMMDDhhmmStringToDate(String date, String time) {
        Date date1 = formatStringToDate("yyyy-MM-dd", date);
        Date date2 = formatStringToDate("HH:mm a", time);
        date1.setHours(date2.getHours());
        date1.setMinutes(date2.getMinutes());
        return date1;
    }

    public static Date formatddMMyyhhmmaStringToDate(String date, String time) {
        Date date1 = formatStringToDate("dd/MM/yy hh:mm aa", date + " " + time);
        return date1;
    }

    public static Date formatStringToDate(final String pattern, String input) {
        try {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern, Locale.ENGLISH);
            return simpleDateFormat1.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String replaceSpace(String name) {
        if (empty(name)) {
            return "";
        }
        return name.replace(" ", "_");
    }

    public static boolean equal(Object input, Object input2) {
        if (empty(input)) {
            return empty(input2);
        } else {
            return input.equals(input2);
        }
    }

    public static boolean notEqual(Object input, Object input2) {
        return !equal(input, input2);
    }

    public static boolean emptyOr(Object input, Object input2) {
        return !notEmpty(input) || !notEmpty(input2);
    }

    public static boolean validEmail(String input) {
        if (empty(input)) {
            return false;
        }
        boolean validation = (input.matches(EMAIL_VALIDATION_REGEX));
        return validation;
    }

    public static boolean notValidEmail(String input) {
        return !validEmail(input);
    }

    public static void unexpected() {
        throw new RuntimeException("unexpected logic");
    }

    public static void unimplemented() {
        throw new RuntimeException("unimplemented feature");
    }

    public static void unmaintained() {
        throw new RuntimeException("unmaintained feature");
    }

    public final static String EMAIL_VALIDATION_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)";


    public static final long SECOND = 1000;
    public static final long MINUTES = SECOND * 60;
    public static final long HOUR = MINUTES * 60;
    public static final long DAY = HOUR * 24;
    public static final long WEEK = DAY * 7;
    public static final long MONTH = WEEK * 4;
    public static final long YEAR = DAY * 365;

    public static String generateKey() {
        return generateKey(20);
    }

    public static void init() {
        if (!inited) {

        }
    }

    static boolean inited = false;

    public static String formatDateToJavaDMY(Date now) {
        return now.getDate() + "-" + now.getMonth() + 1 + "-" + now.getYear();
    }

    public static Date trimToHalfHourStart(Date endDate2) {
        Date endDate = newDateJ(endDate2.getTime());
        if (endDate.getMinutes() >= 30) {
            endDate.setMinutes(30);
        } else {
            endDate.setMinutes(0);
        }
        return endDate;
    }

    public static String getShortTime(Date time) {
        String num = time.getHours() + "";
        String part1 = "pm";
        String part2 = "";
        if (time.getHours() < 12) {
            part1 = "am";
        } else if (time.getHours() == 12) {
            part1 = "nn";
        } else {
            num = time.getHours() - 12 + "";
        }
        if (time.getMinutes() != 0) {
            part2 = time.getMinutes() + "";
        }
        return strings.format("%s%s%s", num, part1, part2);
    }

    private static String duration(Date startDate, Date endDate) {
        String startAm = getAmpm(startDate.getHours());
        String endAm = getAmpm(endDate.getHours());
        String startMin = getMin(startDate);
        String endMin = getMin(endDate);
        if (startAm.equals(endAm)) {
            return strings.format("%s-%s%s", startMin, endMin, endAm);
        }
        return strings.format("%s%s-%s%s", startMin, startAm, endMin, endAm);
    }

    private static String getMin(Date startDate) {
        String mins = startDate.getMinutes() == 0 ? "" : ":" + startDate.getMinutes();
        int hours = startDate.getHours();
        if (hours > 12) hours -= 12;
        return hours + mins;
    }

    private static String getAmpm(int hours) {
        return hours > 12 ? "am" : "pm";
    }

    private static boolean thisMonth(final GregorianCalendar inputCal,
                                     final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH, 1);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    private static boolean nextMonth(final GregorianCalendar inputCal,
                                     final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.add(Calendar.MONTH, 1);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    private static boolean perviousMonth(final GregorianCalendar inputCal,
                                         final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH, 1);
        input.add(Calendar.MONTH, 1);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    // inputCal is next week of now
    private static boolean nextWeek(
            final GregorianCalendar inputCal,
            final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.setFirstDayOfWeek(0);
        now.setFirstDayOfWeek(0);
        input.set(Calendar.DAY_OF_WEEK, 0);
        now.set(Calendar.DAY_OF_WEEK, 0);
        now.add(Calendar.DATE, 7);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    private static boolean thisWeek(
            final GregorianCalendar inputCal,
            final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.setFirstDayOfWeek(0);
        now.setFirstDayOfWeek(0);
        input.set(Calendar.DAY_OF_WEEK, 0);
        now.set(Calendar.DAY_OF_WEEK, 0);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    // inputCal is previous week of now
    private static boolean perviousWeek(
            final GregorianCalendar inputCal,
            final GregorianCalendar nowCal) {
        final GregorianCalendar input = (GregorianCalendar) inputCal.clone();
        final GregorianCalendar now = (GregorianCalendar) nowCal.clone();
        input.setFirstDayOfWeek(0);
        now.setFirstDayOfWeek(0);
        input.add(Calendar.DATE, 7);
        input.set(Calendar.DAY_OF_WEEK, 0);
        now.set(Calendar.DAY_OF_WEEK, 0);
        return input.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                && input.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    public static boolean sameDay(final Date now, final Date newDate) {
        if (now == null || newDate == null) {
            return false;
        }
        return now.getDate() == newDate.getDate()
                && now.getMonth() == newDate.getMonth()
                && now.getYear() == newDate.getYear();
    }

    public static boolean isBlank(final Object e) {
        if (e == null) {
            return false;
        }
        if (e instanceof String) {
            return isBlank(((String) e));
        } else if (e instanceof Collection) {
            final Collection collection = (Collection) e;
            if (collection.size() == 0) {
                return true;
            }
        }
        return false;
    }


    public static String genTipContent(String content) {
        return strings.format("<div class=\"tipHolderContent\">%s</div>", content);
    }


    public static ArrayList<String> parsePattern(String wholePattern, String inputHistory) throws ParseStringException {
        ArrayList<String> result = new ArrayList<String>();
        String[] patterns = wholePattern.split("%s");
        String remainString = inputHistory;
        for (int i = 0; i < patterns.length - 1; i++) {
            String pattern = patterns[i];
            String nextPattern = patterns[i + 1];
            final int startPoint = remainString.indexOf(pattern) + pattern.length();
            String target = remainString.substring(startPoint, remainString.indexOf(nextPattern, startPoint));
            result.add(target);
            remainString = remainString.substring(remainString.indexOf(nextPattern, startPoint));
        }
        result.add(remainString.substring(patterns[patterns.length - 1].length()));
        return result;
    }

    public static ArrayList<String> parseUrl(String wholePattern, String inputHistory) throws ParseStringException {
        ArrayList<String> result = new ArrayList<String>();
        String[] patterns = wholePattern.split("/");
        String[] inputToken = inputHistory.split("/");
        if (patterns.length != inputToken.length) {
            throw new ParseStringException();
        }
        for (int i = 0; i < patterns.length; i++) {
            String pattern = patterns[i];
            if (pattern.equals("%s")) {
                result.add(inputToken[i]);
            }
        }
        return result;
    }

    public static Long toLong(String input) {
        return Long.parseLong(input);
    }

    public static List removeLeft(List input, int i) {
        if (input == null) {
            return new ArrayList();
        }
        if (input.size() >= i) {
            for (int j = 0; j < i; j++) {
                if (input.size() > 0) {
                    input.remove(0);
                }
            }
        } else {
            input.clear();
        }
        return input;
    }

    public static <T> ArrayList<T> takeLeft(List<T> input, int i) {
        if (input == null) {
            return new ArrayList<T>();
        }
        ArrayList<T> ress = new ArrayList<T>();
        if (input.size() >= i) {
            for (int j = 0; j < i; j++) {
                ress.add(input.get(j));
            }
        } else {
            ress.addAll(input);
        }
        return ress;
    }

    public static String removeLinkification(String imageUrl) {
        imageUrl = imageUrl.replaceAll("<file-1-imageurl>(.*)</file-1-imageurl>", "$1");
        imageUrl = imageUrl.replaceAll("<a.*>(.*)</a>", "$1");
        return imageUrl;
    }

    public static boolean sameHHMM(Date time1, Date time2) {
        return sameHour(time1, time2) && sameMinutes(time1, time2);
    }

    private static boolean sameMinutes(Date time1, Date time2) {
        return time1.getMinutes() == time2.getMinutes();
    }

    private static boolean sameHour(Date time1, Date time2) {
        return time1.getHours() == time2.getHours();
    }

    public static int rand(int i) {
        return (int) (Math.random() * i);
    }

    private static Date trimToYearStart(Date date) throws ParseException {
        date.setMonth(0);
        date.setDate(1);
        return trimToDayStart(date);
    }

    public static Date getYear(int i) throws ParseException {
        Date date = newDateJ();
        date.setYear(i - 1900);
        return trimToYearStart(date);
    }

    private static Long ticket = 1L;

    public static Long getTicket() {
        Long curTicket = ticket;
        ticket += 1;
        return curTicket;
    }

    public static boolean within(Date day, Date startDate, Date endDate) throws ParseException {
        long target = ut.trimToDayStart(day).getTime();
        long start = ut.trimToDayStart(startDate).getTime();
        long end = ut.trimToDayStart(endDate).getTime();
        return start <= target && start <= end;
    }

    public static boolean endWithAny(String fileName, String... postFix) {
        String file2 = fileName.toLowerCase();
        for (String s : postFix) {
            String lowerPostFix = s.toLowerCase();
            if (file2.endsWith(lowerPostFix)) {
                return true;
            }
        }
        return false;
    }

    public static Long TimeMachineJ = 0L;

    public static Date newDateJ() {
        Date res = new Date(currentTimeJ());
        return res;
    }

    //input expect currentTime in millies
    //NOT offset
    public static Date newDateJ(Long start) {
        Date res = new Date(start + TimeMachineJ);
        return res;
    }

    public static Date getTimeJ(Long offset) {
        Date res = new Date(currentTimeJ() + offset);
        return res;
    }

    public static Long currentTimeJ() {
        long res = System.currentTimeMillis() + TimeMachineJ;
        return res;
    }

    public static String getDurationString(Long millisS) {
        Long minutes = millisS / ut.MINUTES;
        Long seconds = (millisS % ut.MINUTES) / ut.SECOND;
        String minutesStr = minutes + "";
        if (minutes < 10)
            minutesStr = "0" + minutesStr;
        String secondsStr = seconds + "";
        if (seconds < 10)
            secondsStr = "0" + secondsStr;
        return strings.format("%s:%s", minutesStr, secondsStr);
    }

    public static boolean isValidEmail(String value) {
        if (value == null) return true;
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
        boolean valid = false;
        if (value.getClass().toString().equals(String.class.toString())) {
            valid = ((String) value).matches(emailPattern);
        } else {
            valid = ((Object) value).toString().matches(emailPattern);
        }
        return valid;
    }

    public static <T> T filterOne(List<T> results, f1R<T, Boolean> f1R) {
        for (T result : results) {
            if (f1R.run(result)) {
                return result;
            }
        }
        return null;
    }

    static ExecutorService executor = Executors.newFixedThreadPool(getCores() * 2);

    private static int getCores() {
        int cores = Runtime.getRuntime().availableProcessors();
        return cores;
    }

    public static void awaitAll(F... fs) {
        Collection<Callable<Object>> callables = map(fs, new Mapper<F, Callable<Object>>() {
            @Override
            public Callable<Object> map(final F input) {
                return new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        input.r();
                        return null;
                    }
                };
            }
        });
        try {
            List<Future<Object>> results = executor.invokeAll(callables);
            for (Future<Object> result : results) {
                try {
                    result.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void assertNotEmpty(Object user) {
        assert (notEmpty(user));
    }

    public static List<Object> fork(List<fR> fxs) {
        ArrayList<Object> ress = new ArrayList<Object>();
        for (fR fx : fxs) {
            Object res = fx.run();
            ress.add(res);
        }
        return ress;
    }

    static logger log = logger.getLogger(ut.class);

    public static List<Object> fork2(List<fR> fxs) {
        Collection<MyCallable> callables = map(fxs, new Mapper<fR, MyCallable>() {
            @Override
            public MyCallable map(final fR input) {
                return new MyCallable() {
                    @Override
                    public Object call() throws Exception {
                        return input.run();
                    }
                };
            }
        });
        ArrayList<Object> ress = new ArrayList<Object>();
        try {
            List<Future<Object>> res = executor.invokeAll(callables);
            for (Future<Object> re : res) {
                if (!re.isDone()) {
                    log.error("skipped a result");
                } else {
                    try {
                        assert (re != null);
                        Object e = re.get();
                        assert (e != null);
                        ress.add(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ress;
    }

    public static <T, R> List parallelLoop1(List<T> input, final f1<T> fx) {
        List<fR> fxs = new ArrayList<fR>();
        for (final T item : input) {
            fxs.add(new fR() {
                @Override
                public Object run() {
                    fx.run(item);
                    return null;
                }
            });
        }
        return ut.fork2(fxs);
    }

    public static <T, R> List parallelLoop(List<T> input, final f1R<T, R> fx) {
        List<fR> fxs = new ArrayList<fR>();
        for (final T item : input) {
            fxs.add(new fR() {
                @Override
                public Object run() {
                    fx.run(item);
                    return null;
                }
            });
        }
        return ut.forkWithLog(fxs);
    }

    public static List<Object> forkWithLog(List<fR> fxs) {
        final int listCount = fxs.size();
        final ArrayList resultCount = new ArrayList();
        final ArrayList<Date> logDate = new ArrayList<Date>();
        logDate.add(newDateJ());
        Collection<MyCallable> callables = map(fxs, new Mapper<fR, MyCallable>() {
            @Override
            public MyCallable map(final fR input) {
                return new MyCallable() {
                    @Override
                    public Object call() throws Exception {
                        Object run = input.run();
                        synchronized (resultCount) {
                            resultCount.add(new Object());
                            synchronized (logDate) {
                                Date lastLogDate = logDate.get(logDate.size() - 1);
                                if ((newDateJ().getTime() - lastLogDate.getTime()) > 1000) {
                                    log.info("task finished %s/%s", resultCount.size(), listCount);
                                    logDate.add(newDateJ());
                                }
                            }
                            if (resultCount.size() == listCount)
                                log.info("task finished %s/%s", resultCount.size(), listCount);
                        }
                        return run;
                    }
                };
            }
        });
        ArrayList<Object> ress = new ArrayList<Object>();
        try {
            List<Future<Object>> res = executor.invokeAll(callables);
            for (Future<Object> re : res) {
                if (!re.isDone()) {
                    log.error("skipped a result");
                } else {
                    try {
                        assert (re != null);
                        Object e = re.get();
                        assert (e != null);
                        ress.add(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ress;
    }

    public static List<Object> forkWithLogFx(List<fR> fxs, final f2<Integer, Integer> updateFx) {
        final int listCount = fxs.size();
        final ArrayList resultCount = new ArrayList();
        final ArrayList<Date> logDate = new ArrayList<Date>();
        logDate.add(newDateJ());
        Collection<MyCallable> callables = map(fxs, new Mapper<fR, MyCallable>() {
            @Override
            public MyCallable map(final fR input) {
                return new MyCallable() {
                    @Override
                    public Object call() throws Exception {
                        Thread thread = Thread.currentThread();
                        thread.setPriority(Thread.MIN_PRIORITY);
                        Object run = input.run();
                        synchronized (resultCount) {
                            resultCount.add(new Object());
                            synchronized (logDate) {
                                Date lastLogDate = logDate.get(logDate.size() - 1);
                                if ((newDateJ().getTime() - lastLogDate.getTime()) > 1000) {
                                    log.info("task finished %s/%s", resultCount.size(), listCount);
                                    logDate.add(newDateJ());
                                    updateFx.run(resultCount.size(), listCount);
                                }
                            }
                            if (resultCount.size() == listCount) {
                                log.info("task finished %s/%s", resultCount.size(), listCount);
                                updateFx.run(resultCount.size(), listCount);
                            }
                        }
                        thread.setPriority(Thread.NORM_PRIORITY);
                        return run;
                    }
                };
            }
        });
        ArrayList<Object> ress = new ArrayList<Object>();
        try {
            List<Future<Object>> res = executor.invokeAll(callables);
            for (Future<Object> re : res) {
                if (!re.isDone()) {
                    log.error("skipped a result");
                } else {
                    try {
                        assert (re != null);
                        Object e = re.get();
                        assert (e != null);
                        ress.add(e);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ress;
    }

    public static Date setToday(Date endTime) {
        Date today = new Date();
        endTime.setYear(today.getYear());
        endTime.setMonth(today.getMonth());
        endTime.setDate(today.getDate());
        return endTime;
    }

    public static String formatMoney(Double salaryTotal) {
        DecimalFormat df = new DecimalFormat("$#.##");
        return df.format(salaryTotal);
    }

    public static String formatHour(Double salaryTotal) {
        DecimalFormat df = new DecimalFormat("#.## Hours");
        return df.format(salaryTotal);
    }

    public static int getMinutesInDay(Date dutyIn) {
        return dutyIn.getHours() * 60 + dutyIn.getMinutes();
    }

    public static String encryptPassword(String password) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static String salt1 = "zysXf4a5p16AKDF34FFFF";

    public static String sha512(String input) {
        Security.addProvider(new BouncyCastleProvider());
        String data = input + salt1;
        MessageDigest mda = null;
        try {
            mda = MessageDigest.getInstance("SHA-512", "BC");
            byte[] digesta = mda.digest(data.getBytes());
            char[] x = Hex.encodeHex(digesta);
            return new String(x);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static fR<Long> startLogging() {
        final Long start = System.currentTimeMillis();
        return new fR<Long>() {
            @Override
            public Long run() {
                return System.currentTimeMillis() - start;
            }
        };
    }
}
