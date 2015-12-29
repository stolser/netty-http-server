package com.stolser.nettyserver.server.handlers;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;


public class StatusUriHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	static private final Logger logger = LoggerFactory.getLogger(StatusUriHandler.class);
	private String responseContent = "<h1 style='color:red;text-align:center;'>Statistics table</h1>";
	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		logger.debug("StatusUriHandler.channelRead0");
		/*
		 * - get info from the statistics.xml into objects;
		 * - generate a responseText - html tables;
		 * - sent responseText:
		 	response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(
					(defaultContent.toString()).getBytes()));
			context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		 * - 
		 * */
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(
				responseContent.getBytes()));
		response.headers().set(CONTENT_TYPE, "text/html");
		context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

}
