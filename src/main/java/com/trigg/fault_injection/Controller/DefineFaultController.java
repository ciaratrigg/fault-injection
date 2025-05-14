package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.FaultType;
import com.trigg.fault_injection.Service.FaultService;
import com.trigg.fault_injection.Utilities.CurrentUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/define")
public class DefineFaultController {

    private FaultService faultService;
    private CurrentUserInfo currentUserInfo;

    @Autowired
    public DefineFaultController(FaultService faultService, CurrentUserInfo currentUserInfo){
        this.faultService = faultService;
        this.currentUserInfo = currentUserInfo;
    }

    private ResponseEntity<String> defineFaultController(FaultType type, String name, int duration, int numNodesOrThreads, Integer frequency, long latency){
        String curUser = currentUserInfo.getCurrentUsername();
        if(curUser == null){
            return ResponseEntity.badRequest().body("Must be logged in");
        }
        try{
            Fault fault = faultService.defineFault(type.getTypeName());
            fault.setCommonAttr(curUser, name, duration, type.getTypeName());

            if(frequency != null){
                fault.setUniqueAttr(numNodesOrThreads, frequency);
            }
            else if(latency != -1){
                fault.setUniqueAttr(latency);
            }
            else{
                fault.setUniqueAttr(numNodesOrThreads);
            }

            int faultId = faultService.saveFault(fault);
            return ResponseEntity.ok("Created " + type + " fault with ID: " + faultId);
        }
        catch(DataAccessException e){
            return ResponseEntity.internalServerError().body("Database error.");
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/node-restart")
    public ResponseEntity<String> defineNodeRestart(@RequestParam String name,
                                    @RequestParam int duration,
                                    @RequestParam (defaultValue = "1") int numNodes,
                                    @RequestParam int frequency){
        return defineFaultController(FaultType.NODE_RESTART, name, duration, numNodes, frequency, -1);
    }

    @GetMapping("/cpu-stress-sc")
    public ResponseEntity<String> defineCpuStress(@RequestParam String name,
                                  @RequestParam int duration,
                                  @RequestParam (defaultValue ="1") int numThreads){
        return defineFaultController(FaultType.CPU_STRESS_SC, name, duration, numThreads, null, -1);
    }


    @GetMapping("/node-crash")
    public ResponseEntity<String> defineNodeCrash(@RequestParam String name,
                                  @RequestParam int duration,
                                  @RequestParam (defaultValue ="1") int numNodes){
        return defineFaultController(FaultType.NODE_CRASH, name, duration, numNodes, null, -1);
    }

    @GetMapping("/network-delay")
    public ResponseEntity<String> defineNetworkDelay(@RequestParam String name,
                                     @RequestParam int duration,
                                     @RequestParam int latency){
        return defineFaultController(FaultType.NETWORK_DELAY, name, duration, -1, null, latency);

    }

    @GetMapping("/bandwidth-throttle")
    public ResponseEntity<String> defineBandwidthThrottle(@RequestParam String name,
                                                          @RequestParam int duration,
                                                          @RequestParam long rate){
        return defineFaultController(FaultType.BANDWIDTH_THROTTLE, name, duration, -1, null, rate);
    }
}
