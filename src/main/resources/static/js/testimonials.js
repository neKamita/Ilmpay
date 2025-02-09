/**
 * 🌟 Testimonials Management Module
 * 
 * This module handles the CRUD operations for testimonials in the admin panel.
 * It's designed to work seamlessly with the modal system and provides a smooth
 * user experience for managing student testimonials.
 * 
 * @author neKamita
 * @version 1.0.0
 */

import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';

// Initialize testimonial configuration
function initTestimonialConfig() {
    Logger.info('Testimonials', '🎭 Initializing testimonial configuration');

    // Check if Modal is available
    if (typeof Modal === 'undefined' || !Modal.config) {
        Logger.warn('Testimonials', '⚠️ Modal not ready, retrying in 100ms');
        setTimeout(initTestimonialConfig, 100);
        return;
    }

    // Add testimonial configuration to Modal.config
    Modal.config['testimonial'] = {
        title: 'Testimonial',
        endpoint: '/admin/testimonials',  
        fields: [
            {
                name: 'name',
                type: 'text',
                label: 'Student Name',
                placeholder: 'Enter student name',
                required: true,
                validation: {
                    minLength: 2,
                    maxLength: 100
                },
                help: '✨ Enter the name of the student providing the testimonial'
            },
            {
                name: 'comment',
                type: 'textarea',
                label: 'Testimonial',
                placeholder: 'Enter student testimonial',
                required: true,
                validation: {
                    minLength: 10,
                    maxLength: 500
                },
                help: '📝 Share the student\'s experience and feedback',
                rows: 4
            },
            {
                name: 'rating',
                type: 'number',
                label: 'Rating',
                placeholder: 'Enter rating (1-5)',
                required: true,
                validation: {
                    min: 1,
                    max: 5
                },
                help: '⭐ Rate the experience from 1 to 5 stars',
                min: 1,
                max: 5,
                step: 1
            },
            {
                name: 'imageFile',
                type: 'file',
                label: 'Student Photo',
                required: false,
                accept: 'image/*',
                help: '📸 Upload a photo of the student (optional)',
                uploadPath: 'testimonials/',
                filepond: {
                    allowImagePreview: true,
                    imagePreviewMaxHeight: 200,
                    allowImageCrop: true,
                    imageCropAspectRatio: '1:1',
                    labelIdle: '🖼️ Drag & Drop student photo or <span class="filepond--label-action">Browse</span>',
                    imageResizeTargetWidth: 400,
                    imageResizeTargetHeight: 400,
                    server: {
                        process: {
                            url: '/admin/testimonials/upload',
                            method: 'POST',
                            withCredentials: false,
                            headers: {},
                            timeout: 7000,
                            onload: null,
                            onerror: null
                        },
                        revert: null,
                        restore: null,
                        load: null,
                        fetch: null
                    }
                }
            }
        ],
        successMessage: {
            create: 'Testimonial added successfully! 🌟',
            update: 'Testimonial updated successfully! ✨',
            delete: 'Testimonial deleted successfully! 🗑️'
        },
        errorMessage: {
            create: 'Failed to add testimonial. Please try again.',
            update: 'Failed to update testimonial. Please try again.',
            delete: 'Failed to delete testimonial. Please try again.'
        }
    };

    Logger.info('Testimonials', '✅ Testimonial configuration initialized');
}

// Initialize when the DOM is ready
document.addEventListener('DOMContentLoaded', initTestimonialConfig);
