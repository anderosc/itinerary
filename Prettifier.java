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

                if(data.contains("T12")){
                    System.out.println("yess");
                    // System.out.println(data);

                    int number =  data.indexOf("T12");
                    String date = data.substring(number +4, (data.length() - 1));
                    System.out.println(date);


                }else if (data.contains("T24")) {
                    System.out.println("yess");
                    int number =  data.indexOf("T24");
                    String date = data.substring(number +4, (data.length() - 1));
                    System.out.println(date);
                }else{
                    System.out.println("dont have");


                }

            }
            myReader.close();
        } catch (FileNotFoundException e){
            System.out.println("errrrror");
        }
    }

    public static void date(String isodate){

    }

}