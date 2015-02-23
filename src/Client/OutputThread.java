package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import GBall.World;
import GBall.EntityManager;

public class OutputThread implements Runnable {
	private static DatagramSocket m_socket;
	private static int m_shipNumber;
	private static World m_world;
	private static int m_gamePort;
	private static InetAddress m_serverAddress;
	private static EntityManager m_entities;
	
	OutputThread(DatagramSocket socket, int shipNumber, int port, InetAddress address){
		System.out.println("Started a client output thread");
		m_socket = socket;
		m_shipNumber = shipNumber;
		m_world = World.getInstance();
		m_gamePort = port;
		m_serverAddress = address;
		m_entities = EntityManager.getInstance();
	}
	
	@Override
	public void run(){
		while(true){
			boolean[] keys;
			keys = m_entities.getShipKeys(m_shipNumber);
			//TODO Fix byte size
			byte[] buf = new byte[48];
			for(int i = 0; i < keys.length; i++){
				if(keys[i] == true){
					buf[i] = 1;
				}
				else{
					buf[i] = 0;
				}
			}
			DatagramPacket packet = new DatagramPacket(buf,buf.length,m_serverAddress, m_gamePort);
			try {
				m_socket.send(packet);
			} catch (IOException e1) {
				System.err.println("Unable to send my package");
				e1.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Did not want to sleep output thread");
				e.printStackTrace();
			}
		}
	}
}
