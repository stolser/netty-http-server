package com.stolser.nettyserver.server.handlers;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.*;

@Sharable
public class ActiveConnectionsCounter extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ActiveConnectionsCounter.class);
	private AtomicInteger activeConnCount = new AtomicInteger(0);
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		int i = activeConnCount.incrementAndGet();
		logger.debug("channelActive: active conns = {}", i);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		activeConnCount.decrementAndGet();
	}
	
	public int getCounter() {
		return activeConnCount.intValue();
	}
}
