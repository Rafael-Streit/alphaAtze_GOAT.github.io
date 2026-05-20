package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.MemoryInfo;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

@Service
public class MemoryService {

    private final SystemInfo systemInfo = new SystemInfo();

    public MemoryInfo getMemoryInfo() {

        GlobalMemory memory =
                systemInfo.getHardware().getMemory();

        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        double usage =
                ((double) used / total) * 100;

        return new MemoryInfo()
                .totalBytes(total)
                .usedBytes(used)
                .freeBytes(available)
                .usagePercent(usage);
    }
}