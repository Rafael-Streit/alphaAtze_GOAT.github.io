package org.systeminfo.systeminfoapi.export;

import com.example.systemmonitor.dto.ProcessInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Slf4j
@Service
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService() {
        this.objectMapper = new ObjectMapper();
    }

    public String exportProcessesToCsv(List<ProcessInfo> processes) {
        log.debug("Exporting {} processes to CSV", processes.size());
        
        try (StringWriter sw = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
                     .withHeader("PID", "Name", "Status", "CPU Usage %", "Memory Bytes", "Thread Count"))) {
            
            for (ProcessInfo process : processes) {
                csvPrinter.printRecord(
                        process.getPid(),
                        process.getName(),
                        process.getStatus(),
                        process.getCpuUsagePercent(),
                        process.getMemoryBytes(),
                        process.getThreadCount()
                );
            }
            
            csvPrinter.flush();
            String csv = sw.toString();
            log.info("Processes exported to CSV - Records: {}", processes.size());
            return csv;
            
        } catch (Exception e) {
            log.error("Error exporting processes to CSV", e);
            throw new RuntimeException("Could not export processes to CSV", e);
        }
    }

    public String exportProcessesToJson(List<ProcessInfo> processes) {
        log.debug("Exporting {} processes to JSON", processes.size());
        
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processes);
            log.info("Processes exported to JSON - Records: {}", processes.size());
            return json;
        } catch (Exception e) {
            log.error("Error exporting processes to JSON", e);
            throw new RuntimeException("Could not export processes to JSON", e);
        }
    }
}
