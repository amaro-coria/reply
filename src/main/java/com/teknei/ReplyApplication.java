package com.teknei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ReplyApplication {

	private static final Logger log = LoggerFactory.getLogger(ReplyApplication.class);

	public static void main(String[] args) {
		boolean reply = false;
		boolean test = false;
		if (args == null || args.length == 0) {
			log.info("No known option, invoke following: '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
			log.info("Exiting with code 1");
			System.exit(1);
		} else {
			if (args[0].equalsIgnoreCase("-v")) {
				log.info("TKN_VERSION:{}", "1.0.0");
				System.exit(0);
			} else if(args[0].equalsIgnoreCase("-r")){
				log.info("Runnig to reply");
				reply = true;
			}else if (args[0].equalsIgnoreCase("-t")) {
				log.debug("TKN Unit testing");
				test = true;
			}else if (args[0].equalsIgnoreCase("-h")){ 
				log.info("Application for reply transactional records to master database");
				log.info("Host needs to have the correct version of the PG DB and schemas as well as know the details of the master database");
				log.info("The principal requirement is that the master database is visible to this host");
				log.info("Usage:  '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
				log.info("For specify application.properties use '--spring.config.location=<FULL_CONFIG_PATH_TO_PROPERTIES>' as last command parameter");
				log.info("Example: java -jar reply.jar -t --spring.config.location=/home/teknei/SITM/CONFIG//tkn_reply.properties");
				System.exit(0);
			}else {
				log.info("No known option, invoke following: '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
				log.info("Exiting with code 1");
				System.exit(1);
			}
		}
		ConfigurableApplicationContext ctx = SpringApplication.run(ReplyApplication.class, args);
		ReplyBD bd = ctx.getBean(ReplyBD.class);
		if (test) {
			Integer count = bd.callDBUnitTest();
			log.info("Count testing:{}", count);
			bd.callDBParamTest();
		} else if (reply) {
			String result = bd.callDBReply();
			log.info("Result for BDReply: {}", result);
		}
	}

}
