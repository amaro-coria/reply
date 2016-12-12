/**
 * Teknei 2016
 */
package com.teknei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Jorge Amaro
 * 
 *         <pre>
 *  DB Utility functions for reply data from functions stored locally
 *         </pre>
 * 
 * @version 1.0.0
 * @since 1.0.0
 *
 */
@Component
public class ReplyBD {

	/*
	 * Injected value
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;
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

	private static final Logger log = LoggerFactory.getLogger(ReplyBD.class);

	/**
	 * Function calling count(*) from common table. Should return at least 1
	 * result. The table {@code cctm_cata} must exist on the database
	 * 
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
		log.info("Linking BD with params: {} {} {} {} {}", dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny,
				pwdBDDestiny);
	}

	/**
	 * Main function that calls data synchronization from host to host. Must
	 * have correct properties parameters set
	 * 
	 * @return the status output from function
	 */
	public String callDBReply() {
		try {
			log.info("Linking BD with params: {} {} {} {} {}", dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny,
					pwdBDDestiny);
			String sql = "select sitm_disp.repl_data(?, ?, ?, ?, ?);";
			Object result = jdbcTemplate.queryForObject(sql,
					new Object[] { dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny, pwdBDDestiny }, Object.class);
			return result.toString();
		} catch (Exception e) {
			log.error("Error calling BD Reply: {}", e.getMessage());
			return "01";
		}

	}

}
