// 🎭 Modal Management System - Where UI magic happens!
const Modal = {
    // 🎨 Current state (like a magician's hat, holding our secrets)
    state: {
        currentType: null,
        currentData: null
    },

    // 🎯 Modal Elements (our magical props for the show)
    elements: {
        modal: null,
        backdrop: null,
        panel: null,
        form: null,
        title: null,
        formFields: null,
        closeButtons: null
    },

    // 🎨 Configuration for different modal types (our spellbook)
    config: {
        'support-logo': {
            createTitle: 'Add New Support Logo',
            editTitle: 'Edit Support Logo',
            fields: [
                {
                    name: 'name',
                    label: 'Logo Name',
                    type: 'text',
                    required: true
                },
                {
                    name: 'imageUrl',
                    label: 'Image URL',
                    type: 'text',
                    required: true
                },
                {
                    name: 'order',
                    label: 'Display Order',
                    type: 'number',
                    required: true,
                    min: 1
                }
            ]
        }
        // Add other modal configurations here as needed
    },

    // 🎯 Initialize the modal system (setting up our stage)
    init() {
        // Cache DOM elements (finding all our props)
        this.elements.modal = document.getElementById('crudModal');
        if (!this.elements.modal) {
            console.error('🎭 Modal element not found! Make sure crud-modal fragment is included.');
            return;
        }

        this.elements.backdrop = this.elements.modal.querySelector('.modal-backdrop');
        this.elements.panel = this.elements.modal.querySelector('.modal-content');
        this.elements.form = this.elements.modal.querySelector('#crudForm');
        this.elements.title = this.elements.modal.querySelector('#modalTitle');
        this.elements.formFields = this.elements.modal.querySelector('.form-fields');
        this.elements.closeButtons = this.elements.modal.querySelectorAll('.modal-close');

        // Set up event listeners (preparing our interactive elements)
        this.setupEventListeners();
    },

    // 🎭 Set up all event listeners (choreographing our show)
    setupEventListeners() {
        // Close on backdrop click (the classic disappearing act)
        this.elements.modal?.addEventListener('click', (e) => {
            if (e.target === this.elements.modal) {
                this.close();
            }
        });

        // Close on button click (the proper way to exit)
        this.elements.closeButtons?.forEach(btn => {
            btn.addEventListener('click', () => this.close());
        });

        // Close on Escape key (the emergency exit)
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && !this.elements.modal?.classList.contains('hidden')) {
                this.close();
            }
        });

        // Form submission (the grand finale)
        this.elements.form?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSubmit(e);
        });
    },

    // 🎬 Show the modal (raising the curtain)
    show(type, data = null) {
        if (!this.elements.modal) {
            console.error('🎭 Cannot show modal: Modal elements not initialized!');
            return;
        }

        this.state.currentType = type;
        this.state.currentData = data;

        // Show modal (lights up!)
        this.elements.modal.classList.remove('hidden');
        
        // Animate backdrop (setting the atmosphere)
        requestAnimationFrame(() => {
            this.elements.backdrop?.classList.add('show');
            this.elements.panel?.classList.add('show');
        });
        
        // Update content (preparing the stage)
        this.updateContent();
    },

    // ❌ Close the modal (the final bow)
    close() {
        if (!this.elements.modal) return;

        // Fade out elements (dimming the lights)
        this.elements.backdrop?.classList.remove('show');
        this.elements.panel?.classList.remove('show');

        // Wait for animations to finish
        setTimeout(() => {
            this.elements.modal.classList.add('hidden');
            this.resetState();
        }, 300); // Match this with our CSS transition duration
    },

    // 🎨 Update modal content (setting the scene)
    updateContent() {
        const config = this.config[this.state.currentType];
        if (!config) {
            console.error(`🎭 No configuration found for type: ${this.state.currentType}`);
            return;
        }

        // Update title (announcing the act)
        if (this.elements.title) {
            this.elements.title.textContent = this.state.currentData ? config.editTitle : config.createTitle;
        }
        
        // Clear previous fields (clean slate)
        if (this.elements.formFields) {
            this.elements.formFields.innerHTML = '';
            
            // Create form fields with animations (setting up props)
            config.fields.forEach((field, index) => {
                const fieldDiv = document.createElement('div');
                fieldDiv.className = `form-field-enter form-field-enter-${index + 1}`;
                fieldDiv.innerHTML = this.generateFieldHTML(field, this.state.currentData);
                this.elements.formFields.appendChild(fieldDiv);
            });
        }

        // Initialize icons (adding the finishing touches)
        lucide.createIcons();
    },

    // 📝 Generate HTML for a form field (crafting each prop)
    generateFieldHTML(field, data) {
        const value = data ? data[field.name] || '' : '';
        return `
            <label for="${field.name}" class="form-label">${field.label}</label>
            <input type="${field.type}" 
                   id="${field.name}" 
                   name="${field.name}" 
                   value="${value}"
                   class="form-input"
                   ${field.required ? 'required' : ''}
                   ${field.min ? `min="${field.min}"` : ''}>
        `;
    },

    // 🔄 Reset modal state (cleaning up after the show)
    resetState() {
        this.state.currentType = null;
        this.state.currentData = null;
        if (this.elements.form) {
            this.elements.form.reset();
        }
    },

    // 📨 Handle form submission (the final act)
    async handleSubmit(e) {
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());
        
        try {
            // Show loading state (the anticipation builds!)
            Swal.fire({
                title: 'Saving...',
                text: 'Just a moment while we save your changes!',
                icon: 'info',
                allowOutsideClick: false,
                showConfirmButton: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            const response = await fetch(`/api/admin/${this.state.currentType}s`, {
                method: this.state.currentData ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Failed to save');

            // Show success message (applause! 👏)
            Swal.fire({
                icon: 'success',
                title: 'Success!',
                text: `${this.state.currentType} has been ${this.state.currentData ? 'updated' : 'created'} successfully.`,
                timer: 2000,
                showConfirmButton: false
            });

            this.close();
            // Refresh the page to show updated data
            setTimeout(() => window.location.reload(), 2000);

        } catch (error) {
            console.error('🎭 Modal submission error:', error);
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: error.message || 'Something went wrong!'
            });
        }
    }
};

// 🎬 Initialize on page load (the pre-show setup)
document.addEventListener('DOMContentLoaded', () => Modal.init());
