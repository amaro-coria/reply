/**
 * Teknei 2016
 */
package com.teknei;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Jorge Amaro
 *         <p>
 *         <pre>
 *                                          DB Utility functions for reply data from functions stored locally
 *                                                 </pre>
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ReplyBD {

    /*
     * Injected value
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /*
     * Injected value
     */
    @Autowired
    private ReplyLog logReply;
    /*
     * Injected properties
     */
    @Value("${tkn.db.destiny.name}")
    private String dbDestiny;
    @Value("${tkn.ip.destiny}")
    private String ipDestiny;
    @Value("${tkn.port.bd.destiny}")
    private String portBDDestiny;
    @Value("${tkn.user.bd.destiny}")
    private String usuaBDDestiny;
    @Value("${tkn.pwd.bd.destiny}")
    private String pwdBDDestiny;
    @Value("${tkn.reply.table}")
    private String replyTable;
    @Value("${tkn.timeout}")
    private String timeOut;
    @Value("${tkn.timeout.apply}")
    private String applyTimeout;
    private final static int RE_INVOKE = 2;

    private static final Logger log = LoggerFactory.getLogger(ReplyBD.class);

    /**
     * Function calling count(*) from common table. Should return at least 1
     * result. The table {@code cctm_cata} must exist on the database
     *
     * @return the number of records
     */
    public Integer callDBUnitTest() {
        String sql = "select count(*) from sitm_disp.cctm_cata;";
        Object o = jdbcTemplate.queryForObject(sql, Object.class);
        try {
            Integer i = Integer.parseInt(o.toString());
            return i;
        } catch (NumberFormatException ne) {
            return null;
        }
    }

    /**
     * Shows the parameters that call this function
     */
    public void callDBParamTest() {
        log.info("Linking BD with params: {} , {} , {} , {} , {} , {}", dbDestiny, ipDestiny, portBDDestiny,
                usuaBDDestiny, pwdBDDestiny, replyTable);
    }

    /**
     * Public method for managed db reply
     *
     * @return the status of the transaction
     */
    public String dbReply() {
        log.info("Running reply data");
        log.info("Linking BD with params: {} , {} , {} , {} , {} , {}", dbDestiny, ipDestiny, portBDDestiny,
                usuaBDDestiny, pwdBDDestiny, replyTable);
        String response = "0";
        if (applyTimeout.equalsIgnoreCase("Y")) {
            log.info("Reply with timeout");
            response = callDBReplyWithTimeout();
        } else {
            log.info("Reply without timeout");
            response = callDBReply();
        }
        return response;
    }

    /**
     * Main function that calls data synchronization from host to host. Must
     * have correct properties parameters set
     *
     * @return the status output from function
     */
    private String callDBReplyWithTimeout() {
        log.info("Running with {} minutes in timeout", timeOut);
        Integer iMins = 9;
        try {
            iMins = Integer.parseInt(timeOut);
        } catch (Exception e) {
            log.info("Problem setting minutes in timeout, default is 9");
            iMins = 9;
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        @SuppressWarnings({"unchecked", "rawtypes"})
        Future<String> future = executor.submit(new Callable() {
            public String call() throws Exception {
                String bdReply = callDBReply();
                return bdReply;
            }
        });
        try {
            String result = future.get(iMins, TimeUnit.MINUTES);
            return result;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            future.cancel(true);
            log.error("Timeout reached, aborting,");
        }
        executor.shutdownNow();
        return "01";
    }

    /**
     * Main function that calls data synchronization from host to host. Must
     * have correct properties parameters set
     *
     * @return the status output from function
     */
    private String callDBReply() {
        String overallResult = "0";
        log.info("Linking BD with params: {} , {} , {} , {} , {} , {}", dbDestiny, ipDestiny, portBDDestiny,
                usuaBDDestiny, pwdBDDestiny, replyTable);
        String[] replyModes = replyTable.split(",");
        for (String replyMode : replyModes) {
            try {
                String sqlCont = "select sitm_disp.cont_repl(?);";
                String sql = "select sitm_disp.repl_data(?, ?, ?, ?, ?, ?);";
                Integer intReplyTable = 0;
                try {
                    intReplyTable = Integer.parseInt(replyMode);
                } catch (Exception e) {
                    intReplyTable = 0;
                }
                log.info("#########################################");
                log.info("Reply request for reply mode: {}", replyMode);
                log.info("Calling repl_cont with params: {} ", intReplyTable);
                Object resultPre = jdbcTemplate.queryForObject(sqlCont, new Object[]{intReplyTable}, Object.class);
                log.info("Result for repl_cont: {}", resultPre);
                Object result = jdbcTemplate.queryForObject(sql, new Object[]{dbDestiny, ipDestiny, portBDDestiny,
                        usuaBDDestiny, pwdBDDestiny, intReplyTable}, Object.class);
                log.info("Reply mode: {} , Reply partial result: {}", replyMode, result.toString());
                log.info("Calling repl_cont with params: {}  ", intReplyTable);
                Object resultPost = jdbcTemplate.queryForObject(sqlCont, new Object[]{intReplyTable}, Object.class);
                log.info("Result for repl_cont: {}", resultPost);
                String partialResult = result.toString();
                if (partialResult.contains("\\|")) {
                    String[] splitResult = partialResult.split("\\|");
                    String statisticData = splitResult[1];
                    partialResult = splitResult[0];
                    logReply.writeLog(intReplyTable, statisticData);
                } else {
                    log.info("No statistics recognized");
                }
                try {
                    int partialResultInt = Integer.parseInt(partialResult);
                    if (partialResultInt == 0 && overallResult.equals("0")) {
                        overallResult = "0";
                    } else if (partialResultInt == RE_INVOKE) {
                        log.info("Partial result reached, ready to new attempt...");
                        overallResult = partialResult;
                        break;
                    } else {
                        overallResult = partialResult;
                    }
                } catch (NumberFormatException ne) {
                    log.error("No '0' or number result");
                    overallResult = partialResult;
                }
            } catch (Exception e) {
                log.error("Error calling BD Reply: {}", e.getMessage());
                overallResult = "1";
            }
        }
        System.out.println("##############################################");
        System.out.println("   ###########  ###     ###   #####     ###");
        System.out.println("       ###      ###   ####    ### ###   ###");
        System.out.println("       ###      ### #####     ###  ###  ###");
        System.out.println("       ###      ######        ###   ### ###");
        System.out.println("       ###      ###  ####     ###    ######");
        System.out.println("       ###      ####  ####    ###     #####");
        System.out.println("##############################################");
        log.info("Overall result: {}", overallResult);
        return overallResult;
    }

}
