package org.systeminfo.systeminfoapi.contoller;

import com.example.systemmonitor.api.ProcessesApi;
import com.example.systemmonitor.dto.ProcessInfo;
import com.example.systemmonitor.dto.ProcessStartedResponse;
import com.example.systemmonitor.dto.StartProcessRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.systeminfo.systeminfoapi.service.ProcessService;

import java.util.List;

@Slf4j
@RestController
public class ProcessesController implements ProcessesApi {

    private final ProcessService processService;

    public ProcessesController(
            ProcessService processService
    ) {
        this.processService = processService;
    }

    @Override
    public ResponseEntity<List<ProcessInfo>> getProcesses(
            Integer limit,
            String sort
    ) {
        log.info("GET /processes endpoint called - limit: {}, sort: {}", limit, sort);
        return ResponseEntity.ok(
                processService.getProcesses()
        );
    }

    @Override
    public ResponseEntity<Void> killProcess(Integer pid) {
        log.warn("DELETE /processes/{} endpoint called - PID: {}", pid, pid);

        boolean killed =
                processService.killProcess(pid);

        if (killed) {
            log.info("Process killed successfully - PID: {}", pid);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to kill process - PID: {}", pid);
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<ProcessStartedResponse>
    startProcess(StartProcessRequest startProcessRequest) {
        log.info("POST /processes endpoint called - Command: {}", startProcessRequest.getCommand());

        ProcessStartedResponse response =
                processService.startProcess(
                        startProcessRequest
                );

        log.info("Process started - Command: {}, PID: {}", startProcessRequest.getCommand(), response.getPid());
        return ResponseEntity
                .status(201)
                .body(response);
    }
}
