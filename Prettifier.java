import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Prettifier {

    public static ArrayList<String> dataList = new ArrayList<String>();
    public static ArrayList<Integer> emptyElements = new ArrayList<Integer>();

    public static ArrayList<Integer> printToFileElements = new ArrayList<Integer>();

    public static boolean airportLookupMalformed = false;




    
    public static void main(String[] args){

        //arguments
        ArrayList<String> argsList = new ArrayList<String>();
        for (String s: args){
            System.out.println(s);
            argsList.add(s);
        }
        if(args.length < 3){
            return;
        }
        if(!argsList.contains("./input.txt") || !argsList.contains("./output.txt") || !argsList.contains("./airports_lookup.csv")){
            return;
        }

        //read every line and put it in arraylist
        try{
            File myObj = new File("input.txt");
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
                System.out.println("its working");

                if(dataList.get(i+1).equals("")){
                System.out.println("its working too");

                    emptyElements.add(i);
                }
            }else{
                System.out.println("Nothing");

            }
        }

        //check if there are some empty cells, if yes then loop though to remove them
        if(emptyElements.size() > 0){
            for(int i = 0; i < emptyElements.size(); i++){
                int removeIt = emptyElements.get(i);
                dataList.remove(removeIt);
            }
        }

    //loop through new array and manipulate data and replace it

        for(int i = 0; i < dataList.size(); i++){
            if(dataList.get(i).contains("T12") && !dataList.get(i).contains("D(") ){
                dataList.set(i, T12(dataList.get(i)));
            }
            if(dataList.get(i).contains("T24") && !dataList.get(i).contains("D(")){
                dataList.set(i, T24(dataList.get(i)));
            }
            if(dataList.get(i).contains("D(")){
                dataList.set(i, Date(dataList.get(i)));
            }
            if(dataList.get(i).contains("#")){
                dataList.set(i, null);
            }
        }
        System.out.println(dataList);
    }

    public static String T12(String data){
        int number =  data.indexOf("T12");
        String date = data.substring(number +4, (data.length() - 1));
        String replaceable = data.substring(number, data.length());
        // System.out.println(replaceable);
        int hours = Integer.parseInt(date.substring(11, 13));
        String minutes = date.substring(14, 16);
        String hoursWithEnding = "";
        if(hours > 12){
            hours = hours - 12;
            hoursWithEnding = "0" + hours + ":" + minutes+ "PM";
        } else{
            String hours2 = date.substring(11, 13);
            hoursWithEnding = hours2 + ":" + minutes+ "AM";
        }
        // System.out.println(hoursWithEnding);
        String offset = "";  
        String lastChar = date.substring(date.length()-1);
        if(lastChar.equals("Z")){
            offset = "+00:00";
        } else if(!lastChar.equals("Z")){
            offset = date.substring( date.length() -6, date.length());
        }
        String answer = hoursWithEnding + " " + "(" + offset + ")" ;
        System.out.println(answer);
        System.out.println(data);
        System.out.println(date);
        String output = data.replace(replaceable, answer);
        System.out.println(output);

        return output;
    }

    public static String T24(String data){
        int number =  data.indexOf("T24");
        String date = data.substring(number +4, (data.length() - 1));
        String replaceable = data.substring(number, data.length());
        date = date.trim();
        // System.out.println(data);
        
        String offset = ""; 
        String hours = "";
        String lastChar = date.substring(date.length()-1);
        if(lastChar.equals("Z")){
            offset = "+00:00";
            hours = date.substring(11, 16);
        } else if(!lastChar.equals("Z")){
            hours = date.substring(11, 16);
            offset = date.substring( date.length() -6, date.length());
        }
        // System.out.println(time);
        // System.out.println(hoursWithEnding);
        String answer = hours + " " + "(" + offset + ")";
        String output = data.replace(replaceable, answer);

        return output;
    }

    public static String Date(String data){
        String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


        int number =  data.indexOf("D(");
        String replaceable = data.substring(number, data.length());
        String date = data.substring(number +2, (data.length() - 1));
        String day = date.substring(8, 10);
        String month = Months[Integer.parseInt(date.substring(5, 7)) - 1];
        // System.out.println(month);
        String year = date.substring(0, 4);
        String answer = day + " " + month + " " + year;
        String output = data.replace(replaceable, answer);

        return output;
    }

    public static String airportCode(String data){
        // IATA # followed by three letters
        //ICAO ## followed by four letters

        int firstHashtag = data.indexOf("#");
        // String otherHalf = data.substring(firstHashtag +1 , data.length());
        int secondhashtag = data.indexOf("#", firstHashtag + 2);

        String firstAirportCode = "";
        String secondAirportCode = "";


        //lets find if first # is IATA or ICAO code
        if(data.charAt(firstHashtag +1 ) == '#'){
            //ICAO

            firstAirportCode = data.substring(firstHashtag , firstHashtag + 6 );
            String firstAirportCodeWithOutH = firstAirportCode.substring(2, 6);
            System.out.println(firstAirportCodeWithOutH);
            String answers =  readFile(firstAirportCodeWithOutH);
            System.out.println(answers);
            // FileWriter(answers);


        } else {
            // IATA

            firstAirportCode = data.substring(firstHashtag , firstHashtag + 4 );
            String firstAirportCodeWithOutH = firstAirportCode.substring(1, 4);
            System.out.println(firstAirportCodeWithOutH);
            String answers =  readFile(firstAirportCodeWithOutH);
            System.out.println(answers);
            // FileWriter(answers);

        }
        // and second #
        if(data.charAt(secondhashtag +1 ) == '#'){
            //ICAO


            secondAirportCode = data.substring(secondhashtag , secondhashtag + 6 );

            String secondAirportCodeWithOutH = secondAirportCode.substring(2, 6);
            String answers =  readFile(secondAirportCodeWithOutH);
            System.out.println(answers);
            // FileWriter(answers);



        } else {
            //IATA


            secondAirportCode = data.substring(secondhashtag , secondhashtag + 4 );

            String secondAirportCodeWithOutH = secondAirportCode.substring(1, 4);
            String answers = readFile(secondAirportCodeWithOutH);
            // FileWriter(answers);

        }
        return null;
    }


    public static String readFile(String searchTerm){

        String filepath = "airport-lookup.csv";

        BufferedReader reader = null;

        String line = "";

        try{
            reader = new BufferedReader(new FileReader(filepath));

            while((line = reader.readLine()) !=null){
                String[] row = line.split(",");

                if(row[3].equals(searchTerm)  || row[4].equals(searchTerm)){
                    System.out.println("found it");
                    System.out.println(row[0]);

                    if(row[0].contains("�")){
                        System.out.println("Airport lookup malformed");
                        airportLookupMalformed = true;
                        break;
                    }
                    return row[0];
                    
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

  
}
