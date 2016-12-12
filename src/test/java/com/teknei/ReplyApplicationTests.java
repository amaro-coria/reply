/**
 * Teknei 2016
 */
package com.teknei;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test class
 * @author Jorge Amaro
 * @version 1.0.0
 * 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReplyApplicationTests { 

	private static final Logger log = LoggerFactory.getLogger(ReplyApplicationTests.class);
	
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
	public void connectBD(){
		Integer count = reply.callDBUnitTest();
		log.info("Count from DB Unit test: {}", count);
		assertNotNull(count);
	}
	
	/**
	 * Try to reply form database function
	 */
	@Test
	public void replyBD(){
		reply.callDBReply();
	}

}
