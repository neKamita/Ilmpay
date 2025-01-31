// 🌟 Common Functionality for Ilmpay

// 🎯 Global Animation Utilities
const AnimationUtils = {
    // 🎨 Animate elements on scroll
    animateOnScroll(elements, config) {
        if (!elements?.length) return;
        
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
        if (!elements?.length) return;

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
    }
};

// 🌟 Initialize everything on page load
document.addEventListener('DOMContentLoaded', function() {
    // 🎨 Initialize Lucide icons
    lucide.createIcons();

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

    // 🎠 Document Ready Handler
    // 🏢 Support Logos Carousel - Linear animation with 6 items
    $('.support-carousel').owlCarousel({
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

    // 💬 Testimonial Carousel - Ease animation with custom dots
    const testimonialCarousel = $('.testimonial-carousel').owlCarousel({
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
        testimonialCarousel.trigger('to.owl.carousel', [index, 300]);
    });

    // 🎨 Update active dot on slide change
    testimonialCarousel.on('changed.owl.carousel', function(event) {
        $('.custom-dot').removeClass('active');
        $(`.custom-dot[data-index="${event.item.index - event.item.count}"]`).addClass('active');
    });

    // 🎯 Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // 🔗 Smooth scroll for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});
