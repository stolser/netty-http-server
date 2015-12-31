package com.stolser.nettyserver.server.handlers;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.NettyHttpServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;

public class MainHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static final Logger logger = LoggerFactory.getLogger(MainHttpHandler.class);
	private static final String REDIRECT_PARAM_NAME = "url";
	private static final String DEFAULT_REDIRECT_URL = "http://example.com";
	private ChannelHandlerContext context;
	private FullHttpRequest request;
	private FullHttpResponse response;
	private String redirectUrl;
	/**A Buffer that stores the response content */
	private final StringBuilder defaultContent = new StringBuilder("<h1>Oops! Nothing found!!!</h1>");
	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		this.request = request;
		this.context = context;

		logger.debug("MainHttpHandler.channelRead0; uri = {}", request.getUri());
		String requestedUri = request.getUri();

		if("/hello".equalsIgnoreCase(requestedUri)) {
			context.pipeline().addLast("helloHandler", new HelloUriHandler());
			context.fireChannelRead(request.retain());
			
		} else if(requestedUri.startsWith("/redirect")) {
			createRedirectResponse();
			context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			
		} else if("/status".equalsIgnoreCase(requestedUri)) {
			EventExecutorGroup group = new DefaultEventExecutorGroup(16);
			context.pipeline().addLast(group, "statusHandler", new StatusUriHandler());
			context.fireChannelRead(request.retain());
			
		} else {
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(
					(defaultContent.toString()).getBytes()));
			context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Exception during pipelining.", cause);
		ctx.close();
	}

	private void createRedirectResponse() {
		redirectUrl = retrieveRedirectUrl();
		response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
		response.headers().set(LOCATION, redirectUrl);
	}

	private String retrieveRedirectUrl() {
		String url = null;
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = queryStringDecoder.parameters();
		if (params.containsKey(REDIRECT_PARAM_NAME)) {
			url = params.get(REDIRECT_PARAM_NAME).get(0);
		}
		
		if ((url == null) || ("".equals(url))) {
			url = DEFAULT_REDIRECT_URL;
		}
		logger.debug("url = {}", url);
		return url;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}
}
