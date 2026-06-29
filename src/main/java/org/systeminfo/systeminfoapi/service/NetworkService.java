package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.NetworkInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NetworkService {

    private final SystemInfo systemInfo = new SystemInfo();

    @Cacheable("networkInfo")
    public List<NetworkInfo> getNetworkInfo() {
        log.debug("Fetching network information");

        HardwareAbstractionLayer hardware =
                systemInfo.getHardware();

        List<NetworkIF> networkIFs =
                hardware.getNetworkIFs();

        List<NetworkInfo> result =
                new ArrayList<>();

        for (NetworkIF net : networkIFs) {

            net.updateAttributes();

            String ipv4 = "";

            if (net.getIPv4addr().length > 0) {
                ipv4 = net.getIPv4addr()[0];
            }

            NetworkInfo info =
                    new NetworkInfo()
                            .hostName(net.getDisplayName())
                            .ipv4(ipv4)
                            .macAddress(net.getMacaddr())
                            .bytesSent(net.getBytesSent())
                            .bytesReceived(net.getBytesRecv());

            result.add(info);
            log.debug("Network interface - Name: {}, IP: {}", net.getDisplayName(), ipv4);
        }

        log.info("Network Info retrieved - {} interfaces found", result.size());
        return result;
    }
}