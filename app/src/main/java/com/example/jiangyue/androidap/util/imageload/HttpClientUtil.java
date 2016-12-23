package com.example.jiangyue.androidap.util.imageload;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn
 * 
 */
public class HttpClientUtil {
	private final static String TAG = "HttpClientUtil";
	private final static int TIMEOUT = 20 * 1000;

	// end zhucl 2013.4.16
	private static HttpClient httpClient = null;

	// 解决javax.net.ssl.SSLPeerUnverifiedException no peer certificate
	public static HttpClient getNewHttpClient(Context context) {
		try {
			if (httpClient == null) {
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);
				SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, "gbk");
				HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
				HttpConnectionParams.setSoTimeout(params, TIMEOUT);
				ConnManagerParams.setTimeout(params, TIMEOUT);
				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));
				ClientConnectionManager ccm = new ThreadSafeClientConnManager(
						params, registry);
				httpClient = new DefaultHttpClient(ccm, params);
			}
			httpClient = setNetWork(context, httpClient);
		} catch (Exception e) {
		}
		return httpClient;
	}

    private static HttpClient setNetWork(Context context, HttpClient client) {
        String netType = NetworkManager
                .getNetWorkType((ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (!CommonConstants.WIFI_STATE.equalsIgnoreCase(netType)) {
            if (!Utility.isOPhone()) {
                Map<String, Object> map = NetworkManager.getProxy();
                if (map != null && !map.isEmpty()) {
                    if (android.os.Build.VERSION.SDK_INT <= 7) {
                        String proxyHost = (String) map
                                .get(NetworkManager.PROXY_HOST);
                        int proxyPort = (Integer) map
                                .get(NetworkManager.PROXY_PORT);
                        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                        client.getParams().setParameter(
                                ConnRoutePNames.DEFAULT_PROXY, proxy);
                    }
                } else {
                    // cmnet set proxy
                    client.getParams().setParameter(
                            ConnRoutePNames.DEFAULT_PROXY, null);
                }
            }

            // modify by wangtao on 20120712 start
        } else if (CommonConstants.WIFI_STATE.equalsIgnoreCase(netType)) {
            client.getParams()
                    .setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
        }
        return client;

    }

	private static HttpPost addParams(HttpPost httpPost, String json) {
		UrlEncodedFormEntity urlEncode = null;
		List<NameValuePair> params = null;
		try {
			LogUtils.v(TAG, "addParams httpPost------>" + httpPost);
			params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("jsonRequest", json));
			LogUtils.v(TAG, "params size " + params.size());
			urlEncode = new UrlEncodedFormEntity(params, "GBK");
			httpPost.setEntity(urlEncode);
		} catch (Exception e) {
		} finally {
			if (params != null) {
				params.clear();
				params = null;
			}
			if (urlEncode != null) {
				urlEncode = null;
			}

		}
		return httpPost;
	}

	// modify by qianch for 修改下载方法 start
	public static boolean downloadZipFile(Context context, String url,
			String zipPath, String json) throws Exception {
		HttpPost httpPost = null;
		HttpResponse httpResponse = null;
		HttpEntity entity = null;
		InputStream in = null;
		FileOutputStream out = null;
		File file = null;
		try {
			file = new File(zipPath);
			httpClient = getNewHttpClient(context);
			httpPost = new HttpPost(url);
			httpResponse = httpClient.execute(addParams(httpPost, json));
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				// 获取响应实体
				entity = httpResponse.getEntity();

				long contentlength = entity.getContentLength();
				Log.d(TAG, "Zip contentlength：" + contentlength);
				if (!(contentlength > 0)) {
					Log.d(TAG, "Zip File is not exist");
					return false;
				}

				in = entity.getContent();

				out = new FileOutputStream(file);

				byte[] bytes = new byte[4096];
				int c;

				Log.v(TAG, "server msb file:begin: \n");
				while ((c = in.read(bytes)) != -1) {
					String content = new String(bytes, 0, c);
					Log.v(TAG, content);
					out.write(bytes, 0, c);
				}
				out.flush();
				// add by qianch for 流量统计 start
				// FlowUtil.recordFlow(contentlength);
				// add by qianch for 流量统计 end
				if (contentlength == file.length()) {
					return true;
				} else {
					if (null != file && file.exists()) {
						file.delete();
					}

				}
				LogUtils.v(TAG, "server msb end: \n");
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			if (null != file && file.exists()) {
				file.delete();
			}
			if (httpPost != null) {
				httpPost.abort();
			}

		} finally {
			if (null != out) {
				out.close();
			}
			if (null != in) {
				in.close();
			}
			if (entity != null) {
				entity.consumeContent();
			}
			if (httpPost != null) {
				httpPost.abort();
			}
			if (httpClient != null) {
				httpClient.getConnectionManager().closeExpiredConnections();
			}
		}
		return false;
	}

	@SuppressWarnings("finally")
	public static File downloadImageFile(Context context, String url, File file) {
		HttpEntity entity = null;
		InputStream conIn = null;
		DataInputStream in = null;
		OutputStream out = null;
		httpClient = getNewHttpClient(context);
		HttpGet httpGet = null;
		long totalSize = 0;
		try {
			long startTime = System.currentTimeMillis();
			LogUtils.v(TAG, url + " downImage start-----" + startTime);
			httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			LogUtils.v(TAG, url + " downloadImageFile httpResponse --->"
					+ httpResponse);
			if (httpResponse != null) {
				long endTime = System.currentTimeMillis();
				LogUtils.v(TAG, url + " downImage end-----" + endTime
						+ " use time :" + ((endTime - startTime) / 1000));
				StatusLine line = httpResponse.getStatusLine();
				LogUtils.v(TAG, url + " downloadImageFile line --->" + line);
				if (line != null) {
					int responseCode = line.getStatusCode();
					if (responseCode == HttpStatus.SC_OK) {
						entity = httpResponse.getEntity();
						if (entity != null) {
							conIn = entity.getContent();
							totalSize = entity.getContentLength();
							// modify by qianch on 20130227 start
							// FlowUtil.recordFlow(entity.getContentLength());
							// modify by qianch on 20130227 end

							in = new DataInputStream(conIn);
							out = new FileOutputStream(file);
							byte[] buffer = new byte[1024];
							int byteread = 0;
							while ((byteread = in.read(buffer)) != -1) {
								out.write(buffer, 0, byteread);
							}
						} else {
							if (file != null) {
								file.delete();
								file = null;
							}
						}

					} else {
						LogUtils.v(
								"downImage",
								url
										+ " downLoadImage Server return error, response code = "
										+ responseCode);
						if (file != null) {
							file.delete();
							file = null;
						}
					}
				} else {
					if (file != null) {
						file.delete();
						file = null;
					}
					LogUtils.v("downImage", url
							+ " Server return error, StatusLine  " + line);
				}

			} else {
				if (file != null) {
					file.delete();
					file = null;
				}
				LogUtils.v("downImage", url
						+ " Server return error, httpResponse  " + httpResponse);
			}

		} catch (Exception e) {
			if (file != null) {
				file.delete();
				file = null;
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		} finally {
			if (file != null) {
				if (file.length() != totalSize) {
					file.delete();
				}
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conIn != null) {
					conIn.close();
				}
				if (entity != null) {
					entity.consumeContent();
				}
				if (httpGet != null) {
					httpGet.abort();
					httpGet = null;
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String getString(InputStream is) {
        BufferedReader reader = null;
        StringBuffer responseText = null;
        String readerText = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "gb2312"));
            responseText = new StringBuffer();
            readerText = reader.readLine();
            while(readerText != null){
                responseText.append(readerText);
                responseText.append(System.getProperty("line.separator"));
                readerText = reader.readLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseText.toString();
    }
}