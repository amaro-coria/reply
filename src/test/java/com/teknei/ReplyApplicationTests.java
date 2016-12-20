/**
 * Teknei 2016
 */
package com.teknei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test class
 * 
 * @author Jorge Amaro
 * @version 1.0.0
 * 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReplyApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(ReplyApplicationTests.class);
	@Value("${tkn.ip.destiny}")
	private String ipDestiny;
	@Value("${tkn.connection.timeout}")
	private String timeOut;
	@Autowired
	private ReplyBD reply;

	/**
	 * Verifies that the context is loaded
	 */
	@Test
	public void contextLoads() {
		log.debug("Context loaded");
	}

	/**
	 * Verifies that the connection to the database could be made
	 */
	@Test
	public void connectBD() {
		Integer count = reply.callDBUnitTest();
		log.info("Count from DB Unit test: {}", count);
		assertNotNull(count);
	}

	/**
	 * Try to reply form database function
	 */
	@Test
	public void replyBD() {
		reply.dbReply();
	}

	/**
	 * Check if the server can reach the destinations url
	 * @param url the url to be reached
	 */
	@Test
	public void checkStatus() {
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
			assertEquals(code, HttpURLConnection.HTTP_OK);
			log.debug("Connection successful");
		} catch (Exception e) {
			log.error("Error connecting the remote server url {} with error: {}", url, e.getMessage());
		}
	}

}
