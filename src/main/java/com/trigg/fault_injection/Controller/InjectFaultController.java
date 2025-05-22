package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.AuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/fault")
public class InjectFaultController {
    /*
     * Defines rest endpoints to inject pre-defined faults, list running/completed jobs,
     * and list all defined faults.
     */

    private final FaultService faultService;
    private final AuthContext authContext;

    @Autowired
    public InjectFaultController(FaultService faultService, AuthContext authContext) {
        this.faultService = faultService;
        this.authContext = authContext;
    }

    @PostMapping("/inject")
    public ResponseEntity<String> injectFault(@RequestParam String name, @RequestParam int scheduledFor) {
        String curUser = authContext.getUsername();
        if(curUser == null){
            return ResponseEntity.badRequest().body("Must be logged in");
        }
        try {
            String result = faultService.injectRequestedFault(
                    name,
                    authContext.getUsername(),
                    authContext.getRole(), // You can extract role from SecurityContext if needed
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