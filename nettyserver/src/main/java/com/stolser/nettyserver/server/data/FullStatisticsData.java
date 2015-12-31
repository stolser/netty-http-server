package com.stolser.nettyserver.server.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.stolser.nettyserver.server.handlers.MainHttpHandler;

public class FullStatisticsData implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(FullStatisticsData.class);
	private static final long serialVersionUID = 1236547L;
	static private final String HTML_FILE_NAME = "response.html";
	static private final int INITIAL_BUFFER_CAPACITY = 500;
	private static final int LOG_ENTRY_MAX_NUMBER = 16;
	transient private ConnectionData newData;
	transient private String redirect;
	private List<IpAddressData> ipAddressDatas;
	private SortedMap<Date, ConnectionData> connectionDatas;
	private SortedMap<String, Integer> redirects;
	private Integer numberOfActiveConn;
	
	public FullStatisticsData() {
		this.ipAddressDatas = new ArrayList<IpAddressData>();
		this.connectionDatas = new TreeMap<Date, ConnectionData>();
		this.redirects = new TreeMap<String, Integer>();
		this.numberOfActiveConn = 0;
	}

	public void update(ConnectionData newData, int number, String redirect) {
		Preconditions.checkNotNull(newData, "connection data may not be null.");
		Preconditions.checkArgument(number >= 0, "number of active connections cannot be less than zero bytes.)");
		this.newData = newData;
		this.numberOfActiveConn = number;
		this.redirect = redirect;
		
		updateIpAddressData();
		updateConnectionData();
		updateRedirects();
	}
	
	public String generateHtmlContent() {
		StringBuffer result = new StringBuffer(INITIAL_BUFFER_CAPACITY);
		String[] htmlStrings = new String[8];
		
		Path path = Paths.get(HTML_FILE_NAME);
		try(BufferedReader out = new BufferedReader(new FileReader(path.toFile()))) {

			for (int i = 0; i < htmlStrings.length; i++) {
				htmlStrings[i] = out.readLine();
			}

		} catch (Exception e) {
			logger.debug("exception during reading a file", e);
		}
		
		
		
		
		
		
		
		
		return result.toString();
	}
	
	private String generateHtmlForRequestsTable() {
		List<String[]> data = new ArrayList<String[]>();
		String result = null;
		
		
		
		
		
		
		
		
		
		
		
		return result;
	}

	private void updateIpAddressData() {
		SocketAddress newIp = newData.getSourceIp();
		String newURI = newData.getUri();
		Date newDate = newData.getTimestamp();
		
		Optional<IpAddressData> existing = ipAddressDatas.stream()
				.filter(e -> newURI.equals(e.getIpAddress()))
				.findAny();
		if (existing.isPresent()) {
			IpAddressData ipData = existing.get();
			ipData.increaseTotalRequestsBy(1);
			ipData.setTimeOfLastRequest(newDate);
			ipData.addRequestIfUnique(newURI);
			
		} else {
			IpAddressData newIpAddressData = new IpAddressData(newIp, 1, Arrays.asList(newURI), newDate);
			ipAddressDatas.add(newIpAddressData);
		}
	}
	
	private void updateConnectionData() {
		Date newDate = newData.getTimestamp();
		if (connectionDatas.size() >= LOG_ENTRY_MAX_NUMBER) {
			Date lastDate = connectionDatas.firstKey();
			if (newDate.after(lastDate)) {
				connectionDatas.remove(lastDate);
				connectionDatas.put(newDate, newData);
			}
		} else {
			connectionDatas.put(newDate, newData);
		}
	}

	private void updateRedirects() {
		if (redirect != null) {
			Integer oldCounter = redirects.get(redirect);
			Integer newCounter;
			if (oldCounter != null) {
				newCounter = oldCounter + 1;
			} else {
				newCounter = 1;
			}
			redirects.put(redirect, newCounter);
		}	
	}
	
	private String generateHtmlForTable(List<String[]> data) {
		StringBuffer result = new StringBuffer(INITIAL_BUFFER_CAPACITY);
		for (int i = 0; i < data.size(); i++) {
			result.append("<tr>");
			String[] row = data.get(i);
			for (int j = 0; j < row.length; j++) {
				result.append("<td>" + row[j] + "</td>");
			}
			result.append("</tr>");
		}
		
		return result.toString();
	}

	public void fillWithRandomDummyData() {
		ipAddressDatas = new ArrayList<>();
		redirects = new TreeMap<>();
		redirects.put("localhost", 101);
		redirects.put("ukr.net", 10);
		
		numberOfActiveConn = 10025;

	}

	@Override
	public String toString() {
		return "FullStatisticsData:\n\tipAddressDatas = " + ipAddressDatas + ";\n\tconnectionDatas = "
				+ connectionDatas + ";\n\tredirects = " + redirects + ";\n\tnumberOfActiveConn = "
				+ numberOfActiveConn + "\n";
	}
	
}
