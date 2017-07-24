import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This program serves as the client of our chat application.
 * This makes 2 threads
 * The 1st thread(main) does the job of sending new messages to the server
 * The 2nd thread listens to the server for incoming messages
 * @author shashank
 *
 */
public class Client {
	public static final String SERVERIP = "localhost";
	public static final int PORT = 8080;

	private BufferedReader reader;
	private Socket chatSocket;
	private PrintWriter writer; 
	
	public void setupNetworking() {
		try {
			chatSocket = new Socket(SERVERIP, PORT);
			writer = new PrintWriter(chatSocket.getOutputStream(), true);
			reader = new BufferedReader(
						new InputStreamReader(
								chatSocket.getInputStream()
						)
					 );
			System.out.println("Connected with server at " + SERVERIP + ":" + PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void go() {
		String message = "";
		Scanner scan = new Scanner(System.in);
		
		// Create the thread for accepting messages from server
		Thread incomingThread = new Thread(new HandleIncoming());
		incomingThread.start();
		
		// Now we want the server to display its welcome msg first, so we ask the current thread to wait
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Now send messages to server
		System.out.println("Enter 'stop' to close the client");
		while(message.equals("stop") != true) {
			message = scan.nextLine();
			writer.println(message);
		}
		scan.close();
	}
	
	public static void main(String[] args) {
		Client obj = new Client();
		obj.setupNetworking();
		obj.go();
	}
	
	/**
	 * This class contains the thread for handling new incoming messages from teh server
	 * @author shashank
	 */
	public class HandleIncoming implements Runnable{
		@Override
		public void run() {
			String message = "";
			try {
				while((message = reader.readLine()) != null) {
					System.out.println(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
