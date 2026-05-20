package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.CpuInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

@Service
@RequiredArgsConstructor
public class CpuService {

    private final SystemInfo systemInfo = new SystemInfo();

    public CpuInfo getCpuInfo() {

        HardwareAbstractionLayer hardware =
                systemInfo.getHardware();

        CentralProcessor processor =
                hardware.getProcessor();

        double cpuUsage =
                processor.getSystemCpuLoad(1000) * 100;

        return new CpuInfo()
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
    }
}