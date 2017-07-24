import java.net.Socket;
import java.util.Iterator;

/**
 * This class contains the functions for several commands that clients might give to server
 */
public class Commands {

	public String getReply(String command) {
		String reply;
		switch (command) {
		case "users":
			reply = getUsers();
			break;
		case "commands":
			reply = getCommands();
			break;
		default:
			reply = "Invalid Command\n";
		}

		return reply;
	}

	public String getCommands() {
		return Server.COMMANDS + "\n";
	}

	public String getUsers() {
		String users = "Following are the conencted users:\n";
		Iterator<Socket> it = Server.clients.iterator();
		while (it.hasNext()) {
			Socket client = (Socket) it.next();
			users = users + client.getInetAddress() + ":" + client.getPort() + "\n";
		}

		return users;
	}
}