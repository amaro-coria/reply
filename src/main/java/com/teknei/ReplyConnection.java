/**
 * Teknei 2016
 */
package com.teknei;

import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for testing if there is an active connection to the server
 * 
 * @author Jorge Amaro
 *
 */
@Component
public class ReplyConnection {

	@Value("${tkn.ip.destiny}")
	private String ipDestiny;
	@Value("${tkn.connection.timeout}")
	private String timeOut;

	private static final Logger log = LoggerFactory.getLogger(ReplyConnection.class);

	/**
	 * Check if the process can reach the destination server
	 * 
	 * @return true if the server is reachable, false otherwise
	 */
	public boolean checkStatus() {
		String url = "";
		Integer tOut = null;
		if(timeOut == null){
			log.debug("No timeout specified, setting 10 seconds");
			tOut = 10;
		}else{
			try{
				tOut = Integer.parseInt(timeOut);
			}catch(Exception e){
				log.error("Error setting timeout property, setting 10 seconds");
				tOut = 10;
			}
		}
		try {
			StringBuilder sb = new StringBuilder("http://");
			sb.append(ipDestiny);
			sb.append(":");
			sb.append("8080");
			URL siteURL = new URL(sb.toString());
			url = sb.toString();
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setConnectTimeout(1000 * tOut); //send in millis
			connection.setRequestMethod("GET");
			connection.connect();
			int code = connection.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				log.info("Connection successful");
				return true;
			}
		} catch (Exception e) {
			log.error("Error connecting the remote server url {} with error: {}", url, e.getMessage());
		}
		return false;
	}
}
