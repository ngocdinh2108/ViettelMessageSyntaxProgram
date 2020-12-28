import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class ViettelMessageSyntaxProgram {
    public static String structFileURL = "./Collections/input/struct.txt";
    public static String messageFileURL = "./Collections/input/message.txt";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        HashMap<Integer, String> listSyntaxByPrefixPhoneNumbers = filterSyntaxByPrefixPhoneNumber(structFileURL);
        Set<Integer> listPrefixPhoneNumbers = listSyntaxByPrefixPhoneNumbers.keySet();
        System.out.println("Cu phap cua tung dau so la:");
        for (Integer prefixPhoneNumber : listPrefixPhoneNumbers) {
            System.out.println(" -- Dau so: " + prefixPhoneNumber + " -- Cu phap: " + listSyntaxByPrefixPhoneNumbers.get(prefixPhoneNumber));
        }
        System.out.println("\n\n==========================================================================================\n\n");
        System.out.println("======== Tat ca cac tin nhan hop le sau khi loc la: ===========\n");
        System.out.println(checkValidMessage(messageFileURL));
        // Ghi danh sach tin nhan hop le vao file, dat ten file theo dau so
        writeFileByPrefixPhoneNumber();
    }

    public static StringBuffer readFile(String url) {
        StringBuffer result = new StringBuffer();
        try {
            FileReader fileReader = new FileReader(url);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }

                result.append(line);
                result.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static HashMap<Integer, String> filterSyntaxByPrefixPhoneNumber(String url) {
        HashMap<Integer, String> listSyntaxByPrefixPhoneNumbers = new HashMap<>();
        StringBuffer readStructFile = readFile(url);
        for (String lineStructFile : readStructFile.toString().split("\n")) {
            int indexPlus = lineStructFile.indexOf("+");
            int indexColon = lineStructFile.indexOf(":");
            int indexOpenBrackets = lineStructFile.indexOf("(");
            int indexCloseBrackets = lineStructFile.indexOf(")");
            Integer prefixPhoneNumber = Integer.parseInt(lineStructFile.substring(indexPlus + 1, indexColon));
            String syntax = lineStructFile.substring(indexOpenBrackets + 1, indexCloseBrackets);
            listSyntaxByPrefixPhoneNumbers.put(prefixPhoneNumber, syntax);
        }
        return listSyntaxByPrefixPhoneNumbers;
    }

    public static StringBuffer checkValidMessageSyntax(String url) {
        StringBuffer result = new StringBuffer();
        StringBuffer resultReadMessageFile = readFile(url);
        for (String lineResultReadMessageFile : resultReadMessageFile.toString().split("\n")) {
            int indexLastSeparate = lineResultReadMessageFile.lastIndexOf("|");
            int indexCloseBrackets = lineResultReadMessageFile.indexOf(")");
            int prefixPhoneNumbersInMessageFile = Integer.parseInt(lineResultReadMessageFile.substring(indexLastSeparate + 1, indexCloseBrackets));
            HashMap<Integer, String> listSyntaxByPrefixPhoneNumbers = filterSyntaxByPrefixPhoneNumber(structFileURL);
            Set<Integer> listPrefixPhoneNumbers = listSyntaxByPrefixPhoneNumbers.keySet();
            for (Integer prefixPhoneNumber : listPrefixPhoneNumbers) {
                if (prefixPhoneNumber == prefixPhoneNumbersInMessageFile) {
                    int indexOpenBrackets = lineResultReadMessageFile.indexOf("(");
                    int indexSeparate = lineResultReadMessageFile.indexOf("|");
                    String syntaxInMessageFile = lineResultReadMessageFile.substring(indexOpenBrackets + 1, indexSeparate);
                    if (listSyntaxByPrefixPhoneNumbers.get(prefixPhoneNumber).contains(syntaxInMessageFile.toUpperCase())) {
                        result.append(lineResultReadMessageFile);
                        result.append("\n");
                    }
                }
            }
        }
        return result;
    }

    public static StringBuffer checkValidMessageLogicTime(String url) {
        StringBuffer result = new StringBuffer();
        StringBuffer resultAfterCheckValidMessageSyntax = checkValidMessageSyntax(url);
        for (String lineResultAfterCheckValidMessageSyntax : resultAfterCheckValidMessageSyntax.toString().split("\n")) {
            int indexLastSeparate = lineResultAfterCheckValidMessageSyntax.lastIndexOf("|");
            int indexSeparate = lineResultAfterCheckValidMessageSyntax.indexOf("|");
            String StringTime = lineResultAfterCheckValidMessageSyntax.substring(indexSeparate + 1, indexLastSeparate);
            Pattern pattern = Pattern.compile("^\\d{1,2}-\\d{1,2}-\\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
            if (pattern.matcher(StringTime).find()) {
                Date stringToDate = null;
                try {
                    stringToDate = simpleDateFormat.parse(StringTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (simpleDateFormat.format(stringToDate).equals(StringTime)) {
                    Date presentTime = new Date();
                    if (presentTime.compareTo(stringToDate) > 0) {
                        result.append(lineResultAfterCheckValidMessageSyntax);
                        result.append("\n");
                    }
                }
            }
        }
        return result;
    }

    public static Set<String> filterAllPhoneNumbersWithPrefixPhoneNumbersFromMessageFile(String url) {
        Set<String> result = new HashSet<>();
        StringBuffer resultAfterCheckValidMessageLogicTime = checkValidMessageLogicTime(url);
        for (String line : resultAfterCheckValidMessageLogicTime.toString().split("\n")) {
            int indexPlus = line.indexOf("+");
            int indexOpenBrackets = line.indexOf("(");
            int indexLastSeparate = line.lastIndexOf("|");
            String phoneNumber = line.substring(indexPlus, indexOpenBrackets + 1);
            String prefixPhoneNumber = line.substring(indexLastSeparate + 1);
            String phoneNumberWithPrefixPhoneNumber = phoneNumber + prefixPhoneNumber;
            result.add(phoneNumberWithPrefixPhoneNumber);
        }
        return result;
        // +5312361631527(102)
    }

    public static StringBuffer checkValidMessage(String url) {
        StringBuffer result = new StringBuffer();
        Set<String> listPhoneNumberWithPrefixPhoneNumberFromMessageFile = filterAllPhoneNumbersWithPrefixPhoneNumbersFromMessageFile(url);
        StringBuffer resultAfterCheckValidMessageTime = checkValidMessageLogicTime(url);
        for (String lineFromListPhoneNumberWithPrefixPhoneNumberFromMessageFile : listPhoneNumberWithPrefixPhoneNumberFromMessageFile) {
            StringBuilder listMessageGroupByPhoneNumberAndPrefixPhoneNumber = new StringBuilder();
            int countLineInGroup = 0;
            for (String lineFromResultAfterCheckValidMessageTime : resultAfterCheckValidMessageTime.toString().split("\n")) {
                int indexPlus = lineFromResultAfterCheckValidMessageTime.indexOf("+");
                int indexOpenBrackets = lineFromResultAfterCheckValidMessageTime.indexOf("(");
                int indexLastSeparate = lineFromResultAfterCheckValidMessageTime.lastIndexOf("|");
                String phoneNumber = lineFromResultAfterCheckValidMessageTime.substring(indexPlus, indexOpenBrackets + 1);
                String prefixPhoneNumber = lineFromResultAfterCheckValidMessageTime.substring(indexLastSeparate + 1);
                String phoneNumberWithPrefixPhoneNumber = phoneNumber + prefixPhoneNumber;
                if (phoneNumberWithPrefixPhoneNumber.equals(lineFromListPhoneNumberWithPrefixPhoneNumberFromMessageFile)) {
                    listMessageGroupByPhoneNumberAndPrefixPhoneNumber.append(lineFromResultAfterCheckValidMessageTime);
                    listMessageGroupByPhoneNumberAndPrefixPhoneNumber.append("\n");
                    countLineInGroup++;
                }
            }
            if (countLineInGroup == 1) {
                result.append(listMessageGroupByPhoneNumberAndPrefixPhoneNumber);
            } else {
                TreeMap<Date, String> timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC = new TreeMap<>();
                for (String lineFromListMessageGroupByPhoneNumberAndPrefixPhoneNumber : listMessageGroupByPhoneNumberAndPrefixPhoneNumber.toString().split("\n")) {
                    int indexSeparate = lineFromListMessageGroupByPhoneNumberAndPrefixPhoneNumber.indexOf("|");
                    int indexLastSeparate = lineFromListMessageGroupByPhoneNumberAndPrefixPhoneNumber.lastIndexOf("|");
                    String timeString = lineFromListMessageGroupByPhoneNumberAndPrefixPhoneNumber.substring(indexSeparate + 1, indexLastSeparate);
                    Date stringToDate = null;
                    try {
                        stringToDate = simpleDateFormat.parse(timeString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC.put(stringToDate, lineFromListMessageGroupByPhoneNumberAndPrefixPhoneNumber);
                }
                Set<Date> times = timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC.keySet();
                Date temporaryVariable = new Date();
                int minNumberOfDaysApart = 30; // So ngay cach nhau toi thieu
                long millisecondsIn30Days = 86400000;
                for (Date time : times) {
                    if (time == timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC.firstKey()) {
                        result.append(timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC.get(time));
                        result.append("\n");
                        temporaryVariable = time;
                    } else {
                        long difference2days = (time.getTime() - temporaryVariable.getTime()) / millisecondsIn30Days;
                        if (difference2days >= minNumberOfDaysApart) {
                            result.append(timeListGroupByPhoneNumberAndPrefixPhoneNumberOrderASC.get(time));
                            result.append("\n");
                            temporaryVariable = time;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void writeFile(String outPutFileName, StringBuffer content) {
        try {
            FileWriter fileWriter = new FileWriter(outPutFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content.toString());
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ghi that bai!");
        }
    }

    public static void writeFileByPrefixPhoneNumber() { //String outPutFileName, String content
        StringBuffer listValidMessage = checkValidMessage(messageFileURL);
        TreeSet<String> listValidPrefixPhoneNumbers = new TreeSet<>();
        for (String lineInlistValidMessage : listValidMessage.toString().split("\n")) {
            int indexLastSeparate = lineInlistValidMessage.lastIndexOf("|");
            int indexCloseBrackets = lineInlistValidMessage.indexOf(")");
            String validPrefixPhoneNumber = lineInlistValidMessage.substring(indexLastSeparate + 1, indexCloseBrackets);
            listValidPrefixPhoneNumbers.add(validPrefixPhoneNumber);
        }
        for (String validPrefixPhoneNumber : listValidPrefixPhoneNumbers) {
            StringBuffer listValidMessageByPrefixPhoneNumbers = new StringBuffer();
            for (String lineInlistValidMessage : listValidMessage.toString().split("\n")) {
                if (lineInlistValidMessage.contains(validPrefixPhoneNumber)) {
                    listValidMessageByPrefixPhoneNumbers.append(lineInlistValidMessage);
                    listValidMessageByPrefixPhoneNumbers.append("\n");
                }
            }
            String outPutFileName = "./output/" + validPrefixPhoneNumber + ".txt";
            writeFile(outPutFileName, listValidMessageByPrefixPhoneNumbers);
        }
    }

}
