package com.liufan.xhttp.common;

import java.io.Serializable;

public class CommonBean<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	public boolean success;
	public String msg;
	public T data;
	@Override
	public String toString() {
		return "CommonBean [success=" + success + ", msg=" + msg + ", data=" + data + "]";
	}
	
	
}
