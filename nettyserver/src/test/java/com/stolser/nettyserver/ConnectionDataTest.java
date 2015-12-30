package com.stolser.nettyserver;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Test;

import com.stolser.nettyserver.server.data.ConnectionData;

public class ConnectionDataTest {

	@Test
	public void equalsTested() throws URISyntaxException {
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
		URI uri = new URI("example.com");
		Date timestamp = new Date();
		ConnectionData conn1 = new ConnectionData(address, uri, timestamp);
		ConnectionData conn2 = new ConnectionData(address, uri, timestamp);
		ConnectionData conn3 = new ConnectionData(address, uri, timestamp);
			
		assertTrue(conn1.equals(conn1));
		assertFalse(conn1.equals(conn2));
		assertEquals(3, new HashSet(Arrays.asList(conn1, conn2, conn3, conn3)).size());
				
	}

}
