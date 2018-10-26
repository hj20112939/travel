package com.weixin.travel.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private static final PoolingHttpClientConnectionManager HTTP_CONN_MANAGER;
	private static final ConnectionConfig CONN_CONFIG;
	private static final RequestConfig REQ_CONFIG;
	private static final String USER_AGENT = "travel";
	private static final int TIME_OUT = 10000;
	private final static String CHARSET = "utf-8";

	static {
		HTTP_CONN_MANAGER = new PoolingHttpClientConnectionManager();
		HTTP_CONN_MANAGER.setDefaultMaxPerRoute(10);
		HTTP_CONN_MANAGER.setMaxTotal(100);
		HTTP_CONN_MANAGER.closeIdleConnections(3, TimeUnit.MINUTES);
		CONN_CONFIG = ConnectionConfig.custom().setCharset(Charset.forName("UTF-8")).build();
		REQ_CONFIG = RequestConfig.custom().setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT)
				.setConnectionRequestTimeout(TIME_OUT).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
	}

	private HttpClientUtils() {
	}

	public static String get(String uri) {
		CloseableHttpClient client = getHttpClient();
		HttpGet request = new HttpGet(uri);
		request.setHeader(HTTP.USER_AGENT, USER_AGENT);
		request.setConfig(REQ_CONFIG);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		return result;
	}

	public static String post(String uri) {
		CloseableHttpClient client = getHttpClient();
		HttpPost request = new HttpPost(uri);
		request.setHeader(HTTP.USER_AGENT, USER_AGENT);
		request.setConfig(REQ_CONFIG);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		return result;
	}

	public static String post(String uri, String params) {
		CloseableHttpClient client = getHttpClient();
		HttpPost request = new HttpPost(uri);
		request.setHeader(HTTP.USER_AGENT, USER_AGENT);
		request.setConfig(REQ_CONFIG);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			if (StringUtils.isNotBlank(params)) {
				request.setEntity(new StringEntity(params, "UTF-8"));
			}
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		return result;
	}

	public static String post(String url, String contentType, String encoding, String data) throws Exception{
		CloseableHttpClient client = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(HTTP.USER_AGENT, USER_AGENT);
		httpPost.setConfig(REQ_CONFIG);
		StringEntity entity = new StringEntity(data, encoding);
		entity.setContentType(contentType);
		httpPost.setHeader(HTTP.CONTENT_TYPE, contentType);
		httpPost.setEntity(entity);
		String content = null;
		HttpEntity resEntity = null;
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpPost);
			resEntity = response.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity, encoding);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		return content;
	}


    public static String postJson(String uri, String json) throws Exception {
        CloseableHttpClient client = null;
        if (uri.startsWith("https")) {
            try {
                SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                }).build();
                client = getHttpClient(sslContext);
            } catch (Exception e) {
                throw e;
            }
        } else {
            client = getHttpClient();
        }
        HttpPost request = new HttpPost(uri);
        request.setHeader(HTTP.USER_AGENT, USER_AGENT);
        request.setConfig(REQ_CONFIG);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            request.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
            if (StringUtils.isNotBlank(json)) {
                request.setEntity(new StringEntity(json, "UTF-8"));
            }
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeHttpResponse(response);
            closeHttpClient(client);
        }
        return result;
    }

	public static String postJson(String uri, String json,Header[] headers) throws Exception {
		CloseableHttpClient client = null;
		if (uri.startsWith("https")) {
			try {
				SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
				client = getHttpClient(sslContext);
			} catch (Exception e) {
				throw e;
			}
		} else {
			client = getHttpClient();
		}
		HttpPost request = new HttpPost(uri);
		request.setHeader(HTTP.USER_AGENT, USER_AGENT);
		request.setConfig(REQ_CONFIG);
		request.setHeaders(headers);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			request.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
			if (StringUtils.isNotBlank(json)) {
				request.setEntity(new StringEntity(json, "UTF-8"));
			}
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		return result;
	}

	public static Map postJsonMap(String uri, String json,Header[] headers) throws Exception {
		CloseableHttpClient client = null;
		if (uri.startsWith("https")) {
			try {
				SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				}).build();
				client = getHttpClient(sslContext);
			} catch (Exception e) {
				throw e;
			}
		} else {
			client = getHttpClient();
		}
		HttpPost request = new HttpPost(uri);
		request.setHeader(HTTP.USER_AGENT, USER_AGENT);
		request.setConfig(REQ_CONFIG);
		request.setHeaders(headers);
		CloseableHttpResponse response = null;
		String result = null;
		try {
			request.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
			if (StringUtils.isNotBlank(json)) {
				request.setEntity(new StringEntity(json, "UTF-8"));
			}
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeHttpResponse(response);
			closeHttpClient(client);
		}
		Map map = new HashMap<String,String>();
		map.put("statusCode", response.getStatusLine().getStatusCode());
		map.put("result", result);
		return map;
	}
	private static void closeHttpResponse(CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				// ignored.
			}
		}
	}

	private static void closeHttpClient(CloseableHttpClient client) {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				// ignored.
			}
		}
	}

	private static CloseableHttpClient getHttpClient() {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader(HTTP.USER_AGENT, USER_AGENT));
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(HTTP_CONN_MANAGER)
				.setDefaultConnectionConfig(CONN_CONFIG).setConnectionManagerShared(true).disableCookieManagement()
				.setUserAgent(USER_AGENT).setDefaultHeaders(headers).build();
		return client;
	}

    private static CloseableHttpClient getHttpClient(SSLContext sslContext) throws Exception {
        SSLConnectionSocketFactory sslsf = null;
        if (sslContext != null) {
            new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        }
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(HTTP_CONN_MANAGER)
                .setDefaultConnectionConfig(CONN_CONFIG)
                .setConnectionManagerShared(true)
                .setUserAgent(USER_AGENT)
                .setSSLSocketFactory(sslsf)
                .disableCookieManagement()
                .disableAutomaticRetries()
                .build();
        return httpclient;
    }

}
