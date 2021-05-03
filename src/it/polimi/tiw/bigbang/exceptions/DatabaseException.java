package it.polimi.tiw.bigbang.exceptions;

public class DatabaseException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String body;

	public DatabaseException(String body) {
		this.body = body;
	}

	public String getBody() {
		return body;
	}

}
