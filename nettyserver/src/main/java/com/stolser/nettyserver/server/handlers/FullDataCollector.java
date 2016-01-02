package com.stolser.nettyserver.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;
import com.stolser.nettyserver.server.data.FileStatisticsStorage;
import com.stolser.nettyserver.server.data.FullStatisticsData;
import com.stolser.nettyserver.server.data.StatisticsDataStorage;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class FullDataCollector extends ChannelOutboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(FullDataCollector.class);
	private ChannelHandlerContext context;
	private FullStatisticsData fullData;
	private String storageFileName;
	private ConnectionData connData;
	private int numberOfActiveConn;
	private String redirect;
	
	public FullDataCollector(String storageFileName) {
		this.storageFileName = storageFileName;
	}

	@Override
	public void flush(ChannelHandlerContext context) throws Exception {
		this.context = context;
		collectAllStatisticsData();
		updateDataInFile();
		context.flush();
	}

	private void updateDataInFile() {
		StatisticsDataStorage storage = FileStatisticsStorage.getInstance(storageFileName);
		
		synchronized(storage) {
			fullData = storage.retrieveData();
			/* if a storage file is empty or you want to reset the file just use one time a direct initialization of fullData:
			fullData = new FullStatisticsData();
			 * */
			fullData.update(connData, numberOfActiveConn, redirect);
			storage.persistData(fullData);
		}
	}

	private void collectAllStatisticsData() {
		connData = ((RequestDataCollector)context.pipeline()
				.get("requestDataCollector"))
				.getData();
		long receivedBytes = ((ChannelTrafficShapingHandler)context.pipeline()
				.get("trafficCounter"))
				.trafficCounter()
				.cumulativeReadBytes();
		long sentBytes = ((ChannelTrafficShapingHandler)context.pipeline()
				.get("trafficCounter"))
				.trafficCounter()
				.cumulativeWrittenBytes();
		double speed = 25.78;

		connData.setReceivedBytes(receivedBytes).setSentBytes(sentBytes).setSpeed(speed);
		
		numberOfActiveConn = ((ActiveConnectionsCounter)context.pipeline()
				.get("connCounter")).getCounter();
		redirect = ((MainHttpHandler)context.pipeline().get("mainHandler")).getRedirectUrl();
	}
}
