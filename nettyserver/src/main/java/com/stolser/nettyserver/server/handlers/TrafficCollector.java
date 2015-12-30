package com.stolser.nettyserver.server.handlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.data.ConnectionData;
import com.stolser.nettyserver.server.data.FullStatisticsData;
import com.stolser.nettyserver.server.data.StatisticsWarehouse;
import com.stolser.nettyserver.server.data.XMLStatisticsWarehouse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class TrafficCollector extends ChannelOutboundHandlerAdapter {
	static private final Logger logger = LoggerFactory.getLogger(TrafficCollector.class);
	private static final String FILE_NAME = "statistics.data";
	private FullStatisticsData fullData;
	private ChannelHandlerContext context;

	@Override
	public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
		this.context = context;

		ConnectionData connData = ((StatisticsCollector)context.pipeline()
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
		int numberOfActiveConn = 10; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		connData.setReceivedBytes(receivedBytes).setSentBytes(sentBytes).setSpeed(speed);

		logger.debug("TrafficCollector.write; sent = {}; received = {}; fullData = {}", sentBytes, receivedBytes, connData);
		
		new Thread(new Runnable() {
			public void run() {
				Path path = Paths.get(FILE_NAME);
				try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
						new FileInputStream(path.toFile())))) {

					fullData = (FullStatisticsData)in.readObject();

				} catch (Exception e) {
					logger.debug("exception during reading a file", e);
				}

				fullData.update(connData, numberOfActiveConn);

				try {
					//erase the old content
					new FileOutputStream(Paths.get(FILE_NAME).toFile()).close();
				} catch (Exception e) {
					logger.debug("exception during erasing the file {}", FILE_NAME, e);
				}

				try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
								new FileOutputStream(path.toFile())))) {
					
					fullData.fillWithDummyData();
					out.writeObject(fullData);
				} catch (Exception e) {
					logger.debug("exception during writing into the file {}", FILE_NAME, e);
				}
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
