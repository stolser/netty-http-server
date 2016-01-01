package com.stolser.nettyserver;

import com.stolser.nettyserver.server.NettyHttpServer;

public class ServerStarter {
	private static final String STORAGE_FILE_NAME = "statistics.data";
	public static void main(String[] args) throws Exception {
		NettyHttpServer.newBuilder().setStorageFileName(STORAGE_FILE_NAME).build().start();
	}
}
