// 🎁 Benefits Management JavaScript
// Where we make managing benefits as fun as opening presents!
import { Logger } from './logger.js';
import { CrudOperations } from './crud.js';

document.addEventListener('DOMContentLoaded', function() {
    Logger.info('Benefits', '🎯 Time to manage some awesome benefits!');

    // 📝 Configure field definitions for the benefit modal
    // These fields are like ingredients in a recipe for success!
    const benefitFields = {
        title: {
            type: 'text',
            label: 'Title',
            placeholder: 'What magical benefit shall we add today?',
            required: true,
            maxLength: 100
        },
        description: {
            type: 'textarea',
            label: 'Description',
            placeholder: 'Tell us more about this amazing benefit...',
            required: true,
            maxLength: 500,
            rows: 4
        },
        displayOrder: {
            type: 'number',
            label: 'Display Order',
            placeholder: 'Pick a number (1-4)',
            required: true,
            min: 1,
            max: 4,
            defaultValue: 1,
            help: 'This determines where your benefit appears (1-4)'
        }
    };

    // 🎮 Initialize CRUD operations for benefits
    // It's like a video game: Create, Read, Update, Delete!
    window.benefitCrud = new CrudOperations('benefit', {
        apiEndpoint: '/api/admin/benefits',
        fields: benefitFields,
        onSuccess: {
            create: () => {
                Logger.success('Benefits', '🎉 Woohoo! New benefit added to the collection!');
                Swal.fire({
                    title: 'Success!',
                    text: 'Your new benefit is ready to shine!',
                    icon: 'success',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
                location.reload();
            },
            update: () => {
                Logger.success('Benefits', '✨ Benefit got a magical makeover!');
                Swal.fire({
                    title: 'Updated!',
                    text: 'Your benefit is now even better!',
                    icon: 'success',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
                location.reload();
            },
            delete: () => {
                Logger.success('Benefits', '🧹 Benefit gracefully retired!');
                Swal.fire({
                    title: 'Deleted!',
                    text: 'The benefit has been removed.',
                    icon: 'success',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
                location.reload();
            }
        },
        onError: {
            create: (error) => {
                Logger.error('Benefits', `💥 Oops! Benefit creation hit a snag: ${error}`);
                Swal.fire({
                    title: 'Error!',
                    text: 'Could not create the benefit. Please try again!',
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
            },
            update: (error) => {
                Logger.error('Benefits', `🚨 Update mission failed: ${error}`);
                Swal.fire({
                    title: 'Error!',
                    text: 'Could not update the benefit. Please try again!',
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
            },
            delete: (error) => {
                Logger.error('Benefits', `🌋 Delete operation encountered resistance: ${error}`);
                Swal.fire({
                    title: 'Error!',
                    text: 'Could not delete the benefit. Please try again!',
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
            }
        },
        // 🎨 Custom validation for our benefits
        validate: (data) => {
            const errors = [];
            
            if (!data.title || data.title.trim().length === 0) {
                errors.push('Title is required! Give your benefit a catchy name! 🏷️');
            }
            
            if (!data.description || data.description.trim().length === 0) {
                errors.push('Description is required! Tell us what makes this benefit special! 📝');
            }
            
            if (!data.displayOrder || data.displayOrder < 1 || data.displayOrder > 4) {
                errors.push('Display order must be between 1 and 4! Choose wisely! 🎯');
            }
            
            return errors;
        }
    });

    Logger.info('Benefits', '✨ Benefits Management is ready to rock and roll!');
});
