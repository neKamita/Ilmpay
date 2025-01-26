package uz.pdp.ilmpay.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 👑 Admin Controller
 * Where the magic happens behind the scenes!
 * 
 * @author Your Admin Panel Architect
 * @version 1.0 (The "Control Room" Edition)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 🎯 Login page
     * The gateway to unlimited power! (well, almost unlimited)
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";  // 🔑 Show the login page
    }

    /**
     * 🎮 Dashboard
     * The command center of our operation!
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";  // 📊 Welcome to the control room
    }
}
