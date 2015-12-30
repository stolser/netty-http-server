package com.stolser.nettyserver.server.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.nettyserver.server.handlers.StatisticsCollector;

public class XMLStatisticsWarehouse implements StatisticsWarehouse {
	static private final Logger logger = LoggerFactory.getLogger(XMLStatisticsWarehouse.class);
	private List<IpAddressData> ipStatistics;
	private List<ConnectionData> connectionStatistics;
	private Map<URI, Integer> redirectStatistics;
	private String baseFileName;
	private XMLEventReader eventReader;

	static final String IPs = "ips";
	static final String IP = "ip";
	static final String UNIQUEREQUESTs = "uniquerequests";
	static final String URI = "uri";
	static final String CONNECTIONs = "connections";
	static final String CONNECTION = "connection";
	static final String REDIRECTs = "redirects";
	static final String REDIRECT = "redirect";

	public XMLStatisticsWarehouse(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	@Override
	public void update(ConnectionData data) {
		// static synchronize block
		try{
			Thread.sleep(2_000);
		} catch(InterruptedException ie) {

		}

	}

	@Override
	public List<IpAddressData> getIpStatistics() {
		createEventReader("ip");
		return getIpStatisticsFromFile();
	}

	@SuppressWarnings("unchecked")
	private List<IpAddressData> getIpStatisticsFromFile() {
		ipStatistics = new ArrayList<IpAddressData>();
		IpAddressData ip = null;
		boolean isUriTag = false;
		String ipAddress = null;
		int totalRequests = 0;
		String timeOfLastRequest = null;
		List<String> uniqueRequests = new ArrayList<>();

		try{
			while(eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				switch(event.getEventType()){
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().getLocalPart();

					if (qName.equalsIgnoreCase("ip")) {
						//ip = new IpAddressData();
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							String attribute = attributes.next().getName().toString();
							switch (attribute) {
							case "address":
								//ip.setIpAddress(attribute);
								break;
							case "total":
								ip.increaseTotalRequestsBy(Integer.valueOf(attribute));
								break;
							case "time":
								//ip.setTimeOfLastRequest(attribute);
								break;
							default:
								break;
							}
						}
					} else if (qName.equalsIgnoreCase("uri")) {
						isUriTag = true;
					} 	        
					break;

				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if(isUriTag){
						uniqueRequests.add(characters.getData());
						isUriTag = false;
					}
					break;

				case  XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if(endElement.getName().getLocalPart().equalsIgnoreCase("ip")){
						ipStatistics.add(ip);
					}
					break;
				}
			}
		} catch (XMLStreamException e) {
			logger.error("exception occured during parsing xml.", e);
		}

		return ipStatistics;
	}

	@Override
	public List<ConnectionData> getConnectionStatistics() {
		createEventReader("conn");
		return getConnectionStatisticsFromFile();
	}

	private List<ConnectionData> getConnectionStatisticsFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<URI, Integer> getRedirectStatistics() {
		createEventReader("redirect");

		return getRedirectStatisticsFromFile();
	}

	private Map<URI, Integer> getRedirectStatisticsFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

	private void createEventReader(String type) {
		Path path = Paths.get("statisticsip.xml");
		InputStream in = null;
		// First, create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		try {
			in = new BufferedInputStream(new FileInputStream(path.toFile()));
		} catch (FileNotFoundException e) {
			logger.error("exception occured during file opening.", e);
		}

		try {
			if(in != null) {
				eventReader = inputFactory.createXMLEventReader(in);
			}
		} catch (XMLStreamException e) {
			logger.error("exception occured during reading xml.", e);
		}

	}

}
