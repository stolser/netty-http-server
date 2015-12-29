package com.stolser.nettyserver;

import static org.junit.Assert.*;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.stolser.nettyserver.server.NettyHttpServer;

public class NettyHttpServerTest {
 
	@Test
	public void testServerWithDefaultAllParams() {
		final int defaultPort = 8080;
		final Class<? extends ServerChannel> defaultTransport = NioServerSocketChannel.class;
		NettyHttpServer server = NettyHttpServer.newBuilder().build();
		
		int expectedPortNumber = defaultPort;
		int actualPortNumber = server.getPortNumber();
		assertEquals(expectedPortNumber, actualPortNumber);

		Class<? extends ServerChannel> expectedTransport = defaultTransport;
		Class<? extends ServerChannel> actualTransport = server.getTransport();
		assertEquals(expectedTransport, actualTransport);
		
		String expectedServerToString = "NettyHttpServer: port = " + defaultPort;
		String actualServerToString = server.toString();
		assertEquals(expectedServerToString, actualServerToString);
	}

	@Test
	public void testServerWithCustomCorrectPortAndTransport() {
		final int port = 4444;
		final Class<? extends ServerChannel> transport = OioServerSocketChannel.class;
		NettyHttpServer server = NettyHttpServer.newBuilder()
				.setPortNumber(port)
				.setTransport(transport).build();
		
		int expectedPortNumber = port;
		int actualPortNumber = server.getPortNumber();
		assertEquals(expectedPortNumber, actualPortNumber);
		
		Class<? extends ServerChannel> expectedTransport = transport;
		Class<? extends ServerChannel> actualTransport = server.getTransport();
		assertEquals(expectedTransport, actualTransport);
		
		String expectedServerToString = "NettyHttpServer: port = " + port;
		String actualServerToString = server.toString();
		assertEquals(expectedServerToString, actualServerToString);
	}
	
	@Test
	public void testServerWithCustomPortLessZero() {
		final int defaultPort = 8080;
		final int port = -100;
		NettyHttpServer server = NettyHttpServer.newBuilder().setPortNumber(port).build();
		
		int expectedPortNumber = defaultPort;
		int actualPortNumber = server.getPortNumber();
		assertEquals(expectedPortNumber, actualPortNumber);
	}

	@Test
	public void testServerWithCustomPortTooLarge() {
		final int defaultPort = 8080;
		final int port = 100_000;
		NettyHttpServer server = NettyHttpServer.newBuilder().setPortNumber(port).build();
		
		int expectedPortNumber = defaultPort;
		int actualPortNumber = server.getPortNumber();
		assertEquals(expectedPortNumber, actualPortNumber);
	}
	
	
}
