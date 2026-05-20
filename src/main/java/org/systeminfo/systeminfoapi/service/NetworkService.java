package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.NetworkInfo;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetworkService {

    private final SystemInfo systemInfo = new SystemInfo();

    public List<NetworkInfo> getNetworkInfo() {

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
        }

        return result;
    }
}