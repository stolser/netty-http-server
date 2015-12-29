package com.stolser.nettyserver;

import com.stolser.nettyserver.server.NettyHttpServer;

public class ServerStarter {

	public static void main(String[] args) throws Exception {
		NettyHttpServer.newBuilder().build().start();
	}
}
