package com.stolser.nettyserver.server.data;

public interface StatisticsDataStorage {
	public FullStatisticsData retrieveData();
	public void persistData(FullStatisticsData data);
}
