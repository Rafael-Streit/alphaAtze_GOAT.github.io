package org.systeminfo.systeminfoapi.contoller;

import com.example.systemmonitor.api.ProcessesApi;
import com.example.systemmonitor.dto.ProcessInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.systeminfo.systeminfoapi.service.ProcessService;

import java.util.List;

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

        return ResponseEntity.ok(
                processService.getProcesses()
        );
    }

    @Override
    public ResponseEntity<Void> killProcess(Integer pid) {

        boolean killed =
                processService.killProcess(pid);

        if (killed) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
