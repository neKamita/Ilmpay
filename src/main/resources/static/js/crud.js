// 🎯 CRUD Operations Helper

/**
 * CrudOperations class for handling CRUD operations
 */
class CrudOperations {
    /**
     * Initialize CRUD operations
     * @param {string} type - The type of item (e.g., 'benefit', 'support-logo')
     * @param {Object} config - Configuration object
     */
    constructor(type, config) {
        this.type = type;
        this.apiEndpoint = config.apiEndpoint;
        this.fields = config.fields;
        this.onSuccess = config.onSuccess || {};
        this.onError = config.onError || {};
        
        // Initialize the modal handlers
        this.initializeModalHandlers();
    }

    /**
     * Initialize modal event handlers
     */
    initializeModalHandlers() {
        // Handle form submission
        document.addEventListener('modal:submit', (event) => {
            if (event.detail.type === this.type) {
                const formData = event.detail.formData;
                const id = event.detail.id;
                
                if (id) {
                    this.update(id, formData);
                } else {
                    this.create(formData);
                }
            }
        });

        // Handle modal open to set up fields
        document.addEventListener('modal:open', (event) => {
            if (event.detail.type === this.type) {
                Modal.setFields(this.fields);
            }
        });
    }

    /**
     * Create a new item
     * @param {FormData} formData - Form data for the new item
     */
    create(formData) {
        fetch(this.apiEndpoint, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            Modal.hide();
            if (this.onSuccess.create) {
                this.onSuccess.create(data);
            }
        })
        .catch(error => {
            console.error('Create Error:', error);
            if (this.onError.create) {
                this.onError.create(error.message);
            }
        });
    }

    /**
     * Update an existing item
     * @param {number} id - ID of the item to update
     * @param {FormData} formData - Updated form data
     */
    update(id, formData) {
        fetch(`${this.apiEndpoint}/${id}`, {
            method: 'PUT',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || `HTTP error! status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(data => {
            Modal.hide();
            if (this.onSuccess.update) {
                this.onSuccess.update(data);
            }
        })
        .catch(error => {
            console.error('Update Error:', error);
            if (this.onError.update) {
                this.onError.update(error.message);
            }
        });
    }
}

/**
 * 🗑️ Delete an item with confirmation
 * @param {string} type - The type of item (e.g., 'support-logo')
 * @param {number} id - The ID of the item to delete
 */
function deleteItem(type, id) {
    // Configuration for different types
    const config = {
        'support-logo': {
            title: 'Delete Support Logo',
            text: 'Are you sure you want to delete this logo? This action cannot be undone.',
            endpoint: '/api/admin/support-logos'
        },
        'benefit': {
            title: 'Delete Benefit',
            text: 'Are you sure you want to delete this benefit? This action cannot be undone.',
            endpoint: '/api/admin/benefits'
        }
    };

    const typeConfig = config[type];
    if (!typeConfig) {
        console.error(`❌ Unknown type: ${type}`);
        return;
    }

    // Show confirmation dialog
    Swal.fire({
        title: typeConfig.title,
        text: typeConfig.text,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444', // Red color for danger
        cancelButtonColor: '#6b7280', // Gray color
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            // Show loading state
            Swal.fire({
                title: 'Deleting...',
                didOpen: () => {
                    Swal.showLoading();
                },
                allowOutsideClick: false,
                allowEscapeKey: false,
                showConfirmButton: false
            });

            // Send delete request
            fetch(`${typeConfig.endpoint}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                // Show success message
                Swal.fire({
                    title: 'Deleted!',
                    text: 'The item has been deleted successfully.',
                    icon: 'success',
                    timer: 1500
                }).then(() => {
                    // Reload the page to reflect changes
                    window.location.reload();
                });
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    title: 'Error!',
                    text: 'Failed to delete the item. Please try again.',
                    icon: 'error'
                });
            });
        }
    });
}

/**
 * 🔄 Update an item
 * @param {string} type - The type of item (e.g., 'support-logo')
 * @param {number} id - The ID of the item to update
 * @param {FormData} formData - The form data to send
 * @returns {Promise} - A promise that resolves when the update is complete
 */
function updateItem(type, id, formData) {
    const config = {
        'support-logo': {
            endpoint: '/api/admin/support-logos'
        },
        'benefit': {
            endpoint: '/api/admin/benefits'
        }
    };

    const typeConfig = config[type];
    if (!typeConfig) {
        return Promise.reject(new Error(`Unknown type: ${type}`));
    }

    // Log the form data for debugging
    console.debug('🔄 Update form data:', {
        type,
        id,
        formData: Object.fromEntries(formData.entries())
    });

    return fetch(`${typeConfig.endpoint}/${id}`, {
        method: 'PUT',
        body: formData
    }).then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || `HTTP error! status: ${response.status}`);
            });
        }
        return response.json();
    });
}
