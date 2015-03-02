package Server;

public class Client {	
	private final ClientConnection m_clientConnection;
	private boolean left, right, forward,brake = false;
	private int m_shipID;
	
	Client(ClientConnection clientConnection, int ship){
		m_clientConnection = clientConnection;
		m_shipID = ship;
	}
	
	public int getShipID() {
		return m_shipID;
	}
	//If not 1 generates a false which means the key is not pressed
	public void setKeys(int[] keys){
		//If key is 1 then it's pressed 
		left 	= (keys[0] == 1);
		right 	= (keys[1] == 1);
		forward = (keys[2] == 1);
		brake 	= (keys[3] == 1);
	}
	
	public boolean[] getKeys(){
		boolean[] keys = {left, right, forward, brake};
		return keys;
	}
	
}
