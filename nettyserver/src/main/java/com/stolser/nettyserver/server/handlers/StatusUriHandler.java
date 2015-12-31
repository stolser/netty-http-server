package com.stolser.nettyserver.server.handlers;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;
import com.stolser.nettyserver.server.data.FullStatisticsData;
import com.stolser.nettyserver.server.data.IpAddressData;
import com.stolser.nettyserver.server.data.StatisticsWarehouse;
import com.stolser.nettyserver.server.data.XMLStatisticsWarehouse;

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
	static private final String STAT_FILE_NAME = "statistics.data";
	private String responseContent;
	private FullStatisticsData fullData;
	private ChannelHandlerContext context;

	@Override
	protected void channelRead0(final ChannelHandlerContext context, FullHttpRequest request) throws Exception {
		logger.debug("StatusUriHandler.channelRead0");
		this.context = context;
		
		retrieveStatData();
		generateResponseContent();
		createAndSendFullHttpResponse();	
	}

	private void retrieveStatData() {
		Path path = Paths.get(STAT_FILE_NAME);
		try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
								new FileInputStream(path.toFile())))) {
			fullData = (FullStatisticsData)in.readObject();
		} catch (Exception e) {
			logger.debug("exception during reading a file {}", STAT_FILE_NAME, e);
		}
	}
	
	private void generateResponseContent() {
		responseContent = fullData.generateHtmlContent();
	}
	
	private void createAndSendFullHttpResponse() {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(
				responseContent.getBytes()));
		response.headers().set(CONTENT_TYPE, "text/html");
		context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
