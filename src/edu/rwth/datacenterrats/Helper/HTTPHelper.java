package edu.rwth.datacenterrats.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import android.util.Log;


/**
 * Class that helps us with some of the HTTP Stuff.
 * @author Benjamin Grap
 *
 */
public class HTTPHelper {

    private HttpClient httpclient;
    private HttpResponse response;
    private BasicCookieStore cookieStore;
    private BasicHttpContext httpContext;
    private String cookie="";
    
    /**
     * Creates a new httpclient and sets it up.
     */
    public HTTPHelper(){
    	  this.cookieStore = new BasicCookieStore();
    	  this.httpContext = new BasicHttpContext();
    	  if(cookieStore != null && this.httpContext != null){
    		  this.httpContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
    	  }
    	  this.httpclient = new DefaultHttpClient();
    	  this.httpclient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,false);
    }
    /**
     * Posting Data to a Webserver.
     * Gets a Number of Parameters to Post as NameValuePairs.
     * @param url
     * @param nameValuePairs
     */
    public void postData(String url, List<NameValuePair> nameValuePairs) {
        // Create a new HttpClient and Post Header
       
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
        	httppost.setHeader("Content-Type","application/x-www-form-urlencoded");
        	httppost.addHeader("Cookie",cookie);
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            // Execute HTTP Post Request
            response = this.httpclient.execute(httppost,this.httpContext);
            
            HttpEntity entity = response.getEntity();
            if(entity != null){
            	entity.consumeContent();
            }
            //List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }   
    }
    /**
     * returns the StatusCode of the Last HTTP Interaction.
     * @return
     */
    public int getStatusCode(){
    	if(response != null){
    		return response.getStatusLine().getStatusCode();
    	}else{
    		return 0;
    	}
    }
    /**
     * returns the Location Field of the last HTTP Header.
     * @return
     */
    public String getresponseLocation(){
    	Header location = response.getFirstHeader("Location");
    	return location.toString();
    }
    
    /**
     * Requests URL and returns the Webpage or the String "FAIL".
     * @param url
     * @return
     */
    public String get(String url){
    	try{
    		StringBuilder total = new StringBuilder();
	    	HttpGet httpActivity = new HttpGet(url);
	    	httpActivity.addHeader("Cookie",cookie);
	    	HttpResponse httpResponseActivity = httpclient.execute(httpActivity, httpContext);
	    	response = httpResponseActivity;
	    	int statusActivity = httpResponseActivity.getStatusLine().getStatusCode();
	    	if(httpResponseActivity.getFirstHeader("Set-Cookie") != null){
	    		cookie = httpResponseActivity.getFirstHeader("Set-Cookie").getValue();
	    	}
	    	if (statusActivity == HttpStatus.SC_OK) {
	    		HttpEntity httpEntityActivity = httpResponseActivity.getEntity();
	    		if (httpEntityActivity != null) {
	    			InputStream inputStreamActivity = httpEntityActivity.getContent();
	    		
	    			BufferedReader r = new BufferedReader(new InputStreamReader(inputStreamActivity));
			    
	    			String line;
	    			while ((line = r.readLine()) != null) {
	    				total.append(line + "\n");
	    			}
	    			
	    			
	    			return total.toString();
			    }        
	    	}
	    	//Log.i("HTTPHelper","Status: " + statusActivity);
	    	return "FAIL";
	    	} catch (IOException e){
	    		//Log.i("HTTPHelper","IO Exception Failure.");
	    		return "FAIL";
    	}
    }
    /**
     * returns the Cookies stored in the httpclient.
     * @return
     */
    public String getCookies(){
    	//return cookieStore.getCookies().toString();
    	return cookie;
    }
    /**
     * Requests a Passwort protected URL and returns the Website-Content.
     * 
     * @param url
     * @param username
     * @param password
     * @return
     */
    public String getData(String url,String username, String password) {
    	InputStream content = null;
    	StringBuilder total = new StringBuilder();
		try {
			
			HttpGet httpGet = new HttpGet(url);
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));

			//HttpsGet
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			response = httpclient.execute(httpGet);
			
			content = response.getEntity().getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(content));
		    
		    String line;
		    while ((line = r.readLine()) != null) {
		        total.append(line);
		    }        
		    
        } catch (Exception e) {
			//handle the exception !
		}
        return total.toString();
    }
    /**
     * Requests a Passwort protected URL and returns an InputStream Object to the return Data.
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static InputStream getInputStreamFromUrl(String url, String username, String password) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));
			//HttpsGet
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
        
		} catch (Exception e) {
		
			//handle the exception !
		}
		return content;
    }
        
}
