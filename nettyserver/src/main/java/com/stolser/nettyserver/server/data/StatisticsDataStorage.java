package com.stolser.nettyserver.server.data;

import java.net.URI;
import java.util.List;
import java.util.Map;

public interface StatisticsDataStorage {
	public FullStatisticsData retrieveData();
	public void persistData(FullStatisticsData data);
	
}
