package com.stolser.nettyserver.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

public class StatisticsPersister extends ChannelOutboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(StatisticsPersister.class);
	public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
				
		long readBytes = 
				((ChannelTrafficShapingHandler)context.pipeline().get("trafficCounter")).trafficCounter().cumulativeReadBytes();
		long writtenBytes = 
				((ChannelTrafficShapingHandler)context.pipeline().get("trafficCounter")).trafficCounter().cumulativeWrittenBytes();
		
		int randomInt = ((StatisticsCollector)context.pipeline().get("statistics")).randomInt;
		
		logger.debug("StatisticsPersister.write; readBytes = {}; writtenBytes = {}; randomInt = {}", readBytes, 
				writtenBytes, randomInt);
		
		/*
		 * - get info about traffic;
		 * - retrieve statistics data from StatisticsCollector
		 * - save data into file (add a new handler for statistics persistence using EventExecutorGroup;)
		 * 
		 * */
		
		promise.setSuccess();
		context.writeAndFlush(message);
	}
}
