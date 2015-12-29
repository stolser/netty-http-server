package com.stolser.nettyserver.server.data;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IpAddressData {
	private SocketAddress ipAddress;
	private int totalRequests;
	private List<URI> uniqueRequests;
	private Date timeOfLastRequest;
	
	public IpAddressData(SocketAddress ipAddress) {
		if(ipAddress == null) {
			throw new IllegalArgumentException("ipAddress cannot be null.");
		}
		this.ipAddress = ipAddress;
		this.uniqueRequests = new ArrayList<URI>();
	}

	public int getTotalRequests() {
		return totalRequests;
	}
	
	public boolean addUniqueRequest(URI newUri) {
		if(newUri == null) {
			throw new IllegalArgumentException("URI cannot be null.");
		}
		if(contains(newUri)) {
			return false;
		} else {
			uniqueRequests.add(newUri);
			return true;
		}
	}
	
	public boolean contains(Object o) {
		if(o == null) return false;
		
		for (int i = 0; i < uniqueRequests.size(); i++) {
			if (o.equals(uniqueRequests.get(i))) 
				return true;
		} 
		
		return false;
	}

	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	public Date getTimeOfLastRequest() {
		return timeOfLastRequest;
	}

	public void setTimeOfLastRequest(Date timeOfLastRequest) {
		this.timeOfLastRequest = timeOfLastRequest;
	}

	public SocketAddress getIpAddress() {
		return ipAddress;
	}

	public List<URI> getUniqueRequests() {
		return uniqueRequests;
	}
	
	@Override
	public String toString() {
		return ipAddress.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (!(obj instanceof IpAddressData)) {
			return false;
		}
		
		IpAddressData other = (IpAddressData) obj;
		if (! ipAddress.equals(other.ipAddress)) {
			return false;
		}
		return true;
	}
}
