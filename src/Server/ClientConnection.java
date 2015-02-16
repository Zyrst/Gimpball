package Server;

import java.net.InetAddress;

public class ClientConnection {
	private final int m_port;
	private final InetAddress m_address;
	
	ClientConnection(int port, InetAddress address){
		m_port = port;
		m_address = address;
	}
	
	int getPort(){
		return m_port;
	}
	
	InetAddress getAddress(){
		return m_address;
	}
}
