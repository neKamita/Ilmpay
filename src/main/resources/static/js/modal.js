// 🎭 Modal Management System - Where UI magic happens!
import Logger from './logger.js';
import { fieldConfigs } from './field-configs.js';
import { fieldTemplates } from './field-templates.js';

class Modal {
    // 🎪 Core properties
    static elements = {
        modal: null,
        form: null,
        title: null,
        formFields: null,
        closeButtons: null,
        submitBtn: null,
        filePonds: [] // Track FilePond instances
    };

    // 🎨 Modal configurations
    static config = {};

    // 🎨 State management
    static state = {
        currentType: null,
        currentData: null,
        onShowCallbacks: {},
        onCloseCallbacks: {}
    };

    // 🎯 Initialize the modal
    static init() {
        Logger.info('Modal', '🎬 Initializing modal system');
        
        // Get DOM elements (finding our stage props)
        this.elements.modal = document.getElementById('crudModal');
        this.elements.form = this.elements.modal.querySelector('#crudForm');
        this.elements.title = this.elements.modal.querySelector('#modalTitle');
        this.elements.formFields = this.elements.modal.querySelector('.form-fields');
        this.elements.closeButtons = this.elements.modal.querySelectorAll('.modal-close');
        this.elements.submitBtn = this.elements.form.querySelector('button[type="submit"]');

        if (!this.elements.modal || !this.elements.form) {
            Logger.error('Modal', '❌ Failed to initialize - Required elements not found');
            return;
        }

        // Initialize configurations
        this.initializeConfigs();
        
        // Set up event listeners
        this.setupEventListeners();
        
        Logger.info('Modal', '✅ Modal system initialized successfully');
    }

    // Initialize configurations
    static initializeConfigs() {
        this.config = {
            'support-logo': {
                title: 'Support Logo',
                endpoint: '/api/admin/support-logos',
                method: 'POST',
                fields: Object.entries(fieldConfigs['support-logo']).map(([name, field]) => ({
                    name,
                    ...field
                })),
                successMessage: {
                    create: 'Support logo created successfully!',
                    update: 'Support logo updated successfully!'
                }
            },
            'testimonial': {
                title: 'Testimonial',
                endpoint: '/api/admin/testimonials',
                method: 'POST',
                fields: [
                    {
                        name: 'title',
                        type: 'text',
                        label: 'Title',
                        placeholder: 'Enter testimonial title',
                        required: true
                    },
                    {
                        name: 'description',
                        type: 'textarea',
                        label: 'Description',
                        placeholder: 'Enter testimonial description',
                        required: true,
                        rows: 4
                    },
                    {
                        name: 'image',
                        type: 'file',
                        label: 'Image',
                        placeholder: 'Select an image',
                        required: true
                    }
                ],
                successMessage: {
                    create: 'Testimonial created successfully!',
                    update: 'Testimonial updated successfully!'
                }
            },
            'benefit': {
                title: 'Benefit',
                endpoint: '/api/admin/benefits',
                method: 'POST',
                fields: [
                    {
                        name: 'title',
                        type: 'text',
                        label: 'Title',
                        placeholder: 'Enter benefit title',
                        required: true
                    },
                    {
                        name: 'description',
                        type: 'textarea',
                        label: 'Description',
                        placeholder: 'Enter benefit description',
                        required: true,
                        rows: 4
                    },
                    {
                        name: 'order',
                        type: 'number',
                        label: 'Display Order',
                        placeholder: 'Enter display order (1-4)',
                        required: true,
                        min: 1,
                        max: 4,
                        step: 1
                    }
                ],
                successMessage: {
                    create: 'Benefit created successfully!',
                    update: 'Benefit updated successfully!'
                }
            }
        };
        
        Logger.info('Modal', '✅ Modal configurations initialized');
    }

    // 🎯 Set up event listeners
    static setupEventListeners() {
        Logger.debug('Modal', '🎯 Setting up event listeners');

        // Handle form submission
        this.elements.form.addEventListener('submit', (e) => this.handleSubmit(e));

        // Handle close buttons
        this.elements.closeButtons.forEach(button => {
            button.addEventListener('click', () => this.close());
        });

        // Handle escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !this.elements.modal.classList.contains('hidden')) {
                Logger.debug('Modal', '🔑 Escape key pressed - closing modal');
                this.close();
            }
        });

        Logger.debug('Modal', '✅ Event listeners setup complete');
    }

    // 🎯 Register callbacks
    static onShow(type, callback) {
        this.state.onShowCallbacks[type] = callback;
    }

    static onClose(type, callback) {
        this.state.onCloseCallbacks[type] = callback;
    }

    // 🔄 Set button loading state
    static setLoading(loading) {
        const btn = this.elements.submitBtn;
        if (!btn) return;
        
        if (loading) {
            btn.disabled = true;
            btn.innerHTML = `
                <svg class="animate-spin h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>Saving...</span>
            `;
        } else {
            btn.disabled = false;
            btn.innerHTML = `
                <i data-lucide="save"></i>
                <span>Save Changes</span>
            `;
            // Reinitialize Lucide icons
            if (window.lucide) {
                lucide.createIcons(btn);
            }
        }
    }

    // 🎨 Generate field HTML
    static generateFieldHtml(field) {
        const currentValue = this.state.currentData?.[field.name];
        Logger.debug('Modal', `🎨 Generating field HTML for ${field.name}`, { 
            fieldType: field.type, 
            currentValue,
            currentData: this.state.currentData 
        });
        
        // Get the field template
        const template = fieldTemplates[field.type];
        if (!template) {
            console.warn(`Unsupported field type: ${field.type}`);
            return '';
        }
        
        // Generate HTML using template
        return template(field, currentValue, this.state.currentData);
    }

    // 🎨 Initialize FilePond for file inputs
    static initializeFilePond() {
        Logger.debug('Modal', '🎣 Initializing FilePond instances');
        
        const inputs = this.elements.form.querySelectorAll('input[type="file"]');
        inputs.forEach(input => {   
            const inputId = input.getAttribute('name');
            
            // Get current image URL if editing
            const currentImageUrl = this.state.currentData?.imageUrl;
            Logger.debug('Modal', '🖼️ Current image data:', { 
                inputId, 
                currentImageUrl,
                currentData: this.state.currentData 
            });
            
            // Create FilePond instance (using global config from filepond-config.js)
            const pond = FilePond.create(input);
            
            // If we have a current image, add it to FilePond
            if (currentImageUrl) {
                Logger.debug('Modal', '🖼️ Loading existing image into FilePond:', currentImageUrl);
                
                // Load the image into FilePond
                pond.addFiles([{
                    source: currentImageUrl,
                    options: {
                        type: 'local',
                        metadata: {
                            poster: currentImageUrl
                        }
                    }
                }]).then(() => {
                    Logger.debug('Modal', '✨ Existing image loaded into FilePond');
                }).catch(error => {
                    Logger.error('Modal', '❌ Failed to load existing image:', error);
                });
            }
            
            this.elements.filePonds.push(pond);
        });
    }

    // 🎨 Update modal content
    static updateContent() {
        Logger.debug('Modal', '🎨 Updating modal content', {
            currentType: this.state.currentType,
            currentData: this.state.currentData
        });
        
        // Clear existing fields
        this.elements.formFields.innerHTML = '';
        
        // Clean up existing FilePond instances
        this.elements.filePonds.forEach(pond => {
            if (pond && typeof pond.destroy === 'function') {
                pond.destroy();
            }
        });
        this.elements.filePonds = [];

        // Get configuration
        const config = this.config[this.state.currentType];
        if (!config || !config.fields) return;

        // Generate fields HTML
        config.fields.forEach(field => {
            const fieldHtml = this.generateFieldHtml(field);
            if (fieldHtml) {
                this.elements.formFields.insertAdjacentHTML('beforeend', fieldHtml);
            }
        });

        // If we have current data, populate the fields
        if (this.state.currentData) {
            Logger.debug('Modal', '📝 Populating fields with data', this.state.currentData);
            Object.entries(this.state.currentData).forEach(([key, value]) => {
                const input = this.elements.form.querySelector(`[name="${key}"]`);
                if (input && value !== null && value !== undefined) {
                    if (input.type !== 'file') {
                        input.value = value;
                    }
                }
            });
        }
    }

    // 🎭 Show the modal
    static show(type, data = null) {
        Logger.debug('Modal', '🎭 Show modal', { type, data });
        
        if (!this.config[type]) {
            Logger.error('Modal', `❌ Invalid modal type: ${type}`);
            return;
        }
        
        // Set current state
        this.state.currentType = type;
        this.state.currentData = null;
        
        // Reset form and clear any existing FilePond instances
        this.elements.form.reset();
        this.elements.filePonds.forEach(pond => {
            if (pond && typeof pond.destroy === 'function') {
                pond.destroy();
            }
        });
        this.elements.filePonds = [];
        
        // Update title
        this.elements.title.textContent = `${data ? 'Edit' : 'Add'} ${this.config[type].title}`;
        
        // Show modal immediately to prevent flashing
        this.elements.modal.classList.remove('hidden');
        
        // If editing, fetch data first
        if (data) {
            Logger.debug('Modal', '🔄 Fetching existing data for editing', { id: data });
            
            // Show loading state
            this.setLoading(true);
            
            fetch(`${this.config[type].endpoint}/${data}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(response => {
                    Logger.debug('Modal', '✨ Received item data', response);
                    
                    // Extract the actual logo data from the response
                    const item = response.data;
                    if (!item) {
                        throw new Error('No data received from server');
                    }
                    
                    // Set the current data
                    this.state.currentData = item;
                    
                    Logger.debug('Modal', '📝 Extracted logo data', item);
                    
                    // Update content with the fetched data
                    this.updateContent();
                    
                    // Initialize FilePond after content is updated
                    this.initializeFilePond();
                    
                    // Reset loading state
                    this.setLoading(false);
                })
                .catch(error => {
                    Logger.error('Modal', '❌ Failed to fetch item data', error);
                    this.setLoading(false);
                    Swal.fire({
                        title: 'Error!',
                        text: 'Failed to load item data. Please try again.',
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000
                    });
                });
        } else {
            // For new items, just update content
            this.updateContent();
            this.initializeFilePond();
        }
        
        Logger.info('Modal', `✨ Modal shown successfully: ${type}`);
    }

    // 🚪 Close the modal
    static close() {
        Logger.group('Modal', '🚪 Closing modal');

        // Clean up FilePond instances
        this.elements.filePonds.forEach(pond => {
            Logger.debug('Modal', '🧹 Cleaning up FilePond instance');
            pond.destroy();
        });
        this.elements.filePonds = [];

        // Hide modal
        this.elements.modal.classList.add('hidden');
        
        // Execute onClose callbacks
        const type = this.state.currentType;
        if (type && this.state.onCloseCallbacks[type]) {
            Logger.debug('Modal', '📞 Executing onClose callback');
            this.state.onCloseCallbacks[type]();
        }

        // Reset state
        this.resetState();

        Logger.info('Modal', '✨ Modal closed successfully');
        Logger.groupEnd();
    }

    // 🧹 Reset state
    static resetState() {
        this.state.currentType = null;
        this.state.currentData = null;
        this.elements.form.reset();
    }

    // 📝 Handle form submission
    static async handleSubmit(e) {
        e.preventDefault();
        Logger.info('Modal', '📝 Form submission started');

        try {
            const formData = new FormData(this.elements.form);
            const currentType = this.state.currentType;

            // Handle file uploads for both support-logo and testimonial types
            if (currentType === 'support-logo' || currentType === 'testimonial') {
                // Find FilePond instance - try multiple selectors
                let filePondInput = this.elements.form.querySelector('input[type="file"].filepond');
                if (!filePondInput) {
                    filePondInput = this.elements.form.querySelector('.filepond--root');
                }
                if (!filePondInput) {
                    filePondInput = document.querySelector('input[name="imageFile"].filepond--browser');
                }
                
                Logger.debug('Modal', '🔍 FilePond input search:', {
                    foundInput: !!filePondInput,
                    inputType: filePondInput ? filePondInput.tagName : null,
                    inputClass: filePondInput ? filePondInput.className : null
                });
                
                // Get FilePond instance and check for files
                const pond = filePondInput ? FilePond.find(filePondInput) : null;
                Logger.debug('Modal', '🔍 Checking FilePond:', {
                    hasPond: !!pond,
                    files: pond ? pond.getFiles() : [],
                    filesLength: pond ? pond.getFiles().length : 0
                });
                
                // Check for new file upload
                const pondFiles = pond ? pond.getFiles() : [];
                Logger.debug('Modal', '📊 FilePond files:', {
                    files: pondFiles.map(f => ({
                        filename: f.filename,
                        origin: f.origin,
                        status: f.status,
                        file: f.file ? {
                            name: f.file.name,
                            type: f.file.type,
                            size: f.file.size
                        } : null
                    }))
                });

                // Handle file upload - process any file in FilePond
                if (pondFiles.length > 0) {
                    const pondFile = pondFiles[0]; // Get first file since we only allow one
                    if (pondFile.file instanceof File) {
                        Logger.debug('Modal', '📁 Processing file:', {
                            filename: pondFile.filename,
                            type: pondFile.file.type,
                            size: pondFile.file.size
                        });
                        formData.append('imageFile', pondFile.file);
                    }
                }
            }

            // Log form data for debugging
            Logger.debug('Modal', '📦 Form data:', {
                entries: Array.from(formData.entries()).map(([key, value]) => {
                    return { key, type: value instanceof File ? 'File' : typeof value };
                })
            });

            this.setLoading(true);
            let response;

            if (this.state.currentData?.id) {
                // Update existing item
                Logger.debug('Modal', '🔄 Updating existing item', { id: this.state.currentData.id });
                const url = `${this.config[currentType].endpoint}/${this.state.currentData.id}`;
                
                if (currentType === 'benefit') {
                    // For benefits, send as JSON
                    const jsonData = {
                        title: formData.get('title'),
                        description: formData.get('description'),
                        displayOrder: parseInt(formData.get('order')),
                        active: true
                    };
                    response = await fetch(url, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(jsonData)
                    });
                } else {
                    // For other types (support-logo, testimonial), send as multipart/form-data
                    response = await fetch(url, {
                        method: 'PUT',
                        body: formData
                    });
                }
            } else {
                // Create new item
                Logger.debug('Modal', '🆕 Creating new item');
                
                if (currentType === 'benefit') {
                    // For benefits, send as JSON
                    const jsonData = {
                        title: formData.get('title'),
                        description: formData.get('description'),
                        displayOrder: parseInt(formData.get('order')),
                        active: true
                    };
                    response = await fetch(this.config[currentType].endpoint, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(jsonData)
                    });
                } else {
                    // For other types (support-logo, testimonial), send as multipart/form-data
                    response = await fetch(this.config[currentType].endpoint, {
                        method: 'POST',
                        body: formData
                    });
                }
            }

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            Logger.info('Modal', '✅ Form submitted successfully:', { data });

            // Show success message
            Swal.fire({
                title: 'Success!',
                text: this.config[currentType].successMessage[this.state.currentData?.id ? 'update' : 'create'],
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000
            });

            // Close modal and refresh
            this.close();
            window.location.reload();

        } catch (error) {
            Logger.error('Modal', '❌ Form submission failed', error);
            Swal.fire({
                title: 'Error!',
                text: error.message || 'Something went wrong. Please try again.',
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000
            });
        } finally {
            this.setLoading(false);
        }
    }
}

// Export Modal for ES modules
export { Modal };

// Also make Modal available globally for inline event handlers
window.Modal = Modal;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => Modal.init());
