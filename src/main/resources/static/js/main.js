// Navbar Scroll Effect
document.addEventListener('DOMContentLoaded', function() {
    const navbar = document.querySelector('.navbar');
    const navLinks = document.querySelectorAll('.nav-link');
    
    // Set initial active state
    setActiveNavLink();
    
    // Update active state on scroll
    window.addEventListener('scroll', function() {
        setActiveNavLink();
        
        // Add/remove background on scroll
        if (window.scrollY > 50) {
            navbar.style.background = 'var(--primary-color)';
            navbar.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
        } else {
            navbar.style.background = 'transparent';
            navbar.style.boxShadow = 'none';
        }
    });
    
    // Handle mobile menu
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');
    
    navbarToggler.addEventListener('click', function() {
        navbar.style.background = 'var(--primary-color)';
    });
    
    // Close mobile menu on link click
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navbarCollapse.classList.contains('show')) {
                navbarToggler.click();
            }
        });
    });
});

// Set active nav link based on scroll position
function setActiveNavLink() {
    const sections = document.querySelectorAll('section');
    const navLinks = document.querySelectorAll('.nav-link');
    
    let current = '';
    
    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        if (window.scrollY >= sectionTop - 100) {
            current = section.getAttribute('id');
        }
    });
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${current}`) {
            link.classList.add('active');
        }
    });
}

// Smooth scroll for anchor links
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

// Hero Section Animations - Making our phones dance! 
document.addEventListener('DOMContentLoaded', function() {
    // Animate hero images with a staggered delay
    const heroImages = document.querySelectorAll('.hero-image');
    
    heroImages.forEach((img, index) => {
        setTimeout(() => {
            img.classList.add('animate');
        }, index * 200); // 200ms delay between each image
    });
});

// Support Carousel - Infinite loop without navigation arrows
$(document).ready(function(){
    $('.support-carousel').owlCarousel({
        items: 5,
        loop: true,
        margin: 30,
        nav: false,
        dots: false,
        autoplay: true,
        autoplayTimeout: 1000,
        autoplayHoverPause: true,
        responsive:{
            0:{ items: 2 },
            576:{ items: 3 },
            768:{ items: 4 },
            992:{ items: 5 }
        }
    });
});
