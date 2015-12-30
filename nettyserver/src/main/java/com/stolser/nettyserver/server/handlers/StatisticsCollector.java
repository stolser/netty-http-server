package com.stolser.nettyserver.server.handlers;

import java.net.SocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.ReferenceCountUtil;

public class StatisticsCollector extends ChannelInboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
	private ConnectionData data;
	private FullHttpRequest request;
	private ChannelHandlerContext context;

	@Override
	public void channelRead(ChannelHandlerContext context, Object message) {
		logger.debug("StatisticsCollector.channelRead");
		request = (FullHttpRequest)message;
		this.context = context;
		
		createConnectionData();

		context.fireChannelRead(message);
	}

	private void createConnectionData() {
		SocketAddress sourceIp = context.channel().remoteAddress();
		String uri = request.getUri();
		Date timestamp = new Date();
		
		data = new ConnectionData(sourceIp, uri, timestamp);
		
	}

	public ConnectionData getData() {
		return data;
	}
}