package com.stolser.nettyserver.server.data;

import java.io.Serializable;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionData implements Serializable {
	private static final long serialVersionUID = 1234567L;
	private static AtomicInteger nextId = new AtomicInteger(1);
	private int id;
	private SocketAddress sourceIp;
	private String uri;
	private Date timestamp;
	private long sentBytes;
	private long receivedBytes;
	private double speed;
	
	public ConnectionData(SocketAddress sourceIp, String uri, Date timestamp) {
		this.id = nextId.getAndIncrement();
		this.sourceIp = sourceIp;
		this.uri = uri;
		this.timestamp = timestamp;
	}

	public long getSentBytes() {
		return sentBytes;
	}

	public ConnectionData setSentBytes(long sentBytes) {
		this.sentBytes = sentBytes;
		return this;
	}

	public long getReceivedBytes() {
		return receivedBytes;
	}

	public ConnectionData setReceivedBytes(long receivedBytes) {
		this.receivedBytes = receivedBytes;
		return this;
	}

	public double getSpeed() {
		return speed;
	}

	public ConnectionData setSpeed(double speed) {
		this.speed = speed;
		return this;
	}

	public SocketAddress getSourceIp() {
		return sourceIp;
	}

	public String getUri() {
		return uri;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "Connection(" + sourceIp + " --> " + uri + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (!(obj instanceof ConnectionData)) {
			return false;
		}
		
		ConnectionData other = (ConnectionData) obj;
		if (id != other.id) return false;
		
		return true;
	}
}
