/**
 * Copyright (C) 2013 CLXY Studio.
 * This content is released under the (Link Goes Here) MIT License.
 * http://en.wikipedia.org/wiki/MIT_License
 */
package com.qidi.uploadfile;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;
import repack.org.apache.http.HttpResponse;
import repack.org.apache.http.HttpStatus;
import repack.org.apache.http.client.HttpClient;
import repack.org.apache.http.client.methods.HttpPost;
import repack.org.apache.http.conn.scheme.PlainSocketFactory;
import repack.org.apache.http.conn.scheme.Scheme;
import repack.org.apache.http.conn.scheme.SchemeRegistry;
import repack.org.apache.http.conn.ssl.SSLSocketFactory;
import repack.org.apache.http.entity.mime.MultipartEntity;
import repack.org.apache.http.entity.mime.content.ByteArrayBody;
import repack.org.apache.http.entity.mime.content.ContentBody;
import repack.org.apache.http.entity.mime.content.StringBody;
import repack.org.apache.http.impl.client.DefaultHttpClient;
import repack.org.apache.http.impl.conn.PoolingClientConnectionManager;
import repack.org.apache.http.params.BasicHttpParams;
import repack.org.apache.http.params.HttpConnectionParams;
import repack.org.apache.http.params.HttpParams;


/**
 * Use http client to upload.
 * @author clxy
 */
public class ApacheHCUploader implements Uploader {

	private static HttpClient client = createClient();
//	private static final Log log = LogFactory.getLog(ApacheHCUploader.class);

	@Override
	public void upload(Part part) {

		String partName = part.getName();
		Map<String, ContentBody> params = new HashMap<String, ContentBody>();
		Log.e("TAG", partName);
		params.put(Config.keyFile, new ByteArrayBody(part.getContent(), partName));
		try {
			params.put(Config.keyFileName, new StringBody(partName));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post(params);
//		log.debug(partName + " uploaded.");
	}

	@Override
	public void done(String fileName, long partCount) {

		Map<String, ContentBody> params = new HashMap<String, ContentBody>();
		try {
			params.put(Config.keyFileName, new StringBody(fileName));
			params.put(Config.keyPartCount, new StringBody(String.valueOf(partCount)));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		post(params);
//		log.debug(fileName + " notification is done.");
	}

	private void post(Map<String, ContentBody> params) {

		HttpPost post = new HttpPost(Config.url);
		MultipartEntity entity = new MultipartEntity();
		for (Entry<String, ContentBody> e : params.entrySet()) {
			entity.addPart(e.getKey(), e.getValue());
		}
		post.setEntity(entity);

		try {
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				throw new RuntimeException("Upload failed.");
			}
		} catch (Exception e) {
			post.abort();
			throw new RuntimeException(e);
		} finally {
			post.releaseConnection();
		}
	}

	/**
	 * The timeout should be adjusted by network condition.
	 * @return
	 */
	private static HttpClient createClient() {

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schReg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager ccm = new PoolingClientConnectionManager(schReg);
		ccm.setMaxTotal(Config.maxUpload);

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, Config.timeOut);

		return new DefaultHttpClient(ccm, params);
	}
}
