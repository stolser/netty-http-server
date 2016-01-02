package com.stolser.nettyserver.server.data;

import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;

public class IpAddressData implements Serializable {
	private static final long serialVersionUID = 4596687113110691949L;
	private int totalRequests;
	private SocketAddress ipAddress;
	private List<String> uniqueRequests;
	private Date timeOfLastRequest;
	
	public IpAddressData(SocketAddress ipAddress) {
		this(ipAddress, 0, new ArrayList<String>(), null);
	}

	public IpAddressData(SocketAddress ipAddress, int totalRequests, List<String> uniqueRequests,
			Date timeOfLastRequest) {
		Preconditions.checkNotNull(ipAddress, "ip may not be null.");
		Preconditions.checkArgument(totalRequests >= 0, "you cannot increase by a negative value.");
		Preconditions.checkNotNull(uniqueRequests, "if no requests yet, pass an empty list<String>.");
				
		this.ipAddress = ipAddress;
		this.totalRequests = totalRequests;
		this.uniqueRequests = uniqueRequests;
		this.timeOfLastRequest = timeOfLastRequest;
	}

	public int getTotalRequests() {
		return totalRequests;
	}
	
	public boolean addRequestIfUnique(String newUri) {
		if((newUri == null) || (uniqueRequests.contains(newUri))) {
			return false;
		} else {
			uniqueRequests.add(newUri);
			return true;
		}
	}
	
	public void increaseTotalRequestsBy(int number) {
		Preconditions.checkArgument(number >= 0, "you cannot increase by a negative value.");
		this.totalRequests += number;
	}

	public Date getTimeOfLastRequest() {
		return timeOfLastRequest;
	}

	public void setTimeOfLastRequest(Date timeOfLastRequest) {
		Preconditions.checkNotNull(ipAddress, "last time may not be null.");
		this.timeOfLastRequest = timeOfLastRequest;
	}

	public SocketAddress getIpAddress() {
		return ipAddress;
	}

	public List<String> getUniqueRequests() {
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
