package org.systeminfo.systeminfoapi.contoller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.systeminfo.systeminfoapi.export.ExportService;
import org.systeminfo.systeminfoapi.service.ProcessService;

@Slf4j
@RestController
@RequestMapping("/api/monitoring/export")
public class ExportController {

    private final ExportService exportService;
    private final ProcessService processService;

    public ExportController(ExportService exportService, ProcessService processService) {
        this.exportService = exportService;
        this.processService = processService;
    }

    @GetMapping("/processes/csv")
    public ResponseEntity<String> exportProcessesCsv() {
        log.info("GET /export/processes/csv endpoint called");
        
        String csv = exportService.exportProcessesToCsv(processService.getProcesses());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"processes.csv\"")
                .body(csv);
    }

    @GetMapping("/processes/json")
    public ResponseEntity<String> exportProcessesJson() {
        log.info("GET /export/processes/json endpoint called");
        
        String json = exportService.exportProcessesToJson(processService.getProcesses());
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"processes.json\"")
                .body(json);
    }
}
