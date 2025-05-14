package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.CurrentUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class InjectFaultController {

    private final FaultService faultService;
    private final CurrentUserInfo currentUserInfo;

    @Autowired
    public InjectFaultController(FaultService faultService, CurrentUserInfo currentUserInfo) {
        this.faultService = faultService;
        this.currentUserInfo = currentUserInfo;
    }

    @PostMapping("/inject")
    public ResponseEntity<String> injectFault(@RequestParam String name, @RequestParam int scheduledFor) {
        String curUser = currentUserInfo.getCurrentUsername();
        if(curUser == null){
            return ResponseEntity.badRequest().body("Must be logged in");
        }
        try {
            String result = faultService.injectRequestedFault(
                    name,
                    currentUserInfo.getCurrentUsername(),
                    "role", // You can extract role from SecurityContext if needed
                    scheduledFor);
            return ResponseEntity.ok(result);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().body("Fault not found.");
        }
    }

    @GetMapping("/list-jobs")
    public ResponseEntity<String> listRunningJobs() {
        String jobs = faultService.listAllJobs().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/list-faults")
    public ResponseEntity<String> listAllDefinedFaults() {
        String faults = faultService.listAllFaults().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n"));
        return ResponseEntity.ok(faults);
    }
}