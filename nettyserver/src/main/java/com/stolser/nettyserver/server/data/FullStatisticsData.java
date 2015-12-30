package com.stolser.nettyserver.server.data;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

public class FullStatisticsData implements Serializable {
	private static final long serialVersionUID = 1236547L;
	private static final int HashMap = 0;
	private List<IpAddressData> ips;
	private List<ConnectionData> conns;
	private Map<String, Integer> redirects;
	private int numberOfActiveConn;
	
	public void update(ConnectionData newData, int number) {
		
		

	}
	
	public void fillWithDummyData() {
		ips = new ArrayList<>();
		redirects = new HashMap<>();
		redirects.put("localhost", 101);
		redirects.put("ukr.net", 10);
		
		numberOfActiveConn = 10025;

	}
	
}
