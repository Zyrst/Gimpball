package Server;



import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.Queue;

import GBall.Server.World;

public class Server implements Runnable {

	//General communication , join / dc other commands
	private static DatagramSocket m_socket;
	//Specific communication about the client, keys and return all positions
	private static DatagramSocket m_clientSocket;
	private static int m_port = 4444;
	private static ConcurrentHashMap<ClientConnection, Client> m_clients = new ConcurrentHashMap();
	private static ConcurrentLinkedQueue<byte[]> m_messageQueue = new ConcurrentLinkedQueue<byte[]>();
	
	public static void main(String[] args) {
		Thread instance = new Thread(new Server(m_port));
		instance.start();
		
		Thread input = new Thread(new PlayerInput(m_clientSocket, m_clients));
		input.start();
		
		Thread output = new Thread(new OutputThread(m_messageQueue, m_clients, m_clientSocket));
		output.start();
		
		World.getInstance().setClientMap(m_clients);
		World.getInstance().process();
	}
	
	Server(int port) {
		try {
			m_socket = new DatagramSocket(port);
			m_clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.err.println("Not able to bind server sockets");
		} 
		System.out.println("Client port: " + m_clientSocket.getLocalPort());
	}
	
	@Override
	public void run() {
		// Receive message
		while(true){
		
			byte[] buf = new byte[24];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				m_socket.receive(packet);
			} catch (IOException e) {
				System.err.println("Not able to receive packet");
				e.printStackTrace();
			}
			System.out.println("Received a message");
			//TODO Think about DC stuff later
			switch(buf[0]){
				case '0' :
				//add a client send back input channel
					ClientConnection clientConnection = new ClientConnection(packet.getPort(), packet.getAddress());
					if(m_clients.size() < 4){
						if(!m_clients.containsKey(clientConnection)){
							System.out.println("Added a client");
							Client client = new Client(clientConnection,(m_clients.size()));
							m_clients.put(clientConnection, client);
						}
					}
					
					// Create and send response message
					buf = new byte[24];
					if(m_clients.containsKey(clientConnection)){
						// Client exists in map
						System.out.println("Size of clients: " + (m_clients.size() - 1));
						String msg = "1 " + m_clientSocket.getLocalPort() + " " + (m_clients.size() - 1);
						buf = msg.getBytes();
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
}
