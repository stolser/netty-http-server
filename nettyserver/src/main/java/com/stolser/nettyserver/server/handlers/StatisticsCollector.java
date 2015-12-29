package com.stolser.nettyserver.server.handlers;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.ReferenceCountUtil;

public class StatisticsCollector extends ChannelInboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
	public int randomInt = new Random().nextInt(1_000_000);

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) {
		logger.debug("StatisticsCollector.channelRead");
		FullHttpRequest request = (FullHttpRequest)message;
		long readBytes = 
				((ChannelTrafficShapingHandler)context.pipeline().get("trafficCounter")).trafficCounter().cumulativeReadBytes();
		long writtenBytes = 
				((ChannelTrafficShapingHandler)context.pipeline().get("trafficCounter")).trafficCounter().cumulativeWrittenBytes();
		logger.debug("uri = {}; randomInt = {}", request.getUri(), randomInt);

		/* 
		 * - get all necessary info from the request;
		 * - create new ConnectionData obj;
		 */ 
		
		context.fireChannelRead(message);
		
	}
}