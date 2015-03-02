package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import GBall.Client.EntityManager;
import GBall.Client.GameEntity;
import GBall.Client.ScoreKeeper;
import GBall.Client.World;

public class InputThread implements Runnable{
	private static DatagramSocket m_socket;
	private static World m_world;
	private static EntityManager m_entities;
	
	InputThread(DatagramSocket socket){
		System.out.println("Started a client input thread");
		m_socket = socket;
		m_world = World.getInstance();
		m_entities = EntityManager.getInstance();
	}
	//TODO Make sure message comes from server and not from another server
	//Evaluate its the same gamePort as packetPort if not discard 
	@Override
	public void run(){
		while(true){
			byte[] buff = new byte[1024];
			
			DatagramPacket packet = new DatagramPacket(buff, buff.length);
			
			try {
				m_socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String message = new String(buff, 0, buff.length).trim();
			
			String[] splinter = message.split("/", 5);
			int team1Score = Integer.parseInt(splinter[4].split("/", 3)[1]);
			int team2Score = Integer.parseInt(splinter[4].split("/", 3)[2]);
			
			ScoreKeeper.getInstance().setScores(team1Score, team2Score);
			
			splinter[4] = splinter[4].split("/", 3)[0];
			Vector<GameEntity> entities = m_entities.getState();
			
			if(m_entities.getState().size() != splinter.length){
				System.out.println("Size of entities: " + m_entities.getState().size());
				System.out.println("Size of splinter: " + splinter.length);
				System.err.println("Entities and splinter not the same size");
			}
			if(splinter.length == entities.size()){
				for(int i = 0; i < splinter.length; i++){
					String[] values = splinter[i].split(" ", 7);
					if(values.length == 7){
						entities.get(i).setPosition(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
						entities.get(i).setDirection(Double.parseDouble(values[2]), Double.parseDouble(values[3]));
						entities.get(i).setSpeed(Double.parseDouble(values[4]), Double.parseDouble(values[5]));
						entities.get(i).setAcceleration(Double.parseDouble(values[6]));
					}
				}
			}
			else{
				System.err.println("Not same sizes");
			}
		}
	}
}
