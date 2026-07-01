package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.DiskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DiskService {

    private final SystemInfo systemInfo = new SystemInfo();

    @Cacheable("diskInfo")
    public List<DiskInfo> getDiskInfo() {
        log.debug("Fetching disk information");

        FileSystem fileSystem =
                systemInfo.getOperatingSystem()
                        .getFileSystem();

        List<DiskInfo> disks = new ArrayList<>();

        for (OSFileStore store : fileSystem.getFileStores()) {

            long total = store.getTotalSpace();
            long free = store.getUsableSpace();
            long used = total - free;

            double usage =
                    total > 0
                            ? ((double) used / total) * 100
                            : 0;

            DiskInfo diskInfo = new DiskInfo()
                    .name(store.getMount())
                    .totalBytes(total)
                    .usedBytes(used)
                    .freeBytes(free)
                    .usagePercent(usage);

            disks.add(diskInfo);
            log.debug("Disk Info - Mount: {}, Usage: {}%", store.getMount(), usage);
        }

        log.info("Disk Info retrieved - {} filesystems found", disks.size());
        return disks;
    }
}