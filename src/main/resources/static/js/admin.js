function toggleSubmenu(menuId) {
    const menu = document.getElementById(menuId);
    const icon = document.getElementById(menuId + '-icon');
    menu.classList.toggle('hidden');
    icon.classList.toggle('rotate-90');
}

// Initialize Lucide icons and GSAP animations
document.addEventListener('DOMContentLoaded', function() {
    // Check for elements before animating
    const contentCards = document.querySelectorAll('.content-card');
    if (contentCards.length > 0) {
        gsap.from(contentCards, {
            opacity: 0,
            y: 20,
            stagger: 0.1,
            duration: 0.5
        });
    }

    lucide.createIcons();
    
    // Register ScrollTrigger plugin
    gsap.registerPlugin(ScrollTrigger);

    // Timeline for sidebar entrance
    const sidebarTL = gsap.timeline();
    
    const sidebarLogo = document.querySelector('.sidebar-logo');
    if (sidebarLogo) {
        sidebarTL
            .from(sidebarLogo, {
                x: -50, 
                opacity: 0,
                duration: 0.8,
                ease: "power3.out"
            })
            .from(".nav-item", {
                x: -30,
                opacity: 0,
                duration: 0.5,
                stagger: 0.1,
                ease: "power2.out"
            }, "-=0.3");
    }

    // Stats cards animation
    gsap.from(".content-card", {
        scrollTrigger: {
            trigger: ".content-card",
            start: "top bottom-=100",
            toggleActions: "play none none reverse"
        },
        y: 30,
        opacity: 0,
        duration: 0.6,
        stagger: {
            each: 0.15,
            ease: "power2.out"
        }
    });

    // Grid items animation with scale and hover effect
    const gridItems = document.querySelectorAll(".grid > div");
    
    gsap.from(gridItems, {
        scrollTrigger: {
            trigger: ".grid",
            start: "top bottom-=50",
            toggleActions: "play none none reverse"
        },
        scale: 0.8,
        opacity: 0,
        duration: 0.5,
        stagger: {
            amount: 0.8,
            ease: "power2.out"
        },
        ease: "back.out(1.7)"
    });

    // Add hover animations for grid items
    gridItems.forEach(item => {
        item.addEventListener("mouseenter", () => {
            gsap.to(item, {
                scale: 1.05,
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

    // Sidebar collapse functionality
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        const sidebarIcon = sidebarToggle.querySelector('i');
        const mainContent = document.querySelector('main');
        let isCollapsed = false;

        sidebarToggle.addEventListener('click', () => {
            isCollapsed = !isCollapsed;
            
            // Timeline for collapse animation
            const tl = gsap.timeline();
            
            if (isCollapsed) {
                tl.to(mainContent, {
                    paddingLeft: '64px',
                    duration: 0.3,
                    ease: 'power2.inOut'
                }, 0)
                .to(sidebarIcon, {
                    rotation: 180,
                    duration: 0.3,
                    ease: 'power2.inOut'
                }, 0);

                // Hide text elements
                gsap.to('.sidebar-text', {
                    opacity: 0,
                    duration: 0.2,
                    display: 'none'
                });
            } else {
                tl.to(mainContent, {
                    paddingLeft: '256px',
                    duration: 0.3,
                    ease: 'power2.inOut'
                }, 0)
                .to(sidebarIcon, {
                    rotation: 0,
                    duration: 0.3,
                    ease: 'power2.inOut'
                }, 0);

                // Show text elements
                gsap.to('.sidebar-text', {
                    opacity: 1,
                    duration: 0.2,
                    display: 'block',
                    delay: 0.1
                });
            }
        });
    }

    // Revenue Chart
    const revenueChartEl = document.querySelector('#revenueChart');
    if (revenueChartEl) {
        const revenueOptions = {
            chart: { type: 'line', height: '100%' },
            colors: ['#10B981'],
            series: [{
                name: 'Revenue',
                data: [1200, 1800, 1500, 2000, 2500, 3000, 3500]
            }],
            xaxis: { categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] }
        };
        new ApexCharts(revenueChartEl, revenueOptions).render();
    }

    // Feature Usage Chart
    const featureUsageChartEl = document.querySelector('#featureUsageChart');
    if (featureUsageChartEl) {
        const featureUsageOptions = {
            chart: { type: 'bar', height: '100%' },
            colors: ['#8B5CF6'],
            series: [{
                name: 'Usage',
                data: [400, 430, 448, 470, 540, 580, 690]
            }],
            xaxis: { 
                categories: ['Feature 1', 'Feature 2', 'Feature 3', 'Feature 4', 'Feature 5', 'Feature 6', 'Feature 7']
            }
        };
        new ApexCharts(featureUsageChartEl, featureUsageOptions).render();
    }
}); 