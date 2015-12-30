package com.stolser.nettyserver.server.data;

import java.net.SocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionData {
	private static AtomicInteger nextId = new AtomicInteger(1);
	private int id;
	private SocketAddress sourceIp;
	private URI uri;
	private Date timestamp;
	private long sentBytes;
	private long recivedBytes;
	private double speed;
	
	public ConnectionData(SocketAddress sourceIp, URI uri, Date timestamp) {
		this.id = nextId.getAndIncrement();
		this.sourceIp = sourceIp;
		this.uri = uri;
		this.timestamp = timestamp;
	}

	public long getSentBytes() {
		return sentBytes;
	}

	public void setSentBytes(long sentBytes) {
		this.sentBytes = sentBytes;
	}

	public long getRecivedBytes() {
		return recivedBytes;
	}

	public void setRecivedBytes(long recivedBytes) {
		this.recivedBytes = recivedBytes;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public SocketAddress getSourceIp() {
		return sourceIp;
	}

	public URI getUri() {
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
