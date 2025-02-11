// 🎮 Benefits Event Handlers
// Making benefit management smooth as butter!
import { Logger } from './logger.js';
import { Modal } from './modal.js';
import { CrudOperations } from './crud.js';

// 🎯 Initialize our CRUD operations
const crud = new CrudOperations('benefit', {
    apiEndpoint: '/api/admin/benefits',
    onSuccess: {
        create: () => {
            Logger.info('Benefits', 'Benefit created successfully! 🎉');
            location.reload();
        },
        update: () => {
            Logger.info('Benefits', 'Benefit updated successfully! ✨');
            location.reload();
        },
        delete: () => {
            Logger.info('Benefits', 'Benefit deleted successfully! 🗑️');
            location.reload();
        }
    },
    onError: {
        create: (error) => Logger.error('Benefits', 'Failed to create benefit 😢', error),
        update: (error) => Logger.error('Benefits', 'Failed to update benefit 😢', error),
        delete: (error) => Logger.error('Benefits', 'Failed to delete benefit 😢', error)
    }
});

// 🎭 Initialize drag and drop reordering
function initializeSortable() {
    const benefitList = document.getElementById('benefitList');
    if (!benefitList) {
        Logger.warn('Benefits', 'Benefit list element not found! 🤔');
        return;
    }

    Logger.info('Benefits', 'Initializing drag and drop for benefits! 🎯');
    
    try {
        if (typeof Sortable === 'undefined') {
            throw new Error('Sortable.js not loaded');
        }

        // Store original order for potential revert
        let originalOrder = [];

        new Sortable(benefitList, {
            handle: '.drag-handle',
            animation: 150,
            ghostClass: 'bg-gray-100',
            onStart: (evt) => {
                // Save the original order before dragging starts
                originalOrder = Array.from(benefitList.children).map(item => ({
                    element: item,
                    id: parseInt(item.dataset.id),
                    order: parseInt(item.dataset.order)
                }));
            },
            onEnd: async (evt) => {
                const items = [...benefitList.children];
                const updates = items.map((item, index) => ({
                    id: parseInt(item.dataset.id),
                    displayOrder: index + 1 // Benefits are 1-based
                }));

                try {
                    Logger.info('Benefits', 'Updating benefit order... 🔄');
                    const response = await fetch('/api/admin/benefits/reorder', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json'
                        },
                        body: JSON.stringify(updates)
                    });

                    const result = await response.json();

                    if (!response.ok || !result.success) {
                        throw new Error(result.message || 'Failed to update benefit order');
                    }

                    // Update the display order in the DOM
                    result.data.forEach(benefit => {
                        const benefitElement = benefitList.querySelector(`[data-id="${benefit.id}"]`);
                        if (benefitElement) {
                            benefitElement.dataset.order = benefit.displayOrder;
                            // Update any order display elements if they exist
                            const orderDisplay = benefitElement.querySelector('.order-display');
                            if (orderDisplay) {
                                orderDisplay.textContent = benefit.displayOrder;
                            }
                        }
                    });

                    Logger.success('Benefits', 'Benefit order updated successfully! ✨');
                    Swal.fire({
                        title: 'Success! 🎉',
                        text: result.funnyMessage || 'Benefits reordered successfully!',
                        icon: 'success',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                } catch (error) {
                    // Revert to original order
                    originalOrder.sort((a, b) => a.order - b.order)
                        .forEach(item => {
                            benefitList.appendChild(item.element);
                            // Restore original order in data attribute and display
                            item.element.dataset.order = item.order;
                            const orderDisplay = item.element.querySelector('.order-display');
                            if (orderDisplay) {
                                orderDisplay.textContent = item.order;
                            }
                        });

                    Logger.error('Benefits', 'Failed to update benefit order 😢', error);
                    Swal.fire({
                        title: 'Error! 😢',
                        text: error.message || 'Failed to update benefit order. Please try again.',
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                }
            }
        });
        Logger.info('Benefits', 'Drag and drop initialized! 🎉');
    } catch (error) {
        Logger.error('Benefits', 'Failed to initialize drag and drop 😢', error);
    }
}

// 🎬 Initialize everything when the DOM is ready
window.addEventListener('DOMContentLoaded', () => {
    Logger.info('Benefits', 'Initializing benefit management... 🎬');
    initializeSortable();
});

// 🗑️ Delete benefit with confirmation
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
                text: 'Your benefit has been deleted.',
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        }
    } catch (error) {
        Logger.error('Benefits', 'Failed to delete benefit 😢', error);
        Swal.fire({
            title: 'Error! 😢',
            text: 'Failed to delete benefit. Please try again.',
            icon: 'error',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
        });
    }
};
