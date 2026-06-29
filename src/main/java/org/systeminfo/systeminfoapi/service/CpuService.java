package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.CpuInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CpuService {

    private final SystemInfo systemInfo = new SystemInfo();

    @Cacheable("cpuInfo")
    public CpuInfo getCpuInfo() {
        log.debug("Fetching CPU information");

        HardwareAbstractionLayer hardware =
                systemInfo.getHardware();

        CentralProcessor processor =
                hardware.getProcessor();

        double cpuUsage =
                processor.getSystemCpuLoad(1000) * 100;

        CpuInfo result = new CpuInfo()
                .usagePercent(cpuUsage)
                .physicalCores(
                        processor.getPhysicalProcessorCount()
                )
                .logicalCores(
                        processor.getLogicalProcessorCount()
                )
                .frequencyHz(
                        processor.getMaxFreq()
                );
        
        log.info("CPU Info retrieved - Usage: {}%", cpuUsage);
        return result;
    }
}