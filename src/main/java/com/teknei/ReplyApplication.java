/**
 * Teknei 2016
 */
package com.teknei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main class for replication functions
 *
 * @author Jorge Amaro
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class ReplyApplication {

    private static final Logger log = LoggerFactory.getLogger(ReplyApplication.class);

    /**
     * Entry point for the application
     *
     * @param args '-v' for version, '-h' for help, '-t' for testing, '-r' for
     *             reply
     */
    public static void main(String[] args) {
        boolean reply = false;
        boolean test = false;
        if (args == null || args.length == 0) {
            log.info(
                    "No known option, invoke following: '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
            log.info("Exiting with code 1");
            System.exit(1);
        } else {
            if (args[0].equalsIgnoreCase("-v")) {
                log.info("TKN_VERSION:{}", "1.0.1");
                System.exit(0);
            } else if (args[0].equalsIgnoreCase("-r")) {
                log.info("Runnig to reply");
                reply = true;
            } else if (args[0].equalsIgnoreCase("-t")) {
                log.debug("TKN Unit testing");
                test = true;
            } else if (args[0].equalsIgnoreCase("-h")) {
                log.info("Application for reply transactional records to master database");
                log.info(
                        "Host needs to have the correct version of the PG DB and schemas as well as know the details of the master database");
                log.info("The principal requirement is that the master database is visible to this host");
                log.info("Usage:  '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
                log.info(
                        "For specify application.properties use '--spring.config.location=<FULL_CONFIG_PATH_TO_PROPERTIES>' as last command parameter");
                log.info(
                        "Example: java -jar reply.jar -t --spring.config.location=/home/teknei/SITM/CONFIG/tkn_reply.properties");
                System.exit(0);
            } else {
                log.info(
                        "No known option, invoke following: '-v' for version , '-t' for testing , '-h' for help, '-r' for reply");
                log.info("Exiting with code 1");
                System.exit(1);
            }
        }
        ConfigurableApplicationContext ctx = SpringApplication.run(ReplyApplication.class, args);
        ReplyBD bd = ctx.getBean(ReplyBD.class);
        ReplyConnection cnx = ctx.getBean(ReplyConnection.class);
        if (test) {
            Integer count = bd.callDBUnitTest();
            log.info("Count testing:{}", count);
            bd.callDBParamTest();
            boolean statusConnection = cnx.checkStatus();
            log.info("Connection status:{}", statusConnection);
        } else if (reply) {
            boolean statusConnection = cnx.checkStatus();
            log.info("##############################################");
            log.info("Connection status:{}", statusConnection);
            if (!statusConnection) {
                log.info("No connection available, aborting reply. ");
                log.info("Exit with code '1'");
                System.exit(1);
            }
            log.info("##############################################");
            String result = bd.dbReply();
            log.info("##############################################");
            log.info("Result for BDReply: {}", result);
            log.info("##############################################");
            try {
                Integer i = Integer.parseInt(result);
                System.exit(i);
            } catch (Exception e) {
                System.exit(1);
            }
        }
    }

}
