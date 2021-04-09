import java.io.File;
import java.util.Scanner;

public class AnagramsProcessor
{
  public static void main(String[] args) throws Exception
  {
	  int count = 0;
    // pass the path to the file as a parameter
    File file =
      new File("anagram.txt");
    Scanner sc = new Scanner(file);
  
    while (sc.hasNextLine()) {
      System.out.println(sc.nextLine());
    count = count +1;
  }
  System.out.println(count);
   
  }
}