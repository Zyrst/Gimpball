package Server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Server {

	private DatagramSocket m_socket;
	private static int m_port = 4444;
	private int currentPlayers = 0;
	private static Map<ClientConnection, Client> m_Clients = new ConcurrentHashMap();
	
	public static void main(String[] args) {
		try {
			Server instance = new Server(m_port);
			instance.listenForMessage();
		} catch (IOException e) {
			System.err.println("Not able to make a server");
			e.printStackTrace();
		}
	}
	
	Server(int port) throws SocketException {
		m_socket = new DatagramSocket(port);
	}
	
	void listenForMessage() {
		//TODO Listen for stuff and update stuff and send stuff
		// Receive message
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			System.err.println("Not able to receive packet");
			e.printStackTrace();
		}
		String msg = buf.toString().trim();
		//TODO Think about DC stuff later
		//TODO Create new socket for input and make a thread
		//TODO Return the input socket for the client
		switch(msg.charAt(0)){
			case '0' :
			//add a client send back input channel
				ClientConnection clientConnection = new ClientConnection(packet.getPort(), packet.getAddress());
				if(currentPlayers < 4){
					if(!m_Clients.containsKey(clientConnection)){
						Client client = new Client(clientConnection);
						m_Clients.put(clientConnection, client);
						currentPlayers++;
					}
				}
				
				// Create and send response message
				buf = new byte[8];
				if(m_Clients.containsKey(clientConnection)){
					// Client exists in map
					buf[0] = 1;
				}
				else{
					// Client does not exists in map
					buf[0] = 0;
				}
				packet = new DatagramPacket(buf, buf.length,packet.getAddress(), packet.getPort());
				try {
					m_socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Not able to send return message on handshake");
				}
				break;
				
			case '1' :
				//Disconnect a user removing him and stuff
				break;
		}
	}
}
