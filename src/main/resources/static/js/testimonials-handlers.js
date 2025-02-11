// 🌟 Testimonials Event Handlers
// Making testimonial management shine bright!
import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';

// Initialize testimonial operations
const testimonialOperations = new CrudOperations('testimonial', {
    apiEndpoint: '/api/admin/testimonials',
    onSuccess: {
        create: (result) => {
            Logger.info('Testimonials', '✨ New testimonial created successfully!');
            Modal.close();
            window.location.reload();
        },
        update: (result) => {
            Logger.info('Testimonials', '✨ Testimonial updated successfully!');
            Modal.close();
            window.location.reload();
        },
        delete: () => {
            Logger.info('Testimonials', '🗑️ Testimonial deleted successfully!');
            window.location.reload();
        }
    },
    onError: {
        create: (error) => {
            Logger.error('Testimonials', '❌ Failed to create testimonial:', error);
            Swal.fire({
                title: 'Error!',
                text: 'Failed to create testimonial. Please try again.',
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        },
        update: (error) => {
            Logger.error('Testimonials', '❌ Failed to update testimonial:', error);
            Swal.fire({
                title: 'Error!',
                text: 'Failed to update testimonial. Please try again.',
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        },
        delete: (error) => {
            Logger.error('Testimonials', '❌ Failed to delete testimonial:', error);
            Swal.fire({
                title: 'Error!',
                text: 'Failed to delete testimonial. Please try again.',
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    }
});

// 🎭 Initialize drag and drop reordering
function initializeSortable() {
    const testimonialList = document.getElementById('testimonialList');
    if (!testimonialList) {
        Logger.warn('Testimonials', 'Testimonial list element not found! 🤔');
        return;
    }

    Logger.info('Testimonials', 'Initializing drag and drop for testimonials! 🎯');
    
    try {
        if (typeof Sortable === 'undefined') {
            throw new Error('Sortable.js not loaded');
        }

        new Sortable(testimonialList, {
            handle: '.drag-handle',
            animation: 150,
            ghostClass: 'bg-gray-100',
            onEnd: async (evt) => {
                const items = [...testimonialList.children];
                const updates = items.map((item, index) => ({
                    id: parseInt(item.dataset.id),
                    displayOrder: index + 1 // Make it 1-based like benefits
                }));

                try {
                    Logger.info('Testimonials', 'Updating testimonial order... 🔄');
                    const response = await fetch('/api/admin/testimonials/reorder', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json'
                        },
                        body: JSON.stringify(updates)
                    });

                    if (!response.ok) {
                        throw new Error('Failed to update testimonial order');
                    }

                    const result = await response.json();
                    if (result.success) {
                        Logger.success('Testimonials', 'Testimonial order updated successfully! ✨');
                        Swal.fire({
                            title: 'Success! 🎉',
                            text: result.funnyMessage || 'Testimonials reordered successfully!',
                            icon: 'success',
                            toast: true,
                            position: 'top-end',
                            showConfirmButton: false,
                            timer: 3000
                        });
                    } else {
                        throw new Error(result.message || 'Failed to update testimonial order');
                    }
                } catch (error) {
                    Logger.error('Testimonials', 'Failed to update testimonial order 😢', error);
                    Swal.fire({
                        title: 'Error! 😢',
                        text: error.message || 'Failed to update testimonial order. Please try again.',
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                }
            }
        });
        Logger.info('Testimonials', 'Drag and drop initialized! 🎉');
    } catch (error) {
        Logger.error('Testimonials', 'Failed to initialize drag and drop 😢', error);
    }
}

// 🎬 Initialize everything when the DOM is ready
window.addEventListener('DOMContentLoaded', () => {
    Logger.info('Testimonials', 'Initializing testimonial management... 🎬');
    initializeSortable();
});

// 🗑️ Delete testimonial with confirmation
function deleteItem(type, id) {
    Swal.fire({
        title: 'Are you sure? 🤔',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it! 🗑️'
    }).then((result) => {
        if (result.isConfirmed) {
            testimonialOperations.delete(id)
                .then(() => {
                    Swal.fire({
                        title: 'Deleted! 🎉',
                        text: 'Your testimonial has been deleted.',
                        icon: 'success',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                })
                .catch((error) => {
                    Logger.error('Testimonials', '❌ Failed to delete testimonial:', error);
                    Swal.fire({
                        title: 'Error! 😢',
                        text: 'Failed to delete testimonial. Please try again.',
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                });
        }
    });
}

// Make deleteItem available globally for HTML onclick handlers
window.deleteItem = deleteItem;
