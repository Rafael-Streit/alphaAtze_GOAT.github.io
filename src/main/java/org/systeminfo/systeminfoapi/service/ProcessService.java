package org.systeminfo.systeminfoapi.service;

import com.example.systemmonitor.dto.ProcessInfo;
import com.example.systemmonitor.dto.ProcessStartedResponse;
import com.example.systemmonitor.dto.StartProcessRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.systeminfo.systeminfoapi.exception.InvalidProcessRequestException;
import org.systeminfo.systeminfoapi.exception.ProcessNotFoundException;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ProcessService {

    private final SystemInfo systemInfo =
            new SystemInfo();

    @Cacheable("processes")
    public List<ProcessInfo> getProcesses() {
        log.debug("Fetching processes list");

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

        log.info("Processes retrieved - Total: {}", result.size());
        return result;
    }

    public boolean killProcess(int pid) {
        log.warn("Kill process request - PID: {}", pid);

        if (pid <= 0) {
            log.error("Invalid PID: {}", pid);
            throw new InvalidProcessRequestException("PID must be greater than 0");
        }

        try {
            Process process = new ProcessBuilder(
                    "taskkill",
                    "/PID",
                    String.valueOf(pid),
                    "/F"
            ).start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Process killed successfully - PID: {}", pid);
                return true;
            } else {
                log.warn("Failed to kill process - PID: {}, Exit code: {}", pid, exitCode);
                throw new ProcessNotFoundException("Process with PID " + pid + " not found or cannot be killed");
            }

        } catch (InterruptedException ie) {
            log.error("Interrupted while killing process - PID: {}", pid, ie);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Process termination interrupted", ie);
        } catch (Exception e) {
            log.error("Error killing process - PID: {}", pid, e);
            throw new ProcessNotFoundException("Could not kill process with PID: " + pid, e);
        }
    }

    public ProcessStartedResponse startProcess(StartProcessRequest request) {
        log.info("Start process request - Command: {}", request.getCommand());

        if (request.getCommand() == null || request.getCommand().isBlank()) {
            log.error("Invalid start process request: command is empty");
            throw new InvalidProcessRequestException("Command cannot be empty");
        }

        try {
            Process process =
                    new ProcessBuilder(
                            request.getCommand()
                    ).start();

            long pid = process.pid();

            ProcessStartedResponse response = new ProcessStartedResponse()
                    .pid((int) pid)
                    .command(request.getCommand())
                    .status("STARTED");

            log.info("Process started successfully - Command: {}, PID: {}", request.getCommand(), pid);
            return response;

        } catch (Exception e) {
            log.error("Could not start process - Command: {}", request.getCommand(), e);
            throw new InvalidProcessRequestException("Could not start process: " + e.getMessage(), e);
        }
    }
}