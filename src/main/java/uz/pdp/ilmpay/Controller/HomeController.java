package uz.pdp.ilmpay.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.pdp.ilmpay.service.SupportLogoService;
import uz.pdp.ilmpay.service.BenefitCardService;
import uz.pdp.ilmpay.service.TestimonialService;
import uz.pdp.ilmpay.service.FaqService;

/**
 * 🏠 Home Sweet Home Controller
 * Where all the magic begins! This little guy handles our landing page
 * and makes sure everyone feels welcome at Ilmpay.
 * 
 * @author Your Friendly Neighborhood Developer
 * @version 1.0 (Now with extra awesomeness!)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    // 🎯 Our trusty services
    private final SupportLogoService supportLogoService;
    private final BenefitCardService benefitCardService; // 🎁 For managing our awesome benefits
    private final TestimonialService testimonialService; // 🌟 For our happy students' stories
    private final FaqService faqService; // 🤔 For frequently asked questions

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
        var logos = supportLogoService.findAllActive();
        log.info("🤝 Found {} support logos for homepage", logos.size());
        mav.addObject("supportLogos", logos);
        
        // 🎁 Add our amazing benefits, sorted by display order
        var benefits = benefitCardService.findAllActive();
        log.info("🎁 Found {} benefits for homepage", benefits.size());
        benefits.forEach(benefit -> log.info("Benefit: {} (Order: {})", benefit.getTitle(), benefit.getDisplayOrder()));
        mav.addObject("benefits", benefits);

        // 🌟 Add testimonials from our happy students
        log.info("🔍 Fetching all active testimonials");
        var testimonials = testimonialService.findAllActive();
        log.info("🎭 Found {} testimonials for homepage", testimonials.size());
        mav.addObject("testimonials", testimonials);

        // 🤔 Add FAQs to help our users
        var faqs = faqService.findAllActive();
        log.info("🤔 Found {} FAQs for homepage", faqs.size());
        mav.addObject("faqs", faqs);
        
        return mav; // 🎉 Off you go, little view!
    }
}
