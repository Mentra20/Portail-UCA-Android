package serveur;

import static org.junit.jupiter.api.Assertions.*;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.protocol.Packet;

import debug.Debug;
import metier.Student;
import metiermanager.semesters.SemestersSample;

public class 
ServerUtilityTest 
{
	private ArrayList<Client> list;
	private Client client;
	private Client clientInvalid;
	
	private Student etu;
	private Student etuInvalid;
	private SocketIOClient csock;
	private SocketIOClient csockInvalid;
	
	public SocketIOClient makeCSock ()
	{
		return new SocketIOClient() {
			@Override
			public void set(String key, Object val) {}
			@Override
			public boolean has(String key) {return false;}
			@Override
			public <T> T get(String key) {return null;}
			@Override
			public void del(String key) {}
			@Override
			public void sendEvent(String name, Object... data) {}
			@Override
			public void send(Packet packet) {}
			@Override
			public void disconnect() {}
			@Override
			public void sendEvent(String name, AckCallback<?> ackCallback, Object... data) {}
			@Override
			public void send(Packet packet, AckCallback<?> ackCallback) {}
			@Override
			public void leaveRoom(String room) {}
			@Override
			public void joinRoom(String room) {}
			@Override
			public boolean isChannelOpen() {return false;}
			@Override
			public Transport getTransport() {return null;}
			@Override
			public UUID getSessionId() {return null;}
			@Override
			public SocketAddress getRemoteAddress() {return null;}
			@Override
			public SocketIONamespace getNamespace() {return null;}
			@Override
			public HandshakeData getHandshakeData() {return null;}
			@Override
			public Set<String> getAllRooms() {return null;}
		};
	}
	
	@BeforeEach
	public void
	init ()
	{
		list = new ArrayList<Client>();
		
		etu = new Student("etu1");
		etuInvalid = new Student("etu2");
		
		csock = makeCSock();
		csockInvalid = makeCSock();
		
		client = new Client(etu, csock);
		clientInvalid = new Client(etuInvalid, csockInvalid);
		list.add(client);
	}
	
	@Test
	public void
	testGetStudentFromList ()
	{
		Student etu = ServerUtility.getStudentFromList(csock, list);
		assertEquals(etu.getNom(), "etu1");
		
		etu = ServerUtility.getStudentFromList(csockInvalid, list);
		assertEquals(etu, null);
	}
	
	@Test
	public void
	testGetSocketFromList ()
	{
		SocketIOClient c = ServerUtility.getSocketFromList(etu, list);
		assertTrue(csock.equals(c));
		
		c = ServerUtility.getSocketFromList(etuInvalid, list);
		assertFalse(csock.equals(c));
	}
	
	@Test
	public void
	testGetClientFromSocketOnList ()
	{
		Client c = ServerUtility.getClientFromSocketOnList(csock, list);
		assertTrue(c.equals(client));
		assertFalse(c.equals(clientInvalid));
		assertNull(ServerUtility.getClientFromSocketOnList(csockInvalid, list));
	}
	
	@Test
	public void
	testGetListOfSemestersJSONed ()
	{
		String expected = "[null,null,null,null]";
		assertTrue(expected.equals(ServerUtility.getListOfSemestersJSONed()));
	}
	
	@Test
	public void
	testGetListOfCourseTypeJSONed ()
	{
		String expected = "[null,null,null,null,null]";
		assertTrue(expected.equals(ServerUtility.getListOfCourseTypeJSONed()));
	}
}
