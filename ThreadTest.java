import java.io.File;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
	This class read files and outputs the number of lowercase, uppercase, and digit characters
	with three separate threads.
*/

public class ThreadTest {

	private static volatile boolean needToRead = true;
	private static volatile boolean needToProcess = true;
	private static BlockingQueue<File> fileQ = new LinkedBlockingQueue<>();
	private static BlockingQueue<String> textQ = new LinkedBlockingQueue<>();
	private static int[] counts = new int[3];
	//	[0]			[1]			[2]
	//	lowercase	UPPERCASE 	0-9

	public static void main(String[] args){

		Runnable fileRead = new Runnable() {
			public void run(){
				fileRead();
			}
		};

		Runnable processing = new Runnable() {
			public void run(){
				process();
			}
		};

		new Thread(fileRead).start();
		new Thread(processing).start();

		//main Thread will do I/O
		String command = "command";
		Scanner kb = new Scanner(System.in);
		while(!command.equals("exit")){
			System.out.print("COMMAND > ");
			command = kb.nextLine();
			String[] commandList = command.split(" ");
			switch(commandList[0]){
				case"read":
					File file = new File(commandList[1]);
					if(file.exists() && file.isFile()){
						fileQ.add(file);
						System.out.println("File added to queue.");
					}
					else
						System.out.println("File either does not exist or is not a normal file.");
					break;
				case"counts":
					System.out.printf("%15s|%15s|%15s\n%15d|%15d|%15d\n",
									  "lowercase", "UPPERCASE", "0-9",
									  counts[0], counts[1], counts[2]);
					break;
				case"exit":
					needToRead = false;
					needToProcess = false;
					System.exit(0);
					break;
				default:
					System.out.println("Invalid command!");
			}
		}
	}

	private static void fileRead(){
		Scanner fileScanner;
		while(needToRead){
			try {
				fileScanner = new Scanner(fileQ.take());
				while(fileScanner.hasNext())
					textQ.put(fileScanner.nextLine());
				fileScanner.close();
			}
			catch(Exception e){
			}
		}	
	}

	private static void process(){
		while(needToProcess){
			try{
				String line = textQ.take();
				char[] text = line.toCharArray();
				for(int i = 0; i < text.length; i++){
					if(Character.isLowerCase(text[i]))
						counts[0]++;
					else if(Character.isUpperCase(text[i]))
						counts[1]++;
					else if(Character.isDigit(text[i]))
						counts[2]++;
				}
			}
			catch(Exception e){
			}
		}
	}

}
