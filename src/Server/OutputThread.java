package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import GBall.Server.Const;
import GBall.Server.GameEntity;
import GBall.Server.World;

public class OutputThread implements Runnable {
	private static ConcurrentLinkedQueue<byte[]> m_messageQueue;
	private static ConcurrentHashMap<ClientConnection, Client> m_clients;
	private static DatagramSocket m_socket;
	
	OutputThread(ConcurrentLinkedQueue<byte[]> messageQueue, ConcurrentHashMap<ClientConnection, Client> clients, DatagramSocket socket ){
		System.out.println("Server outputThread running");
		m_messageQueue = messageQueue;
		m_clients = clients;
		m_socket = socket;
	}
	
	//Send a message to all clients using the keyset of the hashmap
	@Override
	public void run(){
		while(true){
			if(!m_messageQueue.isEmpty()){
				for( ClientConnection clients : m_clients.keySet()){
					int port = clients.getPort();
					InetAddress address = clients.getAddress();
					DatagramPacket packet = new DatagramPacket(m_messageQueue.element(), m_messageQueue.element().length, address, port);
					try {
						m_socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println("Unable to send packet, server side in output thread");
					}
				}
				
				String message = new String(m_messageQueue.element(), 0, m_messageQueue.element().length);
				
				String[] splinter = message.split("/", 5);
				
				if(5 != splinter.length){
					System.out.println("Size of splinter: " + splinter.length);
					System.err.println("Splinter wrong size");
				}
				
				String[] values = splinter[0].split(" ", 7);
				if(values.length != 7){
					System.out.println("Size of Values: " + values.length);
					System.err.println("Values wrong size");
				}
				
				m_messageQueue.remove();
			}
			
			long sleepTime = World.getInstance().sleepTime();
			
			if(sleepTime > 0){
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
