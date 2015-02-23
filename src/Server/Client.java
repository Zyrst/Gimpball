package Server;

public class Client {	
	private final ClientConnection m_clientConnection;
	private boolean left, right, forward,brake = false;
	
	Client(ClientConnection clientConnection){
		m_clientConnection = clientConnection;
	}
	
	//If not 1 generates a false which means the key is not pressed
	void setKeys(int[] keys){
		//If key is 1 then it's pressed 
		System.out.println("Setting keys for a client");
		left 	= (keys[0] == 1);
		right 	= (keys[1] == 1);
		forward = (keys[2] == 1);
		brake 	= (keys[3] == 1);
	}
	
}
