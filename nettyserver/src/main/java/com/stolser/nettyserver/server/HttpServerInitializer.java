package com.stolser.nettyserver.server;

import com.stolser.nettyserver.server.handlers.ActiveConnectionsCounter;
import com.stolser.nettyserver.server.handlers.MainHttpHandler;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel>{
	private static final int MAX_CONTENT_LENGTH = 512 * 1024; // 512kb
	private final ActiveConnectionsCounter activeConnectionsCounter = new ActiveConnectionsCounter();
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("codec", new HttpServerCodec());
		pipeline.addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
		pipeline.addLast("mainHandler", new MainHttpHandler());
	}

}
