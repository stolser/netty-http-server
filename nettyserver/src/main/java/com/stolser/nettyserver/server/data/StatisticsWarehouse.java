package com.stolser.nettyserver.server.data;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface StatisticsWarehouse {
	void update(ConnectionData data);
	List<IpAddressData> getIpStatistics();
	List<ConnectionData> getConnectionStatistics();
	Map<URI, Integer> getRedirectStatistics();
	
}
