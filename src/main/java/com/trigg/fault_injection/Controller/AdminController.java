package com.trigg.fault_injection.Controller;

import com.trigg.fault_injection.Model.UserAccount;
import com.trigg.fault_injection.Service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AppUserService appUserService;

    @Autowired
    public AdminController(AppUserService appUserService){
        this.appUserService = appUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve-user")
    public ResponseEntity<String> approveUser(@RequestParam String username) {
        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return ResponseEntity.badRequest().body("User not found.");

        }
        else if(account.isApproved()){
            return ResponseEntity.badRequest().body("Account has already been approved.");

        }
        else{
            appUserService.approveUser(account.getId());
        }

        return ResponseEntity.ok("User approved successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upgrade-user")
    public ResponseEntity<String> upgradeUser(@RequestParam String username) {
        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return ResponseEntity.badRequest().body("User not found.");
        }
        else{
            String currentRole = appUserService.getUserRole(account.getId());
            if(currentRole.equalsIgnoreCase("ROLE_ADMIN")){
                return ResponseEntity.badRequest().body("User is already an admin.");

            }
            else{
                appUserService.upgradeUserRole(account.getId());
            }
        }
        return ResponseEntity.ok("User upgraded to ROLE_ADMIN.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/downgrade-user")
    public ResponseEntity<String> downgradeUser(@RequestParam String username) {
        UserAccount account = appUserService.retrieveAccount(username);
        if(account == null){
            return ResponseEntity.badRequest().body("User not found.");
        }
        else{
            String currentRole = appUserService.getUserRole(account.getId());
            if(currentRole.equalsIgnoreCase("ROLE_USER")){
                return ResponseEntity.badRequest().body("User is already an user.");

            }
            else{
                appUserService.upgradeUserRole(account.getId());
            }
        }
        return ResponseEntity.ok("User downgraded to ROLE_USER.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-users")
    public ResponseEntity<String> pendingUsers(){
        List<String> pendingUsers = appUserService.getPendingUsers();
        if(pendingUsers.isEmpty()){
            return ResponseEntity.ok("No users awaiting approval.");
        }
        StringBuilder result = new StringBuilder("Users awaiting approval:\n");
        for (String user : pendingUsers) {
            result.append("- ").append(user).append("\n");
        }
        return ResponseEntity.ok(result.toString());
    }
}
