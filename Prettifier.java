import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Prettifier {

    public static void main(String[] args){
        String[] Months = {"Jan", "Feb", "Mar", "Apr", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        try{
            File myObj = new File("input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();

                int number =  data.indexOf("T12");
                String date = data.substring(number +4, (data.length() - 1));

                if(data.contains("T12")){
                    // System.out.println(data);
                    // System.out.println(date);
                    String time = date.substring(11, 16);

                    int hours = Integer.parseInt(date.substring(11, 13));
                    String minutes = date.substring(14, 16);
                    String hoursWithEnding = "";
                    // System.out.println(time);
                    if(hours > 12){
                        hours = hours - 12;
                        hoursWithEnding = "0" + hours + ":" + minutes+ "PM";
                    } else{
                        hoursWithEnding = "0" + hours + ":" + minutes+ "AM";
                    }

                    // System.out.println(hoursWithEnding);

                    String offset = "";  
                    String lastChar = date.substring(date.length()-1);


                    if(lastChar.equals("Z")){
                        offset = "+00:00";
                    } else if(lastChar != "Z"){
                        offset = date.substring( date.length() -6, date.length());
                    }

                    String answer = hoursWithEnding + " " + "(" + offset + ")";
                    System.out.println(answer);



                }else if (data.contains("T24")) {
                    // System.out.println(data);
                    // System.out.println(date);

                    String hours = date.substring(15, 17);
                    String minutes = date.substring(18, 20);
                    String hoursWithEnding = "";
                    // System.out.println(time);


                        hoursWithEnding = hours + ":" + minutes+ "PM";

                        hoursWithEnding =  hours + ":" + minutes+ "AM";

                    // System.out.println(hoursWithEnding);

                    String offset = "";  
                    String lastChar = date.substring(date.length()-1);


                    if(lastChar.equals("Z")){
                        offset = "+00:00";
                    } else if(lastChar != "Z"){
                        offset = date.substring( date.length() -6, date.length());
                    }

                    String answer = hoursWithEnding + " " + "(" + offset + ")";
                    System.out.println(answer);

                }else if(data.contains("D(")){
                    System.out.println(date);
                    String day = date.substring(date.length());
                    String month = "";
                    String year = "";
                    

                }else{
                    System.out.println("dont have");

                }

            }
            myReader.close();
        } catch (FileNotFoundException e){
            System.out.println("errrrror");
        }
    }

}