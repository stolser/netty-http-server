package com.stolser.nettyserver.server.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.NettyHttpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.*;

@Sharable
public class ActiveConnectionsCounter extends ChannelInboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(ActiveConnectionsCounter.class);
	private AtomicInteger activeConnCount = new AtomicInteger(0);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		activeConnCount.incrementAndGet();
		logger.debug("ActiveConnectionsCounter.channelActive");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		activeConnCount.decrementAndGet();
		logger.debug("ActiveConnectionsCounter.channelInactive");
	}
	
	public int getCounter() {
		return activeConnCount.intValue();
	}
	
}
