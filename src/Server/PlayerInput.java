package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInput implements Runnable {
	private DatagramSocket m_socket;
	private ConcurrentHashMap<ClientConnection, Client> m_Clients;
	
	PlayerInput(DatagramSocket socket, ConcurrentHashMap<ClientConnection, Client> clients){
		m_socket = socket;
		m_Clients = clients;
	}
	
	@Override
	public void run() {
		// TODO Make a new thread which takes a player input
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		int left, right,forward,brake = 0;
		while(true){
			try {
				m_socket.receive(packet);
			} catch (IOException e) {
				System.err.println("Not able to receive packet");
				e.printStackTrace();
			}
			
			/*left = buf [0], right [1], forward[2], brake[3] */
			left = buf[0];
			right = buf[1];
			forward = buf[2];
			brake = buf[3];
			Client client = m_Clients.get(new ClientConnection(packet.getPort(),packet.getAddress()));
			
		}
		
	}

}
