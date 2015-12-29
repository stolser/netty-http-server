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
	static private final Logger logger = LoggerFactory.getLogger(MainHttpHandler.class);
	private FullHttpResponse response;
	/**A Buffer that stores the response content */
	private final StringBuilder defaultContent = new StringBuilder("<h1>Oops! Nothing found!!!</h1>");
	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		
		String requestedUri = request.getUri();
		
		if("/hello".equalsIgnoreCase(requestedUri)) {
			//context.pipeline().addLast("helloHandler", new HelloUriHandler());
			context.fireChannelRead(request.retain());
		} else {
			response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(
					(defaultContent.toString()).getBytes()));
			context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Exception during pipelining.", cause);
		ctx.close();
	}
	
}
