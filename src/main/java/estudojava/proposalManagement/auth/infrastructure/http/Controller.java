package estudojava.proposalManagement.auth.infrastructure.http;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {

    @GetMapping
    public String hello(@AuthenticationPrincipal UserDetails user) {
        return "Hello World " + user.getUsername();
    }

    @GetMapping("/influencer")
    //@PreAuthorize("hasRole('INFLUENCER')")
    public String userEndpoint() {
        return "Hello INFLUENCER";
    }

    @GetMapping("/brand")
    //@PreAuthorize("hasRole('BRAND')")
    public String adminEndpoint() {
        return "Hello BRAND";
    }
}