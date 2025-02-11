// 🤔 FAQ Handlers
// Making those frequently asked questions a breeze to manage!

import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';
import { fieldConfigs } from './field-configs.js';

// 🎯 Initialize our CRUD operations
const crud = new CrudOperations('faq', {
    apiEndpoint: '/api/admin/faqs',
    onSuccess: {
        create: () => {
            Logger.info('FAQs', 'FAQ created successfully! 🎉');
            location.reload(); // Refresh to show new order
        },
        update: () => {
            Logger.info('FAQs', 'FAQ updated successfully! ✨');
            location.reload(); // Refresh to show new order
        },
        delete: () => {
            Logger.info('FAQs', 'FAQ deleted successfully! 🗑️');
            location.reload();
        }
    },
    onError: {
        create: (error) => Logger.error('FAQs', 'Failed to create FAQ 😢', error),
        update: (error) => Logger.error('FAQs', 'Failed to update FAQ 😢', error),
        delete: (error) => Logger.error('FAQs', 'Failed to delete FAQ 😢', error)
    }
});

// 🎭 Initialize drag and drop reordering
function initializeSortable() {
    const faqList = document.getElementById('faqList');
    if (!faqList) {
        Logger.warn('FAQs', 'FAQ list element not found! 🤔');
        return;
    }

    Logger.info('FAQs', 'Initializing drag and drop for FAQs! 🎯');
    
    try {
        // Use the global Sortable object since we're loading it via CDN
        if (typeof Sortable === 'undefined') {
            throw new Error('Sortable.js not loaded');
        }

        new Sortable(faqList, {
            handle: '.drag-handle',
            animation: 150,
            ghostClass: 'bg-gray-100',
            onEnd: async (evt) => {
                const items = [...faqList.children];
                const updates = items.map((item, index) => ({
                    id: parseInt(item.dataset.id),
                    displayOrder: index
                }));

                try {
                    Logger.info('FAQs', 'Updating FAQ order... 🔄');
                    const response = await fetch('/api/admin/faqs/reorder', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(updates)
                    });

                    if (!response.ok) {
                        throw new Error('Failed to update FAQ order');
                    }

                    Logger.success('FAQs', 'FAQ order updated successfully! ✨');
                } catch (error) {
                    Logger.error('FAQs', 'Failed to update FAQ order 😢', error);
                }
            }
        });
        Logger.info('FAQs', 'Drag and drop initialized! 🎉');
    } catch (error) {
        Logger.error('FAQs', 'Failed to initialize drag and drop 😢', error);
    }
}

// 🎬 Initialize everything when the DOM is ready
window.addEventListener('DOMContentLoaded', () => {
    Logger.info('FAQs', 'Initializing FAQ management... 🎬');
    initializeSortable();
});

// 🗑️ Delete FAQ with confirmation
window.deleteItem = async (type, id) => {
    try {
        const result = await Swal.fire({
            title: 'Are you sure? 🤔',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it! 🗑️',
            cancelButtonText: 'No, keep it! 🛡️'
        });

        if (result.isConfirmed) {
            await crud.delete(id);
        }
    } catch (error) {
        Logger.error('FAQs', 'Failed to delete FAQ 😢', error);
        Swal.fire({
            title: 'Error! 😢',
            text: 'Failed to delete FAQ. Please try again.',
            icon: 'error'
        });
    }
};
