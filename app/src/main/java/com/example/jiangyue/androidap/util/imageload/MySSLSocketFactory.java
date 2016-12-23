package com.example.jiangyue.androidap.util.imageload;

import android.util.Log;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MySSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = null;

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        sslContext = SSLContext.getInstance("TLS");
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            public X509Certificate[] getAcceptedIssuers() {

                return null;

            }
        };
        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {
        Log.w("MySSLSocketFactory", "socket--->" + socket);
        Log.w("MySSLSocketFactory", "host--->" + host);
        Log.w("MySSLSocketFactory", "port--->" + port);
        Log.w("MySSLSocketFactory", "autoClose--->" + autoClose);
        if (port == -1) {
            port = 443;
        }
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);

    }

    @Override
    public Socket createSocket() throws IOException {

        return sslContext.getSocketFactory().createSocket();

    }
}
