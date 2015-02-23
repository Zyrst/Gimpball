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
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		while(true){
			try {
				m_socket.receive(packet);
			} catch (IOException e) {
				System.err.println("Not able to receive packet");
				e.printStackTrace();
			}
			System.out.println("Wooh received a package containing keys");
			//TODO Make sure that the value of a buff is 0 or 1 and not 49 or something else
			/*left = buf [0], right [1], forward[2], brake[3] */
			int[] keys = {0,0,0,0};
			//TODO Do something with the input, server listens to this instead of keyboard
			for(int i = 0; i < 4; i++){
				keys[i] = buf[i];
			}
			if(m_Clients.containsKey(new ClientConnection(packet.getPort(),packet.getAddress()))){
				Client client = m_Clients.get(new ClientConnection(packet.getPort(),packet.getAddress()));
				client.setKeys(keys);
				System.out.println("Set keys");
			}
			else{
				System.err.println("Did not exist in Client Map");
			}
			
			
		}	
	}
}
