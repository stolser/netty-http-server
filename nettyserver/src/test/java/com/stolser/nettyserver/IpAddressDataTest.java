package com.stolser.nettyserver;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
		ip.addRequestIfUnique(new String("example.com"));
		ip.addRequestIfUnique(new String("google.com.ua"));
		boolean result = ip.addRequestIfUnique(new String("google.com.ua"));
		assertFalse(result);
		
		result = ip.addRequestIfUnique(new String("localhost"));
		assertTrue(result);
		
		ip.addRequestIfUnique(new String("example.com"));
		assertEquals(3, ip.getUniqueRequests().size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addUniqueRequestsWhenNullPassed() {
		ip.addRequestIfUnique(null);
	}
	
	@Test
	public void containsTested() throws URISyntaxException {
		ip.addRequestIfUnique(new String("example.com"));
		
		assertTrue(ip.contains(new String("example.com")));
		assertFalse(ip.contains(new String("example.com.ua")));
	}
	

}
