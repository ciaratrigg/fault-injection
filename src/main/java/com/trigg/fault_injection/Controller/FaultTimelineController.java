package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.FaultEvent;
import com.trigg.fault_injection.Service.ContainerInfoService;
import com.trigg.fault_injection.Utilities.FaultLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class FaultTimelineController {
    /*
     * Dynamic dashboard which displays a fault log as well as all
     * running containers in the target system and their statuses.
     */
    private final FaultLog faultLog;
    private ContainerInfoService containerInfoService;

    @Autowired
    public FaultTimelineController(FaultLog faultLog, ContainerInfoService containerInfoService) {
        this.faultLog = faultLog;
        this.containerInfoService = containerInfoService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("faults", faultLog.getEvents());
        model.addAttribute("containers", containerInfoService.getContainerStatuses());
        return "dashboard";
    }


}

