import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Prettifier {

    public static void main(String[] args){

        //arguments
        for (String s: args){
            System.out.println(s);
        }
        System.out.println(args.length);
        String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        try{
            File myObj = new File("input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();
                data = data.trim();



                if(data.contains("T12") && !data.contains("D(")){

                    // System.out.println(data);
                    // System.out.println(date);
                    int number =  data.indexOf("T12");
                    String date = data.substring(number +4, (data.length() - 1));


                    

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



                }else if (data.contains("T24") && !data.contains("D(")) {
                    int number =  data.indexOf("T24");
                    String date = data.substring(number +4, (data.length() - 1));
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
                    System.out.println(answer);

                }else if(data.contains("D(")){
                    int number =  data.indexOf("D(");

                    String date = data.substring(number +2, (data.length() - 1));
                    String day = date.substring(8, 10);

                    String month = Months[Integer.parseInt(date.substring(5, 7)) - 1];
                    // System.out.println(month);
                    String year = date.substring(0, 4);


                    String answer = day + " " + month + " " + year;
                    System.out.println(answer);

                
                }else if(data.contains("#")  ){
                    airpotCode(data);



                }else{
                    System.out.println("dont have");

                }

            }
            myReader.close();
        } catch (FileNotFoundException e){
            System.out.println("errrrror");
        }
    }

    public static void airpotCode(String data){
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

            firstAirportCode = data.substring(firstHashtag +2, firstHashtag + 6 );

        } else {
            // IATA

            firstAirportCode = data.substring(firstHashtag +1, firstHashtag + 4 );

        }
        // and second #
        if(data.charAt(secondhashtag +1 ) == '#'){
            //ICAO


            secondAirportCode = data.substring(secondhashtag +2, secondhashtag + 6 );

        } else {
            //IATA


            secondAirportCode = data.substring(secondhashtag +1, secondhashtag + 4 );
        }

        System.out.println(firstAirportCode);
        System.out.println(secondAirportCode);




  

    }

}