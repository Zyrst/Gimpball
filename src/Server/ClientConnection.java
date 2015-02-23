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
	
	@Override
	public int hashCode(){
		int hashCode = 1;
		
		hashCode = hashCode * 37 + this.m_port;
		hashCode = hashCode * 37 + this.m_address.hashCode();
		
		return hashCode;
	}
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof ClientConnection)){
			System.out.println("Not same type");
			return false;
		}
		
		ClientConnection that = (ClientConnection) other;
		
		return this.m_port == that.m_port 
				&& this.m_address.equals(that.m_address);
	}
}
