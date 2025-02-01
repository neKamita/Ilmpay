// 🎯 CRUD Operations Helper

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
        }
        // Add other type configurations as needed
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
        }
        // Add other type configurations as needed
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
