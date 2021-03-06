package com.stolser.nettyserver.server.handlers;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.FileStatisticsStorage;
import com.stolser.nettyserver.server.data.StatisticsDataStorage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class StatusUriHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	static private final Logger logger = LoggerFactory.getLogger(StatusUriHandler.class);
	private String responseContent;
	private ChannelHandlerContext context;
	private String storageFileName;

	public StatusUriHandler(String storageFileName) {
		this.storageFileName = storageFileName;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		this.context = context;
		StatisticsDataStorage storage = FileStatisticsStorage.getInstance(storageFileName);

		responseContent = storage.retrieveData().generateHtmlContent();
		createAndSendFullHttpResponse();	
		
	}
	
	private void createAndSendFullHttpResponse() {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(
				responseContent.getBytes()));
		response.headers().set(CONTENT_TYPE, "text/html");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		
		context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
