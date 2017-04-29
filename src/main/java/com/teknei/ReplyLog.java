//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.teknei;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReplyLog {
    @Value("${tkn.log.reply}")
    private String logPath;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(ReplyLog.class);

    public ReplyLog() {
    }

    public String writeLog(Integer mode, String data) {
        log.info("Attempt to write statistics in: {}", this.logPath);
        String[] dataSeparate = data.split(",");

        try {
            String total = dataSeparate[0];
            String pendings = dataSeparate[1];
            StringBuilder sb = new StringBuilder("Results from mode:");
            sb.append(mode);
            sb.append(" :::: Totals - ");
            sb.append(total);
            sb.append(" :::: Pendings - ");
            sb.append(pendings);
            log.info("Statistics: {}", sb.toString());
            FileUtils.writeStringToFile(new File(this.logPath), sdf.format(new Date()));
            FileUtils.writeStringToFile(new File(this.logPath), sb.toString());
            return "00";
        } catch (IOException var7) {
            log.info("Unable to write in file log: {}", var7.getMessage());
            return "0E";
        }
    }
}
