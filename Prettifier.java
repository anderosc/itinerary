import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prettifier {

    public static ArrayList<String> dataList = new ArrayList<String>();
    public static ArrayList<Integer> emptyElements = new ArrayList<Integer>();

    public static boolean airportLookupMalformed = false;
    public static ArrayList<String> airportLookUpOrder = new ArrayList<>();
    public static int airportLookUpNameIndex;
    public static int airportLookUpMunicipalityIndex;
    public static int airportLookUpIcaoCodeIndex;
    public static int airportLookUpIataCodeIndex;


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String BOLD = "\033[0;1m";




    public static void main(String[] args) {

        if (args.length == 1 && args[0].equals("-h")) {
            System.out.println(ANSI_GREEN + "itinerary usage:"+  ANSI_RESET);
            System.out.println(ANSI_GREEN + "java Prettifier.java /input.txt /output.txt /airport-lookup.csv" + ANSI_RESET) ;
            return;
        }

        if (args.length < 3) {
            System.out.println( ANSI_RED + ANSI_YELLOW_BACKGROUND + "Error: Please provide input file, output file, and lookup file." + ANSI_RESET + ANSI_BLACK_BACKGROUND);
            return;
        }


        setupAirpotLookup();

        // read input file
        try {
            File myObj = new File("input.txt");
            if (!myObj.exists()) {
                System.out.println(ANSI_RED + "Input not found" + ANSI_RESET);
                return;
            }
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine().trim();
                dataList.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            return;
        }

        // remove extra empty lines
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).equals("")) {
                if (i == 0) continue;
                if (dataList.get(i - 1).equals("")) {
                    emptyElements.add(i);
                }
            }
        }
        if (emptyElements.size() > 0) {
            for (int i = emptyElements.size() - 1; i >= 0; i--) {
                int removeIt = emptyElements.get(i);
                dataList.remove(removeIt);
            }
        }

        dataList = Patterns();

        printFile(dataList);
    }

    public static void setupAirpotLookup() {
        File myObj = new File("airport-lookup.csv");
        if(!myObj.exists()){
            System.out.println(ANSI_RED + "Airport lookup not found" + ANSI_RESET);
            System.exit(0);
        }

        try (BufferedReader br = new BufferedReader(new FileReader("airport-lookup.csv"))) {
            String firstLine = br.readLine();
            String[] values = firstLine.split(",");
            for (String value : values) {
                airportLookUpOrder.add(value.trim());
            }
            if (airportLookUpOrder.size() < 6 || !airportLookUpOrder.contains("name")) {
                System.out.println(ANSI_RED + "Airport lookup malformed" + ANSI_RESET);
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Error reading lookup file" + ANSI_RESET);
            System.exit(0);
        }
        airportLookUpNameIndex = airportLookUpOrder.indexOf("name");
        airportLookUpMunicipalityIndex = airportLookUpOrder.indexOf("municipality");
        airportLookUpIcaoCodeIndex = airportLookUpOrder.indexOf("icao_code");
        airportLookUpIataCodeIndex = airportLookUpOrder.indexOf("iata_code");
    }
    

    public static ArrayList<String> Patterns() {
        ArrayList<String> processedLines = new ArrayList<>();

        Pattern dPattern = Pattern.compile("D\\(([^)]+)\\)");
        Pattern t12Pattern = Pattern.compile("T12\\(([^)]+)\\)");
        Pattern t24Pattern = Pattern.compile("T24\\(([^)]+)\\)");
        Pattern airportPattern = Pattern.compile("(\\*?)#(#?)([A-Z]{3,4})");

        for (String line : dataList) {

            line = line.replaceAll("\\\\[fvr]", "\n");


            Matcher dMatcher = dPattern.matcher(line);
            while (dMatcher.find()) {
                String original = dMatcher.group(0);
                String manipulated = handleDate(original);
                line = line.replace(original, manipulated);
            }

            Matcher t12Matcher = t12Pattern.matcher(line);
            while (t12Matcher.find()) {
                String original = t12Matcher.group(0);
                String manipulated = T12(original);
                line = line.replace(original, manipulated);
            }

            Matcher t24Matcher = t24Pattern.matcher(line);
            while (t24Matcher.find()) {
                String original = t24Matcher.group(0);
                String manipulated = T24(original);
                line = line.replace(original, manipulated);
            }

            Matcher airportMatcher = airportPattern.matcher(line);
            while (airportMatcher.find()) {
                String fullMatch = airportMatcher.group(0);
                boolean isCity = airportMatcher.group(1).equals("*");
                String hash = airportMatcher.group(2);
                String code = airportMatcher.group(3);

                String replacement = airportCode(hash + code, isCity);
                if (replacement != null) {
                    line = line.replace(fullMatch, replacement);
                }
            }

            processedLines.add(line);
        }

        return processedLines;
    }

    public static String T12(String data) {
        int number = data.indexOf("T12");
        String date = data.substring(number + 4, data.length() - 1);
        String replaceableDate = data.substring(number, data.length());
    
        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1) + "+00:00";
        }
    
        ZonedDateTime zdt = ZonedDateTime.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a (XXX)");
        String formattedDate = zdt.format(formatter);
        
        if(formattedDate.contains("(Z)")){
            formattedDate = formattedDate.replace("(Z)", "(+00:00)");
        }
    
       // System.out.println(formattedDate);
    
        return data.replace(replaceableDate, formattedDate);
    }
    
    public static String T24(String data) {
        int number = data.indexOf("T24");
        String date = data.substring(number + 4, data.length() - 1);
        String replaceable = data.substring(number, data.length());
        date = date.trim();
    
        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1) + "+00:00";
        }
    
        ZonedDateTime zdt = ZonedDateTime.parse(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm (XXX)");
        String formattedDate = zdt.format(formatter);
    
        if(formattedDate.contains("(Z)")){
            formattedDate = formattedDate.replace("(Z)", "(+00:00)");
        }
    
        return data.replace(replaceable, formattedDate);
    }

    public static String handleDate(String data) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int number = data.indexOf("D(");
        String date = data.substring(number + 2, data.length() - 1);
        String day = date.substring(8, 10);
        String month = months[Integer.parseInt(date.substring(5, 7)) - 1];
        String year = date.substring(0, 4);
        return day + " " + month + " " + year;
    }

    
    
    public static String airportCode(String code, boolean isCityNameNeeded) {
        String searchCode = code.replace("#", "").toUpperCase();
        try (BufferedReader reader = new BufferedReader(new FileReader("airport-lookup.csv"))) {
            reader.readLine(); // skip header line
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\"([^\"]*)\"", "");  // Remove quotes and commas inside quotes

                String[] row = line.split(",", -1);
                if (row.length < airportLookUpOrder.size()) {
                    continue;
                }
    
                String icao = row[airportLookUpIcaoCodeIndex].trim().toUpperCase();
                String iata = row[airportLookUpIataCodeIndex].trim().toUpperCase();
    
                if (searchCode.equals(icao) || searchCode.equals(iata)) {
                    if (isCityNameNeeded) {
                        return row[airportLookUpMunicipalityIndex];
                    } else {
                        return row[airportLookUpNameIndex];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public static void printFile(ArrayList<String> printRows) {
        if (airportLookupMalformed) {
            return;
        }
        try {
            FileWriter fw = new FileWriter("output.txt"); // append = false
            BufferedWriter bw = new BufferedWriter(fw);
            for (String line : printRows) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_GREEN + "\n"
        + "\n" + " --- > The program ran successfully < --- \n" + 
        " --- > output.txt updated! < --- \n" + 
         "" + ANSI_BLACK_BACKGROUND + ANSI_RESET + "\n");
    }
}
