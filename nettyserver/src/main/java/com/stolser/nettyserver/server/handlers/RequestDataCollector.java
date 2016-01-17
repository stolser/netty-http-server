package com.stolser.nettyserver.server.handlers;

import java.net.SocketAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class RequestDataCollector extends SimpleChannelInboundHandler<FullHttpRequest> {
	static private final Logger logger = LoggerFactory.getLogger(RequestDataCollector.class);
	static private final Marker dataTracerMarker = MarkerFactory.getMarker("dataTracer");
	private ConnectionData data;
	private FullHttpRequest request;
	private ChannelHandlerContext context;

	@Override
	protected void channelRead0(final ChannelHandlerContext context, 
								FullHttpRequest request) throws Exception {
		this.request = request;
		this.context = context;
		
		retrieveConnectionData();

		context.fireChannelRead(request.retain());
	}

	private void retrieveConnectionData() {
		SocketAddress sourceIp = context.channel().remoteAddress();
		String uri = request.getUri();
		Date timestamp = new Date();
		
		data = new ConnectionData(sourceIp, uri, timestamp);
		logger.debug(dataTracerMarker, "channelRead: data = {}", data);
	}

	public ConnectionData getData() {
		return data;
	}
}