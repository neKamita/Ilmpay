package uz.pdp.ilmpay.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import lombok.RequiredArgsConstructor;
import uz.pdp.ilmpay.service.SupportLogoService;

/**
 * 🏠 Home Sweet Home Controller
 * Where all the magic begins! This little guy handles our landing page
 * and makes sure everyone feels welcome at Ilmpay.
 * 
 * @author Your Friendly Neighborhood Developer
 * @version 1.0 (Now with extra awesomeness!)
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    // 🎯 Our trusty services
    private final SupportLogoService supportLogoService;

    /**
     * 🎯 The main landing page route
     * Serving up our beautiful landing page with a side of Thymeleaf magic!
     * 
     * @return The index view, where dreams of education begin
     */
    @GetMapping("/")
    public ModelAndView home() {
        // 🎨 Creating a fresh canvas for our masterpiece
        ModelAndView mav = new ModelAndView("index");
        
        // 🚀 Set the current page for navigation highlighting
        mav.addObject("currentPage", "home");
        
        // 🤝 Add support logos to show our awesome partners
        mav.addObject("supportLogos", supportLogoService.findAllActive());
        
        return mav; // 🎉 Off you go, little view!
    }
}
