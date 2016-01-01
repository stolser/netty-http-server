package com.stolser.nettyserver.server.handlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;
import com.stolser.nettyserver.server.data.FileStatisticsStorage;
import com.stolser.nettyserver.server.data.FullStatisticsData;
import com.stolser.nettyserver.server.data.StatisticsDataStorage;
import com.stolser.nettyserver.server.data.XMLStatisticsStorage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class FullDataCollector extends ChannelOutboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(FullDataCollector.class);
	private FullStatisticsData fullData;
	private String storageFileName;

	public FullDataCollector(String storageFileName) {
		this.storageFileName = storageFileName;
	}

	@Override
	public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
		StatisticsDataStorage storage = FileStatisticsStorage.getInstance(storageFileName);

		ConnectionData connData = ((RequestDataCollector)context.pipeline()
				.get("requestDataCollector"))
				.getData();
		long sentBytes = ((ChannelTrafficShapingHandler)context.pipeline()
				.get("trafficCounter"))
				.trafficCounter()
				.cumulativeReadBytes();
		long receivedBytes = ((ChannelTrafficShapingHandler)context.pipeline()
				.get("trafficCounter"))
				.trafficCounter()
				.cumulativeWrittenBytes();
		double speed = 25.78;
		int numberOfActiveConn = ((ActiveConnectionsCounter)context.pipeline()
				.get("connCounter")).getCounter();
		String redirect = ((MainHttpHandler)context.pipeline()
				.get("mainHandler")).getRedirectUrl();
		
		connData.setReceivedBytes(receivedBytes).setSentBytes(sentBytes).setSpeed(speed);
		
		new Thread(new Runnable() {
			public void run() {
				fullData = storage.retrieveData();
				/*if a storage file is empty you need to use a direct initialization of fullData
				 * fullData = new FullStatisticsData();*/
				fullData.update(connData, numberOfActiveConn, redirect);
				storage.persistData(fullData);
			}
		}).start();

/*		EventExecutorGroup group = new DefaultEventExecutorGroup(16);
		context.pipeline().addFirst(group, "persister", new ChannelOutboundHandlerAdapter(){
			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				
				// doesn't work like that	
				ctx.write(msg, promise);
			}
		});*/

		context.write(message, promise);
	}
}
