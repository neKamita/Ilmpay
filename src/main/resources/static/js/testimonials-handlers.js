// 🌟 Testimonials Event Handlers
// Making testimonial management shine bright!
import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';

// Initialize testimonial operations
const testimonialOperations = new CrudOperations('testimonial', {
    apiEndpoint: '/admin/testimonials',
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

// Export deleteItem for global use
export async function deleteItem(type, id) {
    Logger.info('Testimonials', '🗑️ Attempting to delete testimonial:', { id });
    
    // Show confirmation dialog
    const result = await Swal.fire({
        title: 'Are you sure?',
        text: 'This testimonial will be permanently deleted.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    });

    if (result.isConfirmed) {
        try {
            await testimonialOperations.delete(id);
            
            // Show success message
            Swal.fire({
                title: 'Deleted!',
                text: 'Testimonial has been deleted.',
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000
            });
            
            // Reload the page
            window.location.reload();
        } catch (error) {
            Logger.error('Testimonials', '❌ Delete operation failed:', error);
            
            // Show error message
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
}

// Make deleteItem available globally for HTML onclick handlers
window.deleteItem = deleteItem;
