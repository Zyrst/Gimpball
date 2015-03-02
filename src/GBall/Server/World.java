package GBall.Server;

import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import Server.Client;
import Server.ClientConnection;

public class World {

	public static final String SERVERIP = "127.0.0.1"; // 'Within' the emulator!  
	public static final int SERVERPORT = 4444;
	
	private static int m_pollRate = 100;
	
	private static ConcurrentHashMap<ClientConnection, Client> m_clients;
	private static ConcurrentLinkedQueue<byte[]> m_messageQueue;
    
	private static class WorldSingletonHolder { 
        public static final World instance = new World();
    }

    public static World getInstance() {
        return WorldSingletonHolder.instance;
    }
	

    private double m_lastTime = System.currentTimeMillis();
    private double m_actualFps = 0.0;

    private final GameWindow m_gameWindow = new GameWindow();

    private World() {
	
	
    }

    public void process() {
	initPlayers();
	
	// Marshal the state
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DatagramSocket m_socket = new DatagramSocket();
		InetAddress m_serverAddress = InetAddress.getByName("localhost");
		ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(new MsgData());
	    oos.flush();
	    
	    byte[] buf = new byte[1024];
	    
	    buf = baos.toByteArray();
	    
	    DatagramPacket pack = new DatagramPacket(buf, buf.length, m_serverAddress, SERVERPORT);
	    m_socket.send(pack);
	    
	    
	} catch (IOException e) {
		e.printStackTrace();
	}
    
	long startTime = System.currentTimeMillis();

	while(true) {
	    if(newFrame()) {
		    EntityManager.getInstance().readKeyInput();
			EntityManager.getInstance().updatePositions();
			EntityManager.getInstance().checkBorderCollisions(Const.DISPLAY_WIDTH, Const.DISPLAY_HEIGHT);
			EntityManager.getInstance().checkShipCollisions();
			m_gameWindow.repaint();
			
			if(System.currentTimeMillis() > startTime + m_pollRate){
				//Send positions
				startTime = System.currentTimeMillis();
				LinkedList<GameEntity> entities = EntityManager.getInstance().getState();
				String all[] = new String[5];
				int counter = 0;
				for(Iterator<GameEntity> itr = entities.iterator();itr.hasNext();){
					GameEntity g = itr.next();
					String positionX = String.valueOf((float)g.getPosition().getX());
					String positionY = String.valueOf((float)g.getPosition().getY());
					
					String directionX = String.valueOf((float)g.getDirection().getX());
					String directionY = String.valueOf((float)g.getDirection().getY());
					//System.out.println(directionX + " current direction");					
					
					String speedX = String.valueOf((float)g.getSpeed().getX());
					String speedY = String.valueOf((float)g.getSpeed().getY());
					
					String acceleration = String.valueOf((float) g.getAcceleration());
					all[counter] = positionX + " " + positionY + " " + directionX + " " + directionY + " " + speedX + " " + speedY + " " + acceleration;
					counter++;
				}
				
				String message = "";
				for(int i = 0; i < 5; i++){
					if(i == 0){
						message = all[i];
					}
					else{
						message = message + "/" + all[i];
					}
				}
				message = message + "/" + ScoreKeeper.getInstance().getScore();
				byte[] byteMessage = message.getBytes();
				m_messageQueue.add(byteMessage);
			}
	    }
	    else{
			if(sleepTime() > 0){
				try {
					Thread.sleep(sleepTime());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
    } 

    private boolean newFrame() {
		double currentTime = System.currentTimeMillis();
		double delta = currentTime - m_lastTime;
		boolean rv = (delta > Const.FRAME_INCREMENT);
		if(rv) {
		    m_lastTime += Const.FRAME_INCREMENT;
		    if(delta > 10 * Const.FRAME_INCREMENT) {
			m_lastTime = currentTime;
		    }
		    m_actualFps = 1000 / delta;
		}
		return rv;
    }
    
    public long sleepTime() {
		double currentTime = System.currentTimeMillis();
		double delta = currentTime - m_lastTime;
			
		return (long) (Const.FRAME_INCREMENT - delta);
    }

    private void initPlayers() {
	// Team 1, player 1
	EntityManager.getInstance().addShip(new Vector2D(Const.START_TEAM1_SHIP1_X, Const.START_TEAM1_SHIP1_Y),
			      new Vector2D(0.0, 0.0),
			      new Vector2D(1.0, 0.0),
			      Const.TEAM1_COLOR,
			      new KeyConfig(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W)
			      );
	//Team 2 player 2
	EntityManager.getInstance().addShip(new Vector2D(Const.START_TEAM2_SHIP1_X, Const.START_TEAM2_SHIP1_Y),
			      new Vector2D(0.0, 0.0),
			      new Vector2D(-1.0, 0.0),
			      Const.TEAM2_COLOR,
			      new KeyConfig(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP)
			      );
	
	
	// Team 1, player 3
	EntityManager.getInstance().addShip(new Vector2D(Const.START_TEAM1_SHIP2_X, Const.START_TEAM1_SHIP2_Y),
			      new Vector2D(0.0, 0.0),
			      new Vector2D(1.0, 0.0),
			      Const.TEAM1_COLOR,
			      new KeyConfig(KeyEvent.VK_F, KeyEvent.VK_H, KeyEvent.VK_G, KeyEvent.VK_T)
			      );
	//Team 2, player 4
	EntityManager.getInstance().addShip(new Vector2D(Const.START_TEAM2_SHIP2_X, Const.START_TEAM2_SHIP2_Y),
			      new Vector2D(0.0, 0.0),
			      new Vector2D(-1.0, 0.0),
			      Const.TEAM2_COLOR,
			      new KeyConfig(KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_K, KeyEvent.VK_I)
			      );
	
	// Ball
	EntityManager.getInstance().addBall(new Vector2D(Const.BALL_X, Const.BALL_Y), new Vector2D(0.0, 0.0));
    }

	public double getActualFps() {
		
		return m_actualFps;
	}
	
    public void addKeyListener(KeyListener k) {
    	m_gameWindow.addKeyListener(k);
    }
    
    public void setClientMap(ConcurrentHashMap<ClientConnection, Client> clients){
    	m_clients = clients;
    }
    
    public ConcurrentHashMap<ClientConnection, Client> getClientMap(){
    	return m_clients;
    }
    
    public void setMessageQueue(ConcurrentLinkedQueue<byte[]> queue){
    	m_messageQueue = queue;
    }
}