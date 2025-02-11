// 🎯 CRUD Operations Helper - For all your data needs! 
import { Logger } from './logger.js';

// 🎭 CRUD Configuration
const crudConfig = {
    'support-logo': {
        endpoint: '/api/admin/support-logos',
        messages: {
            deleteConfirm: 'Are you sure you want to delete this logo?',
            deleteSuccess: 'Logo deleted successfully! 🎉',
            deleteError: 'Failed to delete logo 😢'
        }
    },
    'benefit': {
        endpoint: '/api/admin/benefits',
        messages: {
            deleteConfirm: 'Are you sure you want to delete this benefit? This action cannot be undone.',
            deleteSuccess: 'Benefit deleted successfully! Your benefits list is now updated 🎉',
            deleteError: 'Oops! Failed to delete the benefit. Please try again 😢'
        }
    },
    'testimonial': {
        endpoint: '/admin/testimonials',
        messages: {
            deleteConfirm: 'Are you sure you want to delete this testimonial? This action cannot be undone.',
            deleteSuccess: 'Testimonial deleted successfully! Your testimonials list is now updated 🌟',
            deleteError: 'Oops! Failed to delete the testimonial. Please try again 😢'
        }
    },
    'faq': {
        endpoint: '/api/admin/faqs',
        messages: {
            deleteConfirm: 'Are you sure you want to delete this FAQ? This action cannot be undone.',
            deleteSuccess: 'FAQ deleted successfully! 🎉',
            deleteError: 'Failed to delete FAQ 😢'
        }
    }
};

// Define the CrudOperations class
export class CrudOperations {
    constructor(type, config) {
        this.type = type;
        this.config = config;
        Logger.info('CrudOperations', `🎮 Initializing CRUD operations for ${type}`);
    }

    async create(data) {
        try {
            const response = await fetch(this.config.apiEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            const result = await response.json();
            this.config.onSuccess.create(result);
            return result;
        } catch (error) {
            this.config.onError.create(error);
            throw error;
        }
    }

    async update(id, data) {
        try {
            const response = await fetch(`${this.config.apiEndpoint}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });
            
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            const result = await response.json();
            this.config.onSuccess.update(result);
            return result;
        } catch (error) {
            this.config.onError.update(error);
            throw error;
        }
    }

    async delete(id) {
        try {
            const response = await fetch(`${this.config.apiEndpoint}/${id}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            this.config.onSuccess.delete();
            return true;
        } catch (error) {
            this.config.onError.delete(error);
            throw error;
        }
    }
}

/**
 * Delete an item with confirmation
 * @param {string} type - The type of item (e.g., 'support-logo', 'testimonial')
 * @param {number} id - The ID of the item to delete
 */
export async function deleteItem(type, id) {
    const config = crudConfig[type];
    if (!config) {
        console.error(`❌ Unknown type: ${type}`);
        return;
    }

    // Show confirmation dialog
    const result = await Swal.fire({
        title: 'Are you sure?',
        text: config.messages.deleteConfirm,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    });

    if (result.isConfirmed) {
        try {
            const response = await fetch(`${config.endpoint}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            // Show success message
            Swal.fire({
                title: 'Deleted!',
                text: config.messages.deleteSuccess,
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000
            });

            // Reload the page
            window.location.reload();
        } catch (error) {
            console.error('Delete failed:', error);
            Swal.fire({
                title: 'Error!',
                text: config.messages.deleteError,
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    }
}

// Initialize when the script loads
Logger.info('CrudOperations', '🎯 CRUD Operations initialized');
