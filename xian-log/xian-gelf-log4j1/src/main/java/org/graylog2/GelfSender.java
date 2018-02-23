package org.graylog2;

public interface GelfSender {
	public static final int DEFAULT_PORT = 12201;

	public GelfSenderResult sendMessage(GelfMessage message);
	public void close();
}
