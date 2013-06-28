/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poc.opensrs;

    
import com.flowbuilder.utils.Globals;
import com.flowbuilder.utils.Log;
import java.io.IOException;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

//import javax.mail.Header;
import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.sf.json.JSONObject;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
//import org.apache.commons.httpclient.methods.*;
//import org.apache.commons.httpclient.protocol.*;

/**
 *
 * @author Administrator
 */
public class OpenSRSHttpClient {

    private String privateKey;
    private String host;
    private int port;
    private String userName;
    private Header [] headers = null;
    
    
    public OpenSRSHttpClient (String host, int port, String userName, String privateKey) {
        this.host=host;
        this.port = port;
        this.userName = userName;
        this.privateKey = privateKey;
    }
    protected String md5Sum(String str) {
        String sum = new String();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            sum = String.format("%032x", new BigInteger(1,
            md5.digest(str.getBytes())));
        } 
        catch (Exception ex) {}
        return sum;
    }
    
    public String getSignature(String xml) {
        return md5Sum(md5Sum(xml + privateKey) + privateKey);
    }
    
    public String sendJSONRequest(JSONObject json) throws Exception {
        
        DefaultHttpClient client = getNewHttpClient();
        //String portStr = String.valueOf(port);
        
        //SSLSocketFactory sf =  
        //sf.setHostnameVerifier(new AllowAllHostnameVerifier()); 
        //SSLContext sslContext = SSLContext.getInstance("TLS");                
        //sslContext.init(kms, tms, null);
        //SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
        //Protocol.registerProtocol("https", new Protocol("https", new SSLSocketFactoryImpl(), 443));
        //String signature = getSignature(xml);
        String uri = "https://" + host;
        
                
        HttpPost postRequest = new HttpPost(uri);
        //postRequest.setHeader("jsonRequest",json.toString() ); //
        StringEntity se = new StringEntity( json.toString());   
        se.setContentEncoding("UTF-8");
        //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 
        //se.setContentType("application/json");
        postRequest.setEntity(se); 
        
        //org.apache.axis2.json.JSONDataSource js=new org.apache.axis2.json.JSONDataSource(null, userName);
        
        if (Globals.log!=null) Globals.log.write(Log.INFO, "Sending https request....."+postRequest.getURI().toString());
        HttpResponse hResp = null;
        try {
            hResp = client.execute(postRequest);
        }
        catch (Exception ex) {
            throw new Exception("Sending post got exception ", ex);
        }
        
        HttpEntity he = (HttpEntity)hResp.getEntity();                                
        
        if (Globals.log!=null)Globals.log.write(Log.INFO, "Content length : "+he.getContentLength());        
        if(hResp!=null){   
            //InputStream in = ; //Get the data in the entity   
            //String a=new String(hResp.getEntity().getContent().);   
            //Log.i("Read from Server",a);   
        }   

        //InputStream is=he.getContent();                        

        //byte[] byts=EntityUtils.toByteArray(he);

        //is.read(byts);
        
        return EntityUtils.toString(he);                                
        
    }
    
    public String sendRequest(String xml) throws Exception {
        
        DefaultHttpClient client = new DefaultHttpClient();
        //client.setConnectionTimeout(60000);
        //client.setTimeout(60000);
        //String response = new String();
        String portStr = String.valueOf(port);
        
        //Protocol.registerProtocol("https", new Protocol("https", new SSLSocketFactoryImpl(), port));
        String signature = getSignature(xml);
        String uri = "https://" + host + ":" + portStr + "/";
        
        
        HttpPost postRequest = new HttpPost(uri);
    //    postRequest.addHeader("Content-Length",String.valueOf(xml.length()));
        postRequest.addHeader("Content-Type", "text/xml");
        postRequest.addHeader("X-Signature", signature);
        postRequest.addHeader("X-Username", userName);
        
        //postRequest.
        //CharArrayBuffer cab = new CharArrayBuffer(0);
        //cab.append(xml);
        
        postRequest.setEntity(new StringEntity(xml));
        if (Globals.log!=null) Globals.log.write(Log.INFO, "Sending https request....."+postRequest.getURI().toString());
        BasicHttpResponse hResp = null;
        try {
            hResp = (BasicHttpResponse)client.execute(postRequest);
        }
        catch (Exception ex) {
            throw new Exception("Sending post got exception ", ex);
        }
        //response = postRequest.getResponseBodyAsString();
        headers = postRequest.getAllHeaders();
        
        for (int i=0; i<headers.length; i++) {
            Header h=headers[i];
            if (Globals.log!=null)Globals.log.write(Log.INFO, h.getName()+" - "+h.getValue());        
        }
        
       BasicManagedEntity he = (BasicManagedEntity)hResp.getEntity();                                
        
        if (Globals.log!=null)Globals.log.write(Log.INFO, "Content length : "+he.getContentLength());        
        //InputStream is=he.getContent();                        

        //byte[] byts=EntityUtils.toByteArray(he);

        //is.read(byts);
        
        return EntityUtils.toString(he);                                
                
    }
    
    public String getPrivateKey() {return privateKey;}
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey;}
    public String getHost() { return host;}
    public void setHost(String host) { this.host = host;}
    public int getPort() {return port;}
    public void setPort(int port) { this.port = port; }
    public String getUserName() { return userName;    }
    public void setUserName(String userName) {    this.userName = userName;    }
    public Header[] getHeaders() {    return headers;    }
    public void setHeaders(Header[] headers) {    this.headers = headers;    }

    public static void main(String[] args) {
        
        String privateKey = "f6d978fcc4260dca112ea859ccafa60a0924ee8f8838692fa07a3ccb13cc8585d45113e5c26b59d5107a3f7d380a8f652a1260a106b22cc9";
        String userName = "bcsg";
        String host="horizon.opensrs.net";
        int port = 55443;
        String xml=
        "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>"+
        "<!DOCTYPE OPS_envelope SYSTEM 'ops.dtd'>"+
        "<OPS_envelope>"+
        "<header>"+
        "<version>0.9</version>"+
        "<msg_id>2.21765911726198</msg_id>"+
        "<msg_type>standard</msg_type>"+
        "</header>"+
        "<body>"+
        "<data_block>"+
        "<dt_assoc>"+
        "<item key='attributes'>"+
        "<dt_assoc>"+
        "<item key='domain'>test-1061911771844.com</item>"+
        "<item key='pre-reg'>0</item>"+
        "</dt_assoc>"+
        "</item>"+
        "<item key='object'>DOMAIN</item>"+
        "<item key='action'>NAME_SUGGEST</item>"+
        "<item key='protocol'>XCP</item>"+
        "</dt_assoc>"+
        "</data_block>"+
        "</body>"+
        "</OPS_envelope>";
        OpenSRSHttpClient sslclient = new OpenSRSHttpClient(host,port,userName,privateKey);
        try {
            String response = sslclient.sendRequest(xml);
            System.out.println("\nResponse is:\n"+response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }       
    
    public class SSLSocketFactoryImpl  {
        
        private TrustManager[] getTrustManager() {
            TrustManager[] trustAllCerts = new TrustManager[]{
                                                new X509TrustManager() {
                                                    
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
    
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
             };
            return trustAllCerts;
        }
                    
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            
            TrustManager[] trustAllCerts = getTrustManager();
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                SocketFactory socketFactory =
                HttpsURLConnection.getDefaultSSLSocketFactory();
                return socketFactory.createSocket(host, port);
            }
            catch (Exception ex) {
                throw new UnknownHostException("Problems to connect " + host +  ex.toString());
            }
        }
        
        public Socket createSocket(Socket socket, String host, int port, boolean flag) throws IOException, UnknownHostException {
            
            TrustManager[] trustAllCerts = getTrustManager();
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                SocketFactory socketFactory =
                HttpsURLConnection.getDefaultSSLSocketFactory();
                return socketFactory.createSocket(host, port);
            }
            catch (Exception ex) {
                throw new UnknownHostException("Problems to connect " + host + ex.toString());
            }
        }
    
        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) 
                                                                        throws IOException, UnknownHostException {
            TrustManager[] trustAllCerts = getTrustManager();
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                SocketFactory socketFactory =
                HttpsURLConnection.getDefaultSSLSocketFactory();
                return socketFactory.createSocket(host, port, clientHost, clientPort);
            }
            catch (Exception ex) {
                throw new UnknownHostException("Problems to connect " + host +  ex.toString());
            }
        }



    }
    
    public class MySSLSocketFactory extends SSLSocketFactory { 
        SSLContext sslContext = SSLContext.getInstance("TLS"); 

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException { 
            super(truststore); 

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
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException { 
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose); 
        } 

        @Override 
        public Socket createSocket() throws IOException { 
            return sslContext.getSocketFactory().createSocket(); 
        } 
       } 
        public DefaultHttpClient getNewHttpClient() { 
            try { 
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
                trustStore.load(null, null); 

                SSLSocketFactory sf = new MySSLSocketFactory(trustStore); 
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 

                HttpParams params = new BasicHttpParams(); 
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); 
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); 

                SchemeRegistry registry = new SchemeRegistry(); 
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
                registry.register(new Scheme("https", sf, 443)); 

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry); 

                return new DefaultHttpClient(ccm, params); 
            } catch (Exception e) { 
                return new DefaultHttpClient(); 
            } 
        } 
    
    
}