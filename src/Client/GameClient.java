package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import GBall.World;

public class GameClient {
	//TODO Input and players positions and ball position
	private static DatagramSocket m_socket;
	private static int m_serverPort = 4444;
	private static int m_gamePort = -1;
	private static InetAddress m_serverAddress; 
	private static String m_hostName = null;
	private static int m_shipNum = -1;
	
	public static void main(String[] args) {
		m_hostName = args[0];
		try {
			m_serverAddress = InetAddress.getByName(m_hostName);
			m_socket = new DatagramSocket();
			System.out.println("Socketport: " + m_socket.getLocalPort());
		} catch (SocketException | UnknownHostException e) {
			System.err.println("Unable to receive server adress or create new socket");
			e.printStackTrace();
		}
		
		//Time to shake some hands
		handshake();
		
		//Send keyinput
		Thread output = new Thread(new OutputThread(m_socket,m_shipNum, m_gamePort, m_serverAddress));
		output.start();
		
		World.getInstance().process();
	
	}
	
	private static void handshake(){
		DatagramPacket packet;
		
		byte[] buf = new byte[24];
		
		buf = "0".getBytes();
		packet = new DatagramPacket(buf, buf.length, m_serverAddress, m_serverPort);
		try {
			m_socket.send(packet);
		} catch (IOException e) {
			System.err.println("Not able to send handshake message");
			e.printStackTrace();
		}
		System.out.println("Sent a message and waiting");
		
		buf = new byte[24];
		packet = new DatagramPacket(buf, buf.length);
		try {
			m_socket.receive(packet);
		} catch (IOException e) {
			System.err.println("Not able to receive handshake");
			e.printStackTrace();
		}
		System.out.println("Received an answer");
		String[] x = new String(buf, 0, buf.length).trim().split(" ", 3);
		
		System.out.println("Which is " + x[0] + " port number: " + x[1]);
		if(x[0].equals("1")){
			System.out.println("Able to join server");
			
			int tmpPort = Integer.parseInt(x[1]);
			m_gamePort = tmpPort;
			int tmpShip = Integer.parseInt(x[2]);
			m_shipNum = tmpShip;
			System.out.println(tmpPort);
		}
		else{
			System.out.println("Unable to join server");
			System.exit(1);
		}
	}
}
