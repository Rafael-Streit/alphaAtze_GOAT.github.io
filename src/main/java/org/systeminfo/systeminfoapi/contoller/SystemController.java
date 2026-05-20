package org.systeminfo.systeminfoapi.contoller;

import com.example.systemmonitor.api.SystemApi;
import com.example.systemmonitor.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.systeminfo.systeminfoapi.service.*;

import java.util.List;

@RestController
public class SystemController implements SystemApi {

    private final CpuService cpuService;
    private final MemoryService memoryService;
    private final DiskService diskService;
    private final NetworkService networkService;

    public SystemController(CpuService cpuService, MemoryService memoryService, NetworkService networkService, DiskService diskService, ProcessService processService) {
        this.cpuService = cpuService;
        this.memoryService  = memoryService;
        this.diskService = diskService;
        this.networkService = networkService;
    }

    @Override
    public ResponseEntity<CpuInfo> getCpuInfo() {
        return ResponseEntity.ok(
                cpuService.getCpuInfo()
        );
    }

    @Override
    public ResponseEntity<MemoryInfo> getMemoryInfo() {

        return ResponseEntity.ok(
                this.memoryService.getMemoryInfo()
        );
    }

    @Override
    public ResponseEntity<List<DiskInfo>> getDiskInfo() {

        return ResponseEntity.ok(
                this.diskService.getDiskInfo()
        );
    }

    @Override
    public ResponseEntity<List<NetworkInfo>> getNetworkInfo() {

        return ResponseEntity.ok(
                this.networkService.getNetworkInfo()
        );
    }
}