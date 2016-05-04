package com.stolser.nettyserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.handlers.MainHttpHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyHttpServer {
	static private final Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
	private static final int NUMBER_OF_WORKERS = 16;
	private Class<? extends ServerChannel> transport;
	private int portNumber;
	private String storageFileName;
	
	private NettyHttpServer(NettyHttpServerBuilder builder) {
		this.transport = builder.transport;
		this.portNumber = builder.portNumber;
		this.storageFileName = builder.storageFileName;
	}
	
	public static NettyHttpServerBuilder newBuilder() {
		return new NettyHttpServerBuilder();
	}
	
	public void start() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(group, group)
				.channel(transport)
				.childHandler(new HttpServerInitializer(storageFileName));
			
			logger.debug("starting the server: {}", this);
			ChannelFuture future = bootstrap.bind(portNumber).sync();
			future.channel().closeFuture().sync();
		
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public Class<? extends ServerChannel> getTransport() {
		return transport;
	}

	public int getPortNumber() {
		return portNumber;
	}
	
	public String getStorageFileName() {
		return storageFileName;
	}

	@Override
	public String toString() {
		return "NettyHttpServer [transport = " + transport + ", portNumber = " + portNumber
				+ ", storageFileName = " + storageFileName + "]";
	}

	public static class NettyHttpServerBuilder {
		private static final int DEFAULT_PORT_NUMBER = 8080;
		private static final Class<? extends ServerChannel> DEFAULT_TRANSPORT = NioServerSocketChannel.class;
		private static final String DEFAULT_STORAGE_FILE_NAME = "statistics.data";
		private Class<? extends ServerChannel> transport;
		private int portNumber;
		private String storageFileName;
		
		private NettyHttpServerBuilder() {
			transport = DEFAULT_TRANSPORT;
			portNumber = DEFAULT_PORT_NUMBER;
			storageFileName = DEFAULT_STORAGE_FILE_NAME;
		}

		public NettyHttpServerBuilder setTransport(Class<? extends ServerChannel> transport) {
			this.transport = transport;
			return this;
		}

		public NettyHttpServerBuilder setPortNumber(int portNumber) {
			if((portNumber > 0) && (portNumber <= 65_535)) {
				this.portNumber = portNumber;
			} else {
				logger.warn("Incorrect port number. Use a default one: {}", DEFAULT_PORT_NUMBER);
			}
			
			return this;
		}
		
		public NettyHttpServerBuilder setStorageFileName(String storageFileName) {
			this.storageFileName = storageFileName;
			
			return this;
		}

		public NettyHttpServer build() {
			return new NettyHttpServer(this);
		}
	}
}
