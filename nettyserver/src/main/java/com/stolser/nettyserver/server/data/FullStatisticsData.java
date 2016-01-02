package com.stolser.nettyserver.server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;








import com.google.common.base.Preconditions;
import com.stolser.nettyserver.server.handlers.MainHttpHandler;

public class FullStatisticsData implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(FullStatisticsData.class);
	private static final long serialVersionUID = 1236547L;
	private static final String HTML_FILE_NAME = "response.html";
	private static final int INITIAL_BUFFER_CAPACITY = 500;
	private static final int LOG_ENTRY_MAX_NUMBER = 16;
	private static final String DATE_FORMAT_PATTERN = "dd MMM kk:mm:ss";
	
	transient private ConnectionData newData;
	transient private String redirect;
	private int numberOfUniqueRequests;
	private List<IpAddressData> ipAddressDatas;
	private ConcurrentNavigableMap<Date, ConnectionData> connectionDatas;
	private ConcurrentNavigableMap<String, Integer> redirects;
	private int totalNumberOfRequests;
	private Integer numberOfActiveConn;
	
	public FullStatisticsData() {
		this.ipAddressDatas = new CopyOnWriteArrayList<IpAddressData>();
		this.connectionDatas = new ConcurrentSkipListMap<Date, ConnectionData>();
		this.redirects = new ConcurrentSkipListMap<String, Integer>();
		this.totalNumberOfRequests = 0;
		this.numberOfActiveConn = 0;
	}

	public void update(ConnectionData newData, int number, String redirect) {
		Preconditions.checkNotNull(newData, "connection data may not be null.");
		Preconditions.checkArgument(number >= 0, "number of active connections cannot be less than zero bytes.)");
		this.newData = newData;
		this.numberOfActiveConn = number;
		this.redirect = redirect;
		
		totalNumberOfRequests++;
		logger.debug("update: totalNumberOfRequests = {}", totalNumberOfRequests);
		
		updateIpAddressData();
		updateConnectionData();
		updateRedirects();
	}
	
	public String generateHtmlContent() {
		StringBuffer result = new StringBuffer(INITIAL_BUFFER_CAPACITY);
		String[] htmlStrings = readHtmlStringsFromFile();
		
		numberOfUniqueRequests = calculateUniqueRequestsNumber();
			
		result.append(htmlStrings[0]).append(numberOfActiveConn)
			.append(htmlStrings[1]).append(totalNumberOfRequests)
			.append(htmlStrings[2]).append(numberOfUniqueRequests)
			.append(htmlStrings[3]).append(generateHtmlForRequestsTable())
			.append(htmlStrings[4]).append(generateHtmlForRedirectsTable())
			.append(htmlStrings[5]).append(generateHtmlForLogTable())
			.append(htmlStrings[6]);
		
		return result.toString();
	}
	
	private int calculateUniqueRequestsNumber() {
		AtomicInteger accumulator = new AtomicInteger(0);
		ipAddressDatas.forEach(e -> {
			accumulator.accumulateAndGet(e.getUniqueRequests().size(), ( a, b ) -> a + b);
		});

		return accumulator.intValue();
	}

	private String[] readHtmlStringsFromFile() {
		String[] htmlStrings = new String[7];
		
		File file = new File(HTML_FILE_NAME);
		try(BufferedReader out = new BufferedReader(new FileReader(file))) {

			for (int i = 0; i < htmlStrings.length; i++) {
				htmlStrings[i] = out.readLine();
			}

		} catch (Exception e) {
			logger.debug("exception during reading a file {}", HTML_FILE_NAME, e);
		}
		
		return htmlStrings;
	}

	private String generateHtmlForRequestsTable() {
		final int rowNumber = 3;
		List<String[]> data = new ArrayList<String[]>();
		String result = null;
		
		ipAddressDatas.forEach(e -> {
			String[] column = new String[rowNumber];
			
			column[0] = ((InetSocketAddress)e.getIpAddress()).toString();
			column[1] = "" + e.getTotalRequests();
			column[2] = new SimpleDateFormat(DATE_FORMAT_PATTERN).format(e.getTimeOfLastRequest());
			
			data.add(column);
		});
		
		result = generateHtmlForTable(data);
		
		return result;
	}
	
	private String generateHtmlForRedirectsTable() {
		final int rowNumber = 2;
		List<String[]> data = new ArrayList<String[]>();
		String result = null;
		
		redirects.entrySet().forEach(e -> {
			String[] column = new String[rowNumber];
			
			column[0] = e.getKey();
			column[1] = "" + e.getValue();
			
			data.add(column);
		});
		
		result = generateHtmlForTable(data);
		
		return result;
	}
	
	private String generateHtmlForLogTable() {
		final int rowNumber = 7;
		List<String[]> data = new ArrayList<String[]>();
		String result = null;
		AtomicInteger counter = new AtomicInteger(1);
		
		connectionDatas.descendingMap().entrySet().forEach(e -> {
			String[] column = new String[rowNumber];
			ConnectionData conn = e.getValue();

			column[0] = "" + counter.getAndIncrement();
			column[1] = new SimpleDateFormat(DATE_FORMAT_PATTERN).format(e.getKey());
			column[2] = ((InetSocketAddress)conn.getSourceIp()).toString();
			column[3] = conn.getUri();
			column[4] = "" + conn.getSentBytes();
			column[5] =	"" + conn.getReceivedBytes();
			column[6] = "" + conn.getSpeed();
			
			data.add(column);
		});
		
		result = generateHtmlForTable(data);
		
		return result;
	}

	private void updateIpAddressData() {
		SocketAddress newIp = newData.getSourceIp();
		String newURI = newData.getUri();
		Date newDate = newData.getTimestamp();
		
		Optional<IpAddressData> existing = ipAddressDatas.stream()
				.filter(e -> newIp.equals(e.getIpAddress()))
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
		int visibleRowsLimit = 20;
		StringBuffer result = new StringBuffer(INITIAL_BUFFER_CAPACITY);
		int dataSize = data.size();
		
		if (dataSize > visibleRowsLimit) {
			result.append("<tfoot><tr><td colspan='10'>The total number of rows (unique ip): " + dataSize + 
							"&nbsp;&nbsp;&nbsp;<button id='toggle'>Show all</button></td></tr></tfoot>");
		}
		
		for (int i = 0; i < data.size(); i++) {
			if (i < visibleRowsLimit) {
				result.append("<tr>");
			} else {
				result.append("<tr class='beyondLimit'>");
			}
			
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
		redirects = new ConcurrentSkipListMap<>();
		redirects.put("localhost", 101);
		redirects.put("ukr.net", 10);
		redirects.put("oracle.com", 3);
		numberOfActiveConn = 10025;

	}

	@Override
	public String toString() {
		return "FullStatisticsData:\n\tipAddressDatas = " + ipAddressDatas + ";\n\tconnectionDatas = "
				+ connectionDatas + ";\n\tredirects = " + redirects + ";\n\tnumberOfActiveConn = "
				+ numberOfActiveConn + "\n";
	}
	
}
