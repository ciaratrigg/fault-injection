package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.FaultEvent;
import com.trigg.fault_injection.Utilities.FaultLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class FaultTimelineController {

    private final FaultLog faultLog;

    public FaultTimelineController(FaultLog faultLog) {
        this.faultLog = faultLog;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("faults", faultLog.getEvents());
        return "dashboard";
    }
}

