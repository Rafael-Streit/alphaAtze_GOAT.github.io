package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.ProcessInfo;
import com.example.systemmonitor.dto.ProcessStartedResponse;
import com.example.systemmonitor.dto.StartProcessRequest;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProcessService {

    private final SystemInfo systemInfo =
            new SystemInfo();

    public List<ProcessInfo> getProcesses() {

        OperatingSystem os =
                systemInfo.getOperatingSystem();

        List<OSProcess> processes =
                os.getProcesses(
                        null,
                        OperatingSystem.ProcessSorting.CPU_DESC,
                        50
                );

        List<ProcessInfo> result =
                new ArrayList<>();

        for (OSProcess process : processes) {

            double cpu =
                    process.getProcessCpuLoadCumulative();

            cpu = cpu * 100;

            if (cpu < 0) {
                cpu = 0;
            }

            ProcessInfo info =
                    new ProcessInfo()
                            .pid(process.getProcessID())
                            .name(process.getName())
                            .status(
                                    process.getState().name()
                            )
                            .cpuUsagePercent(
                                    Math.round(cpu * 100.0) / 100.0
                            )
                            .memoryBytes(
                                    process.getResidentSetSize()
                            )
                            .threadCount(
                                    process.getThreadCount()
                            );

            result.add(info);
        }

        result.sort(
                Comparator.comparing(
                        ProcessInfo::getCpuUsagePercent
                ).reversed()
        );

        return result;
    }

    public boolean killProcess(int pid) {

        try {

            Process process = new ProcessBuilder(
                    "taskkill",
                    "/PID",
                    String.valueOf(pid),
                    "/F"
            ).start();

            int exitCode = process.waitFor();

            return exitCode == 0;

        } catch (Exception e) {
            return false;
        }
    }
    public ProcessStartedResponse startProcess(
            StartProcessRequest request
    ) {

        try {

            Process process =
                    new ProcessBuilder(
                            request.getCommand()
                    ).start();

            long pid = process.pid();

            return new ProcessStartedResponse()
                    .pid((int) pid)
                    .command(request.getCommand())
                    .status("STARTED");

        } catch (Exception e) {

            throw new RuntimeException(
                    "Could not start process"
            );
        }
    }
}