package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameClient {
	//TODO Input and players positions and ball position
	private static DatagramSocket m_socket;
	private static int m_serverPort = 4444;
	private static int m_gamePort = -1;
	private static InetAddress m_serverAddress; 
	private static String hostName = null;
	
	public static void main(String[] args) {
		hostName = args[0];
		try {
			m_serverAddress = InetAddress.getByName(hostName);
			m_socket = new DatagramSocket();
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			handshake();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to do the handshake");
			e.printStackTrace();
		}
	}
	
	private static void handshake() throws IOException{
		DatagramPacket packet;
		
		byte[] buf = new byte[24];
		
		buf = "0".getBytes();
		packet = new DatagramPacket(buf, buf.length, m_serverAddress, m_serverPort);
		m_socket.send(packet);
		System.out.println("Sent a message and waiting");
		
		buf = new byte[24];
		packet = new DatagramPacket(buf, buf.length);
		m_socket.receive(packet);
		System.out.println("Received an answer");
		String[] x = new String(buf, 0, buf.length).split(" ", 2);
		
		System.out.println("Which is " + x[0] );
		if(x[0].equals("1")){
			System.out.println("Wooh joined server, TAJM 2 GEME");
			
			int tmpPort = Integer.parseInt(x[1]);
			System.out.println(tmpPort);
		}
		else{
			System.out.println("Unable to join server");
			System.exit(1);
		}
		while(true){
			
		}
	}
}
