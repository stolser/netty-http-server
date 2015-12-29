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
	private Class<? extends ServerChannel> transport;
	private int portNumber;
	
	private NettyHttpServer(NettyHttpServerBuilder builder) {
		this.transport = builder.transport;
		this.portNumber = builder.portNumber;
	}
	
	public static NettyHttpServerBuilder newBuilder() {
		return new NettyHttpServerBuilder();
	}
	
	public void start() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				.channel(transport)
				.childHandler(new HttpServerInitializer());
			
			ChannelFuture future = bootstrap.bind(portNumber).sync();
			future.channel().closeFuture().sync();
		
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public Class<? extends ServerChannel> getTransport() {
		return transport;
	}

	public int getPortNumber() {
		return portNumber;
	}
	
	@Override
	public String toString() {
		return "NettyHttpServer: port = " + portNumber;
	}

	public static class NettyHttpServerBuilder {
		private static final int DEFAULT_PORT_NUMBER = 8080;
		private static final Class<? extends ServerChannel> DEFAULT_TRANSPORT = NioServerSocketChannel.class;
		private Class<? extends ServerChannel> transport;
		private int portNumber;
		
		private NettyHttpServerBuilder() {
			transport = DEFAULT_TRANSPORT;
			portNumber = DEFAULT_PORT_NUMBER;
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
		
		public NettyHttpServer build() {
			return new NettyHttpServer(this);
		}
	}
}
