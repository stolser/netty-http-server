package com.stolser.nettyserver.server;

import com.stolser.nettyserver.server.handlers.ActiveConnectionsCounter;
import com.stolser.nettyserver.server.handlers.MainHttpHandler;
import com.stolser.nettyserver.server.handlers.FullDataCollector;
import com.stolser.nettyserver.server.handlers.RequestDataCollector;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel>{
	private static final int MAX_CONTENT_LENGTH = 512 * 1024; // 512kb
	private static final int NUMBER_OF_THREADS = 400;
	private static EventExecutorGroup group = new DefaultEventExecutorGroup(NUMBER_OF_THREADS);
	private final ActiveConnectionsCounter activeConnectionsCounter = new ActiveConnectionsCounter();
	private String storageFileName;
	
	public HttpServerInitializer(String storageFileName) {
		this.storageFileName = storageFileName;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		
		pipeline.addLast(group, "trafficCollector", new FullDataCollector(storageFileName));
		pipeline.addLast("trafficCounter", new ChannelTrafficShapingHandler(1000));
		pipeline.addLast("connCounter", activeConnectionsCounter);
		pipeline.addLast("codec", new HttpServerCodec());
		pipeline.addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
		pipeline.addLast("requestDataCollector", new RequestDataCollector());
		pipeline.addLast("mainHandler", new MainHttpHandler(storageFileName));
	}
}
