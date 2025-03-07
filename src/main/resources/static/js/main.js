// 🌟 Common Functionality for Ilmpay

// 📊 Session Tracking Utilities
const SessionTracker = {
    startTime: Date.now(),
    lastPageChange: Date.now(),
    pageCount: 1,

    // Track page changes
    trackPageChange() {
        this.pageCount++;
        this.lastPageChange = Date.now();
    },

    // Update session data before page unload
    async updateSession() {
        const duration = Math.floor((Date.now() - this.startTime) / 1000); // Convert to seconds
        const bounced = this.pageCount === 1; // Bounced if only one page was visited

        try {
            await fetch('/api/analytics/session-update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `duration=${duration}&bounced=${bounced}`,
                keepalive: true // Ensure request completes even if page is unloading
            });
        } catch (error) {
            console.error('Failed to update session:', error);
        }
    },

    // Initialize session tracking
    init() {
        // Track page changes
        window.addEventListener('popstate', () => this.trackPageChange());
        document.addEventListener('click', (e) => {
            const link = e.target.closest('a');
            if (link && link.href.startsWith(window.location.origin)) {
                this.trackPageChange();
            }
        });

        // Update session data before page unload
        window.addEventListener('beforeunload', () => {
            this.updateSession();
        });
    }
};

// 🎯 Global Animation Utilities
const AnimationUtils = {
    // 🎨 Animate elements on scroll
    animateOnScroll(elements, config) {
        if (!elements?.length || !window.gsap) return;
        
        gsap.from(elements, {
            scrollTrigger: {
                trigger: elements[0],
                start: "top bottom-=100",
                toggleActions: "play none none reverse"
            },
            ...config
        });
    },

    // ✨ Add hover animation to elements
    addHoverAnimation(elements, scaleAmount = 1.05) {
        if (!elements?.length || !window.gsap) return;

        elements.forEach(item => {
            item.addEventListener("mouseenter", () => {
                gsap.to(item, {
                    scale: scaleAmount,
                    duration: 0.3,
                    ease: "power2.out"
                });
            });

            item.addEventListener("mouseleave", () => {
                gsap.to(item, {
                    scale: 1,
                    duration: 0.3,
                    ease: "power2.out"
                });
            });
        });
    }
};

// 🎯 UI Utilities
const UIUtils = {
    // 🔄 Update custom dots for carousels
    updateCustomDots() {
        const dots = document.querySelectorAll('.custom-dot');
        const activeIndex = document.querySelector('.owl-item.active')?.dataset?.index || 0;
        
        dots.forEach((dot, index) => {
            dot.classList.toggle('active', index === parseInt(activeIndex));
        });
    },

    // 🎯 Update active dot
    updateActiveDot(index) {
        document.querySelectorAll('.custom-dot').forEach((dot, i) => {
            dot.classList.toggle('active', i === index);
        });
    },
    
    // 🎠 Initialize carousels
    initCarousels() {
        // Check if jQuery and Owl Carousel are available
        if (!window.jQuery || !jQuery.fn.owlCarousel) return;
        
        // 🏢 Support Logos Carousel - Linear animation with 6 items
        const supportCarousel = $('.support-carousel');
        if (supportCarousel.length) {
            supportCarousel.owlCarousel({
                loop: true,
                margin: 30,
                nav: false,
                dots: false,
                autoplay: true,
                autoplayTimeout: 2000,
                smartSpeed: 2000,
                autoplayHoverPause: true,
                slideTransition: 'linear',
                responsive: {
                    0: { items: 2 },
                    576: { items: 3 },
                    768: { items: 4 },
                    992: { items: 6 }
                }
            });
        }

        // 💬 Testimonial Carousel - Ease animation with custom dots
        const testimonialCarousel = $('.testimonial-carousel');
        if (testimonialCarousel.length) {
            const carousel = testimonialCarousel.owlCarousel({
                loop: true,
                margin: 30,
                nav: false,
                dots: false,
                autoplay: true,
                autoplayTimeout: 2000,
                smartSpeed: 1000,
                autoplayHoverPause: true,
                slideTransition: 'ease',
                responsive: {
                    0: { items: 1 },
                    576: { items: 2 },
                    992: { items: 3 },
                    1200: { items: 4 }
                }
            });

            // 🎯 Handle custom dot clicks
            $('.custom-dot').click(function() {
                const index = $(this).data('index');
                carousel.trigger('to.owl.carousel', [index, 300]);
            });

            // 🎨 Update active dot on slide change
            carousel.on('changed.owl.carousel', function(event) {
                $('.custom-dot').removeClass('active');
                $(`.custom-dot[data-index="${event.item.index - event.item.count}"]`).addClass('active');
            });
        }
    }
};

// 🌟 Initialize everything on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize session tracking
    SessionTracker.init();
    // 🎨 Initialize Lucide icons
    if (window.lucide) {
        lucide.createIcons();
    }

    // Only run animations if GSAP is loaded
    if (window.gsap) {
        // ✨ Animate content cards
        AnimationUtils.animateOnScroll(
            document.querySelectorAll(".content-card"),
            {
                y: 30,
                opacity: 0,
                duration: 0.6,
                stagger: {
                    each: 0.15,
                    ease: "power2.out"
                }
            }
        );

        // 🎯 Animate grid items
        const gridItems = document.querySelectorAll(".grid > div");
        AnimationUtils.animateOnScroll(
            gridItems,
            {
                scale: 0.8,
                opacity: 0,
                duration: 0.5,
                stagger: {
                    amount: 0.8,
                    ease: "power2.out"
                },
                ease: "back.out(1.7)"
            }
        );

        // ✨ Add hover effect to grid items
        AnimationUtils.addHoverAnimation(gridItems);
    }

    // 🎠 Initialize carousels if available
    UIUtils.initCarousels();

    // 🎯 Initialize tooltips if Bootstrap is available
    if (window.bootstrap) {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    // 🔗 Smooth scroll for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            // Skip language switcher links
            if (this.classList.contains('dropdown-item') && this.href.includes('lang=')) {
                return;
            }
            
            e.preventDefault();
            const href = this.getAttribute('href');
            
            // Check if href is a valid selector (not empty, starts with #, and has more than just #)
            if (href && href !== '#' && href.startsWith('#') && href.length > 1) {
                try {
                    // Try to use the href as a selector
                    const target = document.querySelector(href);
                    if (target) {
                        target.scrollIntoView({
                            behavior: 'smooth',
                            block: 'start'
                        });
                    }
                } catch (error) {
                    // 🐞 Log error if querySelector fails
                    console.error(`Invalid selector: ${href}`, error);
                }
            }
        });
    });
});
