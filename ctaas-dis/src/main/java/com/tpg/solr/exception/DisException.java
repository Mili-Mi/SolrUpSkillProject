package com.tpg.solr.exception;

public class DisException extends Exception {

	public DisException(String msg, Exception e) {
		super(msg, e);
	}

	public DisException(String msg) {
		super(msg);
	}

}
