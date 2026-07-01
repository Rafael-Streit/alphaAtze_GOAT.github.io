package org.systeminfo.systeminfoapi.contoller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.systeminfo.systeminfoapi.service.*;

import java.util.List;

@Slf4j
@RestController
public class SystemController implements com.example.systemmonitor.api.SystemApi {

    private final CpuService cpuService;
    private final MemoryService memoryService;
    private final DiskService diskService;
    private final NetworkService networkService;
    private final SystemService systemService;

    public SystemController(CpuService cpuService, MemoryService memoryService, NetworkService networkService, DiskService diskService, SystemService systemService) {
        this.cpuService = cpuService;
        this.memoryService  = memoryService;
        this.diskService = diskService;
        this.networkService = networkService;
        this.systemService = systemService;
    }

    @Override
    public ResponseEntity<com.example.systemmonitor.dto.CpuInfo> getCpuInfo() {
        log.info("GET /cpu endpoint called");
        return ResponseEntity.ok(
                cpuService.getCpuInfo()
        );
    }

    @Override
    public ResponseEntity<com.example.systemmonitor.dto.MemoryInfo> getMemoryInfo() {
        log.info("GET /memory endpoint called");
        return ResponseEntity.ok(
                this.memoryService.getMemoryInfo()
        );
    }

    @Override
    public ResponseEntity<List<com.example.systemmonitor.dto.DiskInfo>> getDiskInfo() {
        log.info("GET /disk endpoint called");
        return ResponseEntity.ok(
                this.diskService.getDiskInfo()
        );
    }

    @Override
    public ResponseEntity<List<com.example.systemmonitor.dto.NetworkInfo>> getNetworkInfo() {
        log.info("GET /network endpoint called");
        return ResponseEntity.ok(
                this.networkService.getNetworkInfo()
        );
    }

    public ResponseEntity<com.example.systemmonitor.dto.SystemOverview>
    getSystemOverview() {
        log.info("GET /system-overview endpoint called");
        return ResponseEntity.ok(
                systemService.getSystemOverview()
        );
    }
}