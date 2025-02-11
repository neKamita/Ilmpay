// 🖼️ Support Logos Event Handlers
// Managing those beautiful supporter logos with style!
import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';

// 🎯 Initialize our CRUD operations
const crud = new CrudOperations('support-logo', {
    apiEndpoint: '/admin/content/support-logos/reorder',
    onSuccess: {
        create: () => {
            Logger.info('Support Logos', 'Logo created successfully! 🎉');
            location.reload();
        },
        update: () => {
            Logger.info('Support Logos', 'Logo updated successfully! ✨');
            location.reload();
        },
        delete: () => {
            Logger.info('Support Logos', 'Logo deleted successfully! 🗑️');
            location.reload();
        }
    },
    onError: {
        create: (error) => Logger.error('Support Logos', 'Failed to create logo 😢', error),
        update: (error) => Logger.error('Support Logos', 'Failed to update logo 😢', error),
        delete: (error) => Logger.error('Support Logos', 'Failed to delete logo 😢', error)
    }
});

let originalOrder = [];

// 🎭 Initialize drag and drop reordering
function initializeSortable() {
    const logoList = document.getElementById('logoList');
    if (!logoList) {
        Logger.warn('Support Logos', 'Logo list container not found! 😢');
        return;
    }

    try {
        // Store original order for fallback
        originalOrder = [...logoList.children]
            .filter(item => !item.classList.contains('inactive'))
            .map(element => ({
                element,
                displayOrder: parseInt(element.dataset.displayOrder)
            }));
        
        Logger.debug('Support Logos', '📝 Original order stored', originalOrder);

        // Initialize Sortable
        new Sortable(logoList, {
            animation: 150,
            handle: '.drag-handle',
            ghostClass: 'sortable-ghost',
            chosenClass: 'sortable-chosen',
            dragClass: 'sortable-drag',
            
            // Update order badges during drag
            onSort: (evt) => {
                Logger.debug('Support Logos', '🔄 Sort event triggered', { 
                    oldIndex: evt.oldIndex, 
                    newIndex: evt.newIndex 
                });
                
                const items = [...logoList.children]
                    .filter(item => !item.classList.contains('inactive'));
                Logger.debug('Support Logos', '📊 Current items order', items.map(item => ({
                    id: item.dataset.id,
                    order: item.dataset.displayOrder
                })));
                
                items.forEach((item, index) => {
                    const orderBadge = item.querySelector('.order-badge');
                    if (orderBadge) {
                        orderBadge.textContent = index + 1;
                    }
                });
            },

            // Handle reordering
            onEnd: async (evt) => {
                // Skip if dragging an inactive item
                if (evt.item.classList.contains('inactive')) {
                    Logger.warn('Support Logos', '⚠️ Attempted to reorder inactive item');
                    return;
                }

                const items = [...logoList.children]
                    .filter(item => !item.classList.contains('inactive'));
                const updates = items.map((item, index) => ({
                    id: parseInt(item.dataset.id),
                    displayOrder: index + 1
                }));

                try {
                    Logger.info('Support Logos', 'Updating logo order... 🔄');
                    const response = await fetch('/admin/content/support-logos/reorder', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json'
                        },
                        body: JSON.stringify(updates)
                    });

                    if (!response.ok) {
                        throw new Error('Failed to update logo order');
                    }

                    const result = await response.json();
                    if (result.success) {
                        Logger.success('Support Logos', 'Logo order updated successfully! ✨');
                        
                        // Update the display order in the DOM
                        result.data.forEach(logo => {
                            const logoElement = logoList.querySelector(`[data-id="${logo.id}"]`);
                            if (logoElement) {
                                logoElement.dataset.displayOrder = logo.displayOrder;
                                const orderBadge = logoElement.querySelector('.order-badge');
                                if (orderBadge) {
                                    orderBadge.textContent = logo.displayOrder;
                                }
                            }
                        });

                        Swal.fire({
                            title: 'Success! 🎉',
                            text: result.funnyMessage || 'Logos reordered successfully!',
                            icon: 'success',
                            toast: true,
                            position: 'top-end',
                            showConfirmButton: false,
                            timer: 3000
                        });
                    } else {
                        throw new Error(result.message || 'Failed to update logo order');
                    }
                } catch (error) {
                    Logger.error('Support Logos', 'Failed to update logo order 😢', error);
                    
                    // Revert to original order like in benefits-handlers.js
                    originalOrder.sort((a, b) => a.displayOrder - b.displayOrder)
                        .forEach(item => {
                            logoList.appendChild(item.element);
                            item.element.dataset.displayOrder = item.displayOrder;
                            const orderBadge = item.element.querySelector('.order-badge');
                            if (orderBadge) {
                                orderBadge.textContent = item.displayOrder;
                            }
                        });

                    Swal.fire({
                        title: 'Error! 😢',
                        text: error.message || 'Failed to update logo order. Please try again.',
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                }
            }
        });

        Logger.info('Support Logos', '✨ Drag and drop initialized successfully!');
    } catch (error) {
        Logger.error('Support Logos', '😢 Failed to initialize drag and drop', error);
    }
}

// Helper function to revert to original order
function revertToOriginalOrder(container) {
    originalOrder.forEach(item => {
        const element = item.element;
        const parent = element.parentNode;
        const currentIndex = Array.from(parent.children).indexOf(element);
        const targetIndex = item.displayOrder - 1;
        
        if (currentIndex !== targetIndex) {
            const referenceNode = parent.children[targetIndex];
            parent.insertBefore(element, referenceNode);
        }
        
        // Update order badge
        const orderBadge = element.querySelector('.order-badge');
        if (orderBadge) {
            orderBadge.textContent = item.displayOrder;
        }
    });
}

// 🎬 Initialize everything when the DOM is ready
window.addEventListener('DOMContentLoaded', () => {
    Logger.info('Support Logos', 'Initializing logo management... 🎬');
    initializeSortable();
});

// 🗑️ Delete logo with confirmation
window.deleteItem = async (type, id) => {
    try {
        const result = await Swal.fire({
            title: 'Are you sure? 🤔',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it! 🗑️'
        });

        if (result.isConfirmed) {
            await crud.delete(id);
            Swal.fire({
                title: 'Deleted! 🎉',
                text: 'Your logo has been deleted.',
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    } catch (error) {
        Logger.error('Support Logos', 'Failed to delete logo 😢', error);
        Swal.fire({
            title: 'Error! 😢',
            text: 'Failed to delete logo. Please try again.',
            icon: 'error',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
        });
    }
};

// Export for use in other modules
export { initializeSortable };
