package com.stolser.nettyserver.server.data;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;

public class XMLStatisticsWarehouse implements StatisticsWarehouse {
	private List<IpAddressData> ipStatistics;
	private List<ConnectionData> connectionStatistics;
	private Map<URI, Integer> redirectStatistics;
	private String statFileName;
	private XMLEventReader eventReader;
		
	public XMLStatisticsWarehouse(String statFileName) {
		this.statFileName = statFileName;
	}

	@Override
	public void update(ConnectionData data) {
		

	}

	@Override
	public List<IpAddressData> getIpStatistics() {
		createEventReader();
		return getIpStatisticsFromFile();
	}

	private List<IpAddressData> getIpStatisticsFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConnectionData> getConnectionStatistics() {
		createEventReader();
		return getConnectionStatisticsFromFile();
	}

	private List<ConnectionData> getConnectionStatisticsFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<URI, Integer> getRedirectStatistics() {
		createEventReader();
		
		return getRedirectStatisticsFromFile();
	}
	
	private Map<URI, Integer> getRedirectStatisticsFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

	private void createEventReader() {
		// TODO Auto-generated method stub
		
	}

}
