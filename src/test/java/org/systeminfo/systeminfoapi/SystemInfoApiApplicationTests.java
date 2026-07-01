package org.systeminfo.systeminfoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.systeminfo.systeminfoapi.service.CpuService;
import org.systeminfo.systeminfoapi.service.MemoryService;
import org.systeminfo.systeminfoapi.service.DiskService;
import org.systeminfo.systeminfoapi.service.NetworkService;
import org.systeminfo.systeminfoapi.service.ProcessService;
import org.systeminfo.systeminfoapi.service.SystemService;
import com.example.systemmonitor.dto.CpuInfo;
import com.example.systemmonitor.dto.MemoryInfo;
import com.example.systemmonitor.dto.SystemOverview;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SystemInfoApiApplicationTests {

    @Autowired
    private CpuService cpuService;

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private DiskService diskService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private SystemService systemService;

    @Test
    public void testContextLoads() {
        assertNotNull(cpuService);
        assertNotNull(memoryService);
        assertNotNull(diskService);
        assertNotNull(networkService);
        assertNotNull(processService);
        assertNotNull(systemService);
    }

    @Test
    public void testCpuService() {
        CpuInfo cpuInfo = cpuService.getCpuInfo();
        assertNotNull(cpuInfo);
        assertNotNull(cpuInfo.getUsagePercent());
        assertTrue(cpuInfo.getLogicalCores() > 0);
        assertTrue(cpuInfo.getPhysicalCores() > 0);
    }

    @Test
    public void testMemoryService() {
        MemoryInfo memoryInfo = memoryService.getMemoryInfo();
        assertNotNull(memoryInfo);
        assertTrue(memoryInfo.getTotalBytes() > 0);
        assertTrue(memoryInfo.getUsedBytes() >= 0);
        assertTrue(memoryInfo.getFreeBytes() >= 0);
        assertTrue(memoryInfo.getUsagePercent() >= 0);
        assertTrue(memoryInfo.getUsagePercent() <= 100);
    }

    @Test
    public void testDiskService() {
        var diskInfoList = diskService.getDiskInfo();
        assertNotNull(diskInfoList);
        assertFalse(diskInfoList.isEmpty());
        
        diskInfoList.forEach(disk -> {
            assertNotNull(disk.getName());
            assertTrue(disk.getTotalBytes() > 0);
        });
    }

    @Test
    public void testNetworkService() {
        var networkInfoList = networkService.getNetworkInfo();
        assertNotNull(networkInfoList);
        assertFalse(networkInfoList.isEmpty());
    }

    @Test
    public void testProcessService() {
        var processes = processService.getProcesses();
        assertNotNull(processes);
        assertFalse(processes.isEmpty());
        
        processes.forEach(process -> {
            assertTrue(process.getPid() >= 0);
            assertNotNull(process.getName());
            assertNotNull(process.getStatus());
        });
    }

    @Test
    public void testSystemService() {
        SystemOverview overview = systemService.getSystemOverview();
        assertNotNull(overview);
        assertNotNull(overview.getHostname());
        assertNotNull(overview.getOperatingSystem());
        assertTrue(overview.getUptimeSeconds() >= 0);
        assertNotNull(overview.getCpu());
        assertNotNull(overview.getMemory());
        assertNotNull(overview.getDisks());
    }
}
