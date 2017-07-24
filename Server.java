import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is the server program for our chat application
 * This consisits of multiple threads
 * The main thread keeps listening for new connections and makes a new thread everytime a new client connects
 * The otehr thread listens to the client for incoming messages n upon receival of every message,
 * 		broadcasts the message to all other clients
 * We maintain an ArrayList of clients
 * @author shashank
 */
public class Server {
	public static final int PORT = 8080;
	
	public static ArrayList<Socket> clients;
	private static ServerSocket server;
	
	// Give an intro about the Server upon start up
	public static final String INTRO = "Welcome to the new server created by Shashank\n"
			+ "Just type your message and hit enter\n";
	public static final String COMMANDS = "Following are the command options\n"
			+ "@users : List all current users conected\n"
			+ "@commands : Get a list of all availaible commands";
	public static final String stars = "\n************************\n";
	
	// broadcast message to everyone except this client
	public void sendToAllExcept(String message, Socket skipClient) {
		Iterator<Socket> it = clients.iterator();
		while(it.hasNext()) {
			Socket client = (Socket) it.next();
			if(client.equals(skipClient) != true) 
				sendMessage(message, client);
		}
	}
	
	// Send message to this particular client
	public void sendMessage(String message, Socket client) {
		try {
			PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
			writer.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void go() {
		try {
			server = new ServerSocket(PORT);
			System.out.println("Server created at port " + PORT);
			while(true) {
				Socket client = server.accept();
				clients.add(client);
				System.out.println("Client connected- " + client.getInetAddress() + ":" + client.getPort());
				
				Thread handleClient = new Thread(new HandleClient(client));
				handleClient.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		clients = new ArrayList<>();
		Server obj = new Server();
		obj.go();
	}
	
	/**
	 * This class is created for every client
	 * It listens to teh client n upon receving a message calls the sendMessage() to broadcast it to all clients
	 * @author shashank
	 */
	public class HandleClient implements Runnable{
		Socket client;
		private BufferedReader reader;
		
		public HandleClient(Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			// Send intro message with commands to client
			sendMessage(stars + INTRO + COMMANDS + stars, client);
			try {
				reader = new BufferedReader(
							new InputStreamReader(
									client.getInputStream()
									)
							);
				String message;
				while((message = reader.readLine()) != null) {
					System.out.println(client.getInetAddress() + ":" + client.getPort() + "-> " + message);
					
					// Check if msg received is a command. If yes then return teh answer
					if(message.charAt(0) == '@') {
						Commands commands = new Commands();
						String reply = commands.getReply(message.substring(1)); // remove @ from command
						sendMessage(reply, client);
						continue;
					}
					
					message = client.getInetAddress() + ":" + client.getPort() + "-> " + message;
					// Message received, now send it to all clients
					sendToAllExcept(message, client);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
