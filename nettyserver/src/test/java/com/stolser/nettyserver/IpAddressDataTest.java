package com.stolser.nettyserver;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.stolser.nettyserver.server.data.IpAddressData;

public class IpAddressDataTest {
	private IpAddressData ip;
	
	@Before
	public void setUp(){
		ip = new IpAddressData(new InetSocketAddress(8080));
	}

	@Test
	public void addUniqueRequestsTested() throws URISyntaxException {
		ip.addUniqueRequest(new URI("example.com"));
		ip.addUniqueRequest(new URI("google.com.ua"));
		boolean result = ip.addUniqueRequest(new URI("google.com.ua"));
		assertFalse(result);
		
		result = ip.addUniqueRequest(new URI("localhost"));
		assertTrue(result);
		
		ip.addUniqueRequest(new URI("example.com"));
		assertEquals(3, ip.getUniqueRequests().size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addUniqueRequestsWhenNullPassed() {
		ip.addUniqueRequest(null);
	}
	
	@Test
	public void containsTested() throws URISyntaxException {
		ip.addUniqueRequest(new URI("example.com"));
		
		assertTrue(ip.contains(new URI("example.com")));
		assertFalse(ip.contains(new URI("example.com.ua")));
	}
	

}
