package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputThread implements Runnable {
	private static ConcurrentLinkedQueue<byte[]> m_messageQueue;
	private static ConcurrentHashMap<ClientConnection, Client> m_clients;
	private static DatagramSocket m_socket;
	
	OutputThread(ConcurrentLinkedQueue<byte[]> messageQueue, ConcurrentHashMap<ClientConnection, Client> clients, DatagramSocket socket ){
		m_messageQueue = messageQueue;
		m_clients = clients;
		m_socket = socket;
	}
	
	//Send a message to all clients using the keyset of the hashmap
	@Override
	public void run(){
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
			m_messageQueue.remove();
		}
	}
}
