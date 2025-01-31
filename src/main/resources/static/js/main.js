// Global state
let currentType = '';

function openModal(type, data = null) {
    currentType = type;
    const modal = document.getElementById('crudModal');
    const form = document.getElementById('crudForm');
    const title = document.getElementById('modalTitle');
    
    if (title) {
        // Set title based on type
        switch(type) {
            case 'support-logo':
                title.textContent = data ? 'Edit Logo' : 'New Support Logo';
                break;
            case 'benefit':
                title.textContent = data ? 'Edit Benefit' : 'New Benefit';
                break;
            case 'testimonial':
                title.textContent = data ? 'Edit Testimonial' : 'New Testimonial';
                break;
            case 'faq':
                title.textContent = data ? 'Edit FAQ' : 'New FAQ';
                break;
        }
    } else {
        console.error('Modal title element not found');
    }

    // Clear previous fields
    form.innerHTML = `
        <input type="hidden" id="itemId" value="${data?.id || ''}">
        ${generateFields(type, data)}
        <div class="flex justify-end space-x-3 pt-6 border-t">
            <button type="button" onclick="closeModal()" 
                    class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50">
                Cancel
            </button>
            <button type="submit" 
                    class="px-4 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700">
                Save Changes
            </button>
        </div>
    `;

    modal.classList.remove('hidden');
    setupFormSubmission(form, type);
}

function generateFields(type, data) {
    switch(type) {
        case 'support-logo':
            return `
                <div class="space-y-4">
                    <div class="form-group">
                        <label class="form-label">Name</label>
                        <input type="text" name="name" required class="input-field" 
                               value="${data?.name || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Image URL</label>
                        <input type="url" name="imageUrl" required class="input-field"
                               value="${data?.imageUrl || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Website URL</label>
                        <input type="url" name="websiteUrl" class="input-field"
                               value="${data?.websiteUrl || ''}">
                    </div>
                </div>
            `;
        case 'benefit':
            return `
                <div class="space-y-4">
                    <div class="form-group">
                        <label class="form-label">Title</label>
                        <input type="text" name="title" required class="input-field"
                               value="${data?.title || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <textarea name="description" required class="input-field">${data?.description || ''}</textarea>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Icon URL</label>
                        <input type="url" name="iconUrl" required class="input-field"
                               value="${data?.iconUrl || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Display Order (1-4)</label>
                        <input type="number" name="displayOrder" min="1" max="4" required class="input-field"
                               value="${data?.displayOrder || 1}">
                    </div>
                </div>
            `;
        case 'testimonial':
            return `
                <div class="space-y-4">
                    <div class="form-group">
                        <label class="form-label">Name</label>
                        <input type="text" name="name" required class="input-field"
                               value="${data?.name || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Role</label>
                        <input type="text" name="role" class="input-field"
                               value="${data?.role || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Comment</label>
                        <textarea name="comment" required class="input-field">${data?.comment || ''}</textarea>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Rating (1-5)</label>
                        <input type="number" name="rating" min="1" max="5" required class="input-field"
                               value="${data?.rating || 5}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Display Order (1-10)</label>
                        <input type="number" name="displayOrder" min="1" max="10" required class="input-field"
                               value="${data?.displayOrder || 1}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Avatar URL</label>
                        <input type="url" name="avatarUrl" class="input-field"
                               value="${data?.avatarUrl || ''}">
                    </div>
                </div>
            `;
        case 'faq':
            return `
                <div class="space-y-4">
                    <div class="form-group">
                        <label class="form-label">Question</label>
                        <input type="text" name="question" required class="input-field"
                               value="${data?.question || ''}">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Answer</label>
                        <textarea name="answer" required class="input-field">${data?.answer || ''}</textarea>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Display Order (1-20)</label>
                        <input type="number" name="displayOrder" min="1" max="20" required class="input-field"
                               value="${data?.displayOrder || 1}">
                    </div>
                </div>
            `;
        default:
            return '';
    }
}

function closeModal() {
    const modal = document.getElementById('crudModal');
    modal.classList.add('animate__fadeOut');
    setTimeout(() => {
        modal.classList.add('hidden');
        modal.classList.remove('animate__fadeOut');
    }, 300);
}

async function editItem(type, id) {
    try {
        // Show loading state
        Swal.fire({
            title: 'Loading...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        const endpoints = {
            'support-logo': 'support-logos',
            'benefit': 'benefit-cards',
            'testimonial': 'testimonials',
            'faq': 'faqs'
        };
        
        const endpoint = endpoints[type];
        const response = await fetch(`/api/admin/${endpoint}/${id}`);
        const result = await response.json();

        if (!result.success) {
            throw new Error(result.message || 'Failed to fetch item');
        }

        // Close loading indicator
        Swal.close();

        // Open modal with data
        openModal(type, result.data);

    } catch (error) {
        console.error('Error:', error);
        await Swal.fire({
            title: 'Error!',
            text: error.message || 'An unexpected error occurred',
            icon: 'error',
            confirmButtonColor: '#4f46e5'
        });
    }
}

function setupFormSubmission(form, type) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        // Add active status if not present
        if (!data.hasOwnProperty('active')) {
            data.active = true;
        }
        
        const itemId = document.getElementById('itemId').value;
        
        // Show loading state
        Swal.fire({
            title: 'Saving...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        const endpoints = {
            'support-logo': 'support-logos',
            'benefit': 'benefit-cards',
            'testimonial': 'testimonials',
            'faq': 'faqs'
        };
        
        try {
            const endpoint = endpoints[type];
            const url = itemId 
                ? `/api/admin/${endpoint}/${itemId}`
                : `/api/admin/${endpoint}`;

            const response = await fetch(url, {
                method: itemId ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            const responseData = await response.json();
            if (!responseData.success) {
                throw new Error(responseData.message || 'Failed to save');
            }
            
            await Swal.fire({
                title: 'Success!',
                text: itemId ? 'Item updated successfully!' : 'Item created successfully!',
                icon: 'success',
                confirmButtonColor: '#4f46e5'
            });

            closeModal();
            location.reload();
        } catch (error) {
            console.error('Error:', error);
            await Swal.fire({
                title: 'Error!',
                text: error.message,
                icon: 'error',
                confirmButtonColor: '#4f46e5'
            });
        }
    });
}

// Add this function for delete confirmation
async function deleteItem(type, id) {
    const result = await Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#4f46e5',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    });

    if (!result.isConfirmed) return;

    const endpoints = {
        'support-logo': 'support-logos',
        'benefit': 'benefit-cards',
        'testimonial': 'testimonials',
        'faq': 'faqs'
    };
    
    const endpoint = endpoints[type];
    
    try {
        const response = await fetch(`/api/admin/${endpoint}/${id}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        if (!data.success) {
            throw new Error(data.message);
        }

        await Swal.fire({
            title: 'Deleted!',
            text: 'Item has been deleted successfully.',
            icon: 'success',
            confirmButtonColor: '#4f46e5'
        });
        
        location.reload();
    } catch (error) {
        console.error('Error:', error);
        await Swal.fire({
            title: 'Error!',
            text: 'Failed to delete item: ' + error.message,
            icon: 'error',
            confirmButtonColor: '#4f46e5'
        });
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Lucide icons
    lucide.createIcons();
    
    // Initialize Support Carousel - Smooth infinite logo animation! 
    $('.support-carousel').owlCarousel({
        loop: true,
        margin: 40,
        nav: false,
        dots: false,
        autoplay: true,
        autoplayTimeout: 2000,
        autoplaySpeed: 2000,
        autoplayHoverPause: true,
        slideTransition: 'linear',
        responsive: {
            0: {
                items: 2
            },
            576: {
                items: 3
            },
            768: {
                items: 4
            },
            992: {
                items: 6
            }
        }
    });

    // Initialize Testimonial Carousel - Let the happy students shine! 
    const testimonialCarousel = $('.testimonial-carousel').owlCarousel({
        loop: true,
        margin: 24,
        nav: false,
        dots: false, // Disable default dots
        autoplay: true,
        autoplayTimeout: 2000,
        autoplayHoverPause: true,
        smartSpeed: 700,
        slideTransition: 'ease',
        responsive: {
            0: {
                items: 1,
            },
            576: {
                items: 2,
            },
            768: {
                items: 3,
            },
            992: {
                items: 4,
            }
        }
    });

    // Create custom dots container if it doesn't exist
    if (!document.querySelector('.custom-dots-container')) {
        const dotsContainer = document.createElement('div');
        dotsContainer.className = 'custom-dots-container';
        $('.testimonial-carousel').after(dotsContainer);
    }

    // Update custom dots based on carousel items
    function updateCustomDots() {
        const dotsContainer = document.querySelector('.custom-dots-container');
        
        // Clear existing dots
        dotsContainer.innerHTML = '';
        
        // Create 4 dots
        for (let i = 0; i < 4; i++) {
            const dot = document.createElement('div');
            dot.className = 'custom-dot' + (i === 0 ? ' active' : '');
            dot.addEventListener('click', () => {
                testimonialCarousel.trigger('to.owl.carousel', [i, 700]);
                updateActiveDot(i);
            });
            dotsContainer.appendChild(dot);
        }
    }

    // Update active dot based on current slide
    function updateActiveDot(index) {
        document.querySelectorAll('.custom-dot').forEach((dot, i) => {
            dot.classList.toggle('active', i === index);
        });
    }

    // Initialize custom dots
    updateCustomDots();

    // Update dots on carousel change
    testimonialCarousel.on('changed.owl.carousel', function(event) {
        updateActiveDot(event.item.index % 4);
    });

    // Pause autoplay on dot hover
    document.querySelector('.custom-dots-container').addEventListener('mouseenter', () => {
        testimonialCarousel.trigger('stop.owl.autoplay');
    });

    document.querySelector('.custom-dots-container').addEventListener('mouseleave', () => {
        testimonialCarousel.trigger('play.owl.autoplay');
    });

    // Expose functions to window
    window.openSupportLogoModal = (data) => openModal('support-logo', data);
    window.openBenefitModal = (data) => openModal('benefit', data);
    window.openTestimonialModal = (data) => openModal('testimonial', data);
    window.openFaqModal = (data) => openModal('faq', data);
    window.editItem = editItem;
    window.deleteItem = deleteItem;
    window.closeModal = closeModal;
});
