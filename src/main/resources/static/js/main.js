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

    // Initialize testimonial carousel
    var testimonialCarousel = $('.testimonial-carousel').owlCarousel({
        items: 4,
        loop: true,
        margin: 20,
        nav: true,
        dots: false, // Disable default dots
        mouseDrag: true,
        touchDrag: true,
        autoplay: true,
        autoplayTimeout: 3000,
        autoplayHoverPause: true,
        responsive:{
            0:{ items: 1 },
            768:{ items: 2 },
            992:{ items: 3 },
            1200:{ items: 4 }
        }
    });

    // Handle custom dot clicks
    $('.custom-dot').click(function(){
        var index = $(this).data('index');
        testimonialCarousel.trigger('to.owl.carousel', [index, 300]);
    });

    // Update active dot on carousel change
    testimonialCarousel.on('changed.owl.carousel', function(event){
        // Get the current item index
        var currentIndex = event.item.index;
        
        // Calculate the active dot index based on the number of dots
        var dotCount = $('.custom-dot').length;
        var activeDotIndex = currentIndex % dotCount;
        
        // Update active state
        $('.custom-dot').removeClass('active');
        $('.custom-dot').eq(activeDotIndex).addClass('active');
    });

    // Initialize active dot on page load
    $('.custom-dot').first().addClass('active');
});

// FAQ Toggle functionality
$(document).ready(function() {
    $('.faq-question').click(function() {
        const faqItem = $(this).closest('.faq-item');
        
        // Close other FAQ items
        $('.faq-item').not(faqItem).removeClass('active');
        
        // Toggle current FAQ item
        faqItem.toggleClass('active');
    });
});
