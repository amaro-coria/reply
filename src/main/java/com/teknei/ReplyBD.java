package com.teknei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReplyBD {

	@Autowired
	private JdbcTemplate jdbcTemplate;
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

	public Integer callDBUnitTest() {
		String sql = "select count(*) from sitm_disp.cctm_cata;";
		Object o = jdbcTemplate.queryForObject(sql, Object.class);
		try{
			Integer i = Integer.parseInt(o.toString());
			return i;
		}catch(NumberFormatException ne){
			return null;
		}
	}
	
	public void callDBParamTest(){
		log.info("Linking BD with params: {} {} {} {} {}", dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny, pwdBDDestiny);
	}

	public String callDBReply() {
		try{
			log.info("Linking BD with params: {} {} {} {} {}", dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny, pwdBDDestiny);
			String sql = "select sitm_disp.repl_data(?, ?, ?, ?, ?);";
			Object result = jdbcTemplate.queryForObject(sql,
					new Object[] { dbDestiny, ipDestiny, portBDDestiny, usuaBDDestiny, pwdBDDestiny }, Object.class);
			return result.toString();
		}catch(Exception e){
			log.error("Error calling BD Reply: {}", e.getMessage());
			return "01";
		}
		
	}

}
