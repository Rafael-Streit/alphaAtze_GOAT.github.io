package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.MemoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

@Slf4j
@Service
public class MemoryService {

    private final SystemInfo systemInfo = new SystemInfo();

    @Cacheable("memoryInfo")
    public MemoryInfo getMemoryInfo() {
        log.debug("Fetching memory information");

        GlobalMemory memory =
                systemInfo.getHardware().getMemory();

        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        double usage =
                ((double) used / total) * 100;

        MemoryInfo result = new MemoryInfo()
                .totalBytes(total)
                .usedBytes(used)
                .freeBytes(available)
                .usagePercent(usage);
        
        log.info("Memory Info retrieved - Usage: {}%", usage);
        return result;
    }
}