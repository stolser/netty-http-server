package com.stolser.nettyserver.server.handlers;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.concurrent.TimeUnit;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.ScheduledFuture;

public class HelloUriHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static final int TIME_DELAY = 10; // in seconds

	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		String content = "<h1 style='color:blue;font-size:45;display:block;text-align:center;margin-top:200px;'>Hello World</h1>";
		final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(
				content.getBytes()));
		response.headers().set(CONTENT_TYPE, "text/html");
		
		ScheduledFuture<?> future = context.channel().eventLoop().schedule(new Runnable() {
			@Override 
			public void run() {
				context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			}
		}, TIME_DELAY, TimeUnit.SECONDS);
	}
}
