// 🎯 Smooth Scroll Handler
document.addEventListener('DOMContentLoaded', () => {
    // Get all navigation links that have href starting with #
    const navLinks = document.querySelectorAll('a[href^="#"]');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Get the target section id from href
            const targetId = link.getAttribute('href');
            if (targetId === '#') return; // Skip if it's just "#"
            
            const targetSection = document.querySelector(targetId);
            if (!targetSection) return;

            // Add smooth scroll behavior
            targetSection.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });

            // Update URL without jumping (optional)
            history.pushState(null, '', targetId);
        });
    });
}); 