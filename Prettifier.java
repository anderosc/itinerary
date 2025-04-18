import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prettifier {

    public static ArrayList<String> dataList = new ArrayList<String>();
    public static ArrayList<Integer> emptyElements = new ArrayList<Integer>();

    public static ArrayList<Integer> printToFileElements = new ArrayList<Integer>();

    public static boolean airportLookupMalformed = false;
    public static ArrayList<String> airportLookUpOrder = new ArrayList<>();
    public static int airportLookUpNameIndex;
    public static int airportLookUpMunicipalityIndex;


    public static void main(String[] args){

        //arguments
        if(args.length > 0 && args[0].equals("-h")){
            System.out.println("itinerary usage:");
            System.out.println("java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
            return;
        }

        ArrayList<String> argsList = new ArrayList<String>();
        
        for (String s: args){
            argsList.add(s);
        }

        if(args.length < 3){
            return;
        }

        if(!argsList.contains("./input.txt") || !argsList.contains("./output.txt") || !argsList.contains("./airport-lookup.csv")){
            return;
        }

        setupAirpotLookup();

        //read every line and put it in arraylist
        try{
            File myObj = new File("input.txt");
            if(!myObj.exists()){
                System.out.println("Input not found");
                return;
            }
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()){
                String data = myReader.nextLine();
                data = data.trim();

                dataList.add(data);
            }

            myReader.close();
            
        } catch (FileNotFoundException e){
            System.out.println("errrrror");
        }

        //loop through elements to find empty ones and save it to arraylist
        for(int i = 0; i < dataList.size(); i++){
            if(dataList.get(i).equals("")){

                if(i == 0){
                    continue;
                }

                if(dataList.get(i-1).equals("")){
                    emptyElements.add(i);
                }

                if(i == dataList.size() +1 && dataList.get(i -1).equals("")){
                    emptyElements.add(i);
                }
            }
        }

        //check if there are some empty cells, if yes then loop though to remove them
        if(emptyElements.size() > 0){

            for(int i = emptyElements.size() -1 ; i >= 0; i--){
                int removeIt = emptyElements.get(i);
                dataList.remove(removeIt);
            }

        }

        //TEST
        Test();

        
        
    //loop through new array and manipulate data and replace it

        for(int i = 0; i < dataList.size(); i++){
            if(dataList.get(i).contains("T12") && !dataList.get(i).contains("D(") ){
                dataList.set(i, T12(dataList.get(i)));
            }
            if(dataList.get(i).contains("T24") && !dataList.get(i).contains("D(")){
                dataList.set(i, T24(dataList.get(i)));
            }
            if(dataList.get(i).contains("D(")){
                dataList.set(i, handleDate(dataList.get(i)));
            }
            if(dataList.get(i).contains("#")){
                //check if the previous symbol is * for city name:
                dataList.set(i, airportCode(dataList.get(i)));
            }
        }
        
        printFile(dataList);
    }

    public static String T12(String data){
        int number = data.indexOf("T12");
        String date = data.substring(number + 4, data.length() - 1);
        String replaceableDate =  data.substring(number, data.length());

        if (date.endsWith("Z")) {
            date = date.substring(0, date.length() - 1) + "+00:00";
        }

        ZonedDateTime zdt = ZonedDateTime.parse(date);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a (XXX)");
        String formattedDate = zdt.format(formatter);
        System.out.println(formattedDate);


        if(formattedDate.contains("(Z)")){
            formattedDate = formattedDate.replace("(Z)", "(+00:00)");
        }
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



    public static String handleDate(String data){
        String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        int number =  data.indexOf("D(");
        String replaceable = data.substring(number, data.length());
        String date = data.substring(number +2, (data.length() - 1));
        String day = date.substring(8, 10);
        String month = Months[Integer.parseInt(date.substring(5, 7)) - 1];
        // System.out.println(month);
        String year = date.substring(0, 4);
        String answer = day + " " + month + " " + year;

        return data.replace(replaceable, answer);
    }


    public static String airportCode(String data){
        // IATA # followed by three letters
        //ICAO ## followed by four letters
        int firstHashtag = data.indexOf("#");
        int secondhashtag = data.indexOf("#", firstHashtag + 2);
        String firstAirportCode = "";
        String secondAirportCode = "";
        String firstanswer = "";
        String secondanswer = "";


        //lets find if first # is IATA or ICAO code

        if(data.charAt(firstHashtag +1 ) == '#'){

            //ICAO
            //if its *airport code to city, then it hase to be firsthastag -1 to remove it.
            firstAirportCode = data.substring(firstHashtag , firstHashtag + 6 );
            
            String firstAirportCodeWithOutH = firstAirportCode.substring(2, 6);

            if(data.charAt(firstHashtag -1) == '*'){
                firstAirportCode = firstAirportCode.substring(1);
                firstanswer = readFile(firstAirportCodeWithOutH, true);
            }else{
                firstanswer =  readFile(firstAirportCodeWithOutH, false);
            }

            System.out.println(firstanswer);

        } else {
            firstAirportCode = data.substring(firstHashtag , firstHashtag + 4 );
            String firstAirportCodeWithOutH = firstAirportCode.substring(1, 4);

            if(data.charAt(firstHashtag -1) == '*'){
                firstAirportCode = firstAirportCode.substring(1);

                firstanswer = readFile(firstAirportCodeWithOutH, true);
            }else{
                firstanswer =  readFile(firstAirportCodeWithOutH, false);
            }
            // IATA

        }

        // and second #
            if(data.charAt(secondhashtag +1 ) == '#'){
                //ICAO
                secondAirportCode = data.substring(secondhashtag , secondhashtag + 6 );

                String secondAirportCodeWithOutH = secondAirportCode.substring(2, 6);
                if(data.charAt(firstHashtag -1) == '*'){
                    secondanswer = readFile(secondAirportCodeWithOutH, true);
                }else{
                    secondanswer =  readFile(secondAirportCodeWithOutH, false);
    
                }
                // FileWriter(answers);

            } else {
                //IATA

                secondAirportCode = data.substring(secondhashtag , secondhashtag + 4 );

                String secondAirportCodeWithOutH = secondAirportCode.substring(1, 4);
                if(data.charAt(firstHashtag -1) == '*'){
                    secondanswer = readFile(secondAirportCodeWithOutH, true);
                }else{
                    secondanswer =  readFile(secondAirportCodeWithOutH, false);
                }
                // FileWriter(answers);
            }
        System.out.println(firstAirportCode);

        data = data.replace(firstAirportCode, firstanswer).replace(secondAirportCode, secondanswer).replace("*", "");
        return data;
    }

    
    public static String readFile(String searchTerm, boolean isCityNameNeeded){

        String filepath = "airport-lookup.csv";
        BufferedReader reader = null;
        String line = "";

        try{
            reader = new BufferedReader(new FileReader(filepath));

            while((line = reader.readLine()) !=null){
                String[] row = line.split(",");

                for (String cell : row) {
                    if (cell.contains(searchTerm)) {
                        if(isCityNameNeeded){
                            return row[airportLookUpMunicipalityIndex];
                        }

                        return row[airportLookUpNameIndex];
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        } finally{
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void setupAirpotLookup(){

        String filePath = "airport-lookup.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){

            String firstLine = br.readLine();
            String[] values = firstLine.split(",");

            for(String value : values){
                airportLookUpOrder.add(value.trim());
            }
            //Check if airport-lookup is malformed
            if(airportLookUpOrder.size() > 6){
                System.out.println("Airport lookup malformed");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!airportLookUpOrder.contains("name")){
            System.exit(0);
        }

        airportLookUpNameIndex = airportLookUpOrder.indexOf("name");
        airportLookUpMunicipalityIndex = airportLookUpOrder.indexOf("municipality");
    }

    public static void printFile(ArrayList printRows) {
        if(airportLookupMalformed == true){
            return;
        }
        try{
        FileWriter fw = new FileWriter("output.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
            for(int i = 0; i< dataList.size(); i++){
                bw.write(dataList.get(i));
                bw.newLine();
            }
            bw.close();
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<String> Test() {
        ArrayList<String> processedLines = new ArrayList<>();
    
        Pattern dPattern = Pattern.compile("D\\(([^)]+)\\)");
        Pattern t12Pattern = Pattern.compile("T12\\(([^)]+)\\)");
        Pattern t24Pattern = Pattern.compile("T24\\(([^)]+)\\)");
        Pattern iataPattern = Pattern.compile("(?<!#)#([A-Z]{3})(?![A-Z])");
        Pattern icaoPattern = Pattern.compile("##([A-Z]{4})(?![A-Z])");
    
        for (String line : dataList) {

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

            Matcher iataMatcher = iataPattern.matcher(line);
            while (iataMatcher.find()) {
                String original = iataMatcher.group(0);
                String code = iataMatcher.group(1);
                String manipulated = airportCode(code);
                line = line.replace(original, manipulated);
            }
    
            Matcher icaoMatcher = icaoPattern.matcher(line);
            while (icaoMatcher.find()) {
                String original = icaoMatcher.group(0);
                String code = icaoMatcher.group(1);
                String manipulated = airportCode(code);
                line = line.replace(original, manipulated);
            }
            processedLines.add(line);
        }
        System.out.println(processedLines);
        return processedLines;
    }
}
