package com.teknei;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReplyApplicationTests { 

	private static final Logger log = LoggerFactory.getLogger(ReplyApplicationTests.class);
	
	@Autowired
	private ReplyBD reply;
	
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void connectBD(){
		Integer count = reply.callDBUnitTest();
		log.info("Count from DB Unit test: {}", count);
		assertNotNull(count);
	}
	
	@Test
	public void replyBD(){
		reply.callDBReply();
	}

}
