package com.liufan.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {

	public static void copy(InputStream is, OutputStream os) {
		if (is == null || os == null) {
			throw new NullPointerException("copy方法的输入输出流不得为空");
		}
		int len = -1;
		byte[] buff = new byte[1024];
		try {
			while (((len = is.read(buff)) != -1)) {
				os.write(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
