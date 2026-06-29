package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.SystemOverview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Slf4j
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

    @Cacheable("systemOverview")
    public SystemOverview getSystemOverview() {
        log.debug("Fetching system overview");

        OperatingSystem os =
                systemInfo.getOperatingSystem();

        String hostname = os.getNetworkParams().getHostName();
        long uptime = os.getSystemUptime();
        
        SystemOverview result = new SystemOverview()
                .hostname(hostname)
                .operatingSystem(os.toString())
                .uptimeSeconds(uptime)
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
        
        log.info("System Overview retrieved - Hostname: {}, Uptime: {}s", hostname, uptime);
        return result;
    }
}
