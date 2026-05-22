package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.SystemOverview;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Service
public class SystemService {

    private final CpuService cpuService;
    private final MemoryService memoryService;
    private final DiskService diskService;
    private final NetworkService networkService;

    public SystemService(CpuService cpuService, MemoryService memoryService, DiskService diskService, NetworkService networkService) {
        this.cpuService = cpuService;
        this.memoryService = memoryService;
        this.diskService = diskService;
        this.networkService = networkService;
    }

    private final SystemInfo systemInfo =
            new SystemInfo();

    public SystemOverview getSystemOverview() {

        OperatingSystem os =
                systemInfo.getOperatingSystem();

        return new SystemOverview()
                .hostname(os.getNetworkParams().getHostName())
                .operatingSystem(os.toString())
                .uptimeSeconds(os.getSystemUptime())
                .cpu(cpuService.getCpuInfo())
                .memory(memoryService.getMemoryInfo())
                .disks(diskService.getDiskInfo())
                .network(
                        networkService
                                .getNetworkInfo()
                                .stream()
                                .findFirst()
                                .orElse(null)
                );
    }
}
