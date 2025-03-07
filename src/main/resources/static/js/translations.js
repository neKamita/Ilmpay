// 🌍 Translations Management Script
import Logger from './logger.js';

Logger.info('Translations', '🌍 Initializing translations management');

/**
 * Translations Manager
 * Handles the client-side functionality for the translations management page.
 * Optimized for performance with paginated loading and efficient data handling.
 */
class TranslationsManager {
    constructor() {
        // State
        this.allTranslations = [];
        this.filteredTranslations = [];
        this.currentEditingTranslation = null;
        this.supportedLanguages = [];
        this.currentPage = 0;
        this.pageSize = 100;
        this.totalPages = 0;
        this.isLoading = false;
        
        // DOM Elements
        this.searchInput = document.getElementById('searchInput');
        this.languageFilter = document.getElementById('languageFilter');
        this.categoryFilter = document.getElementById('categoryFilter');
        // Removed notTranslatedFilter reference
        this.translationsTableBody = document.getElementById('translationsTableBody');
        this.editTranslationModal = document.getElementById('editTranslationModal');
        this.translationKey = document.getElementById('translationKey');
        this.translationLanguage = document.getElementById('translationLanguage');
        this.translationText = document.getElementById('translationText');
        this.saveTranslationBtn = document.getElementById('saveTranslationBtn');
        this.cancelEditBtn = document.getElementById('cancelEditBtn');
        this.paginationContainer = document.getElementById('paginationContainer');
        
        // Initialize
        this.bindEvents();
        this.loadTranslations();
        
        Logger.info('Translations', '✅ Translations manager initialized');
    }
    
    /**
     * Bind event listeners
     */
    bindEvents() {
        Logger.debug('Translations', '🔗 Binding event listeners');
        
        // Debounce search input to prevent too many requests
        let searchTimeout;
        this.searchInput.addEventListener('input', () => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                this.currentPage = 0;
                this.loadTranslations();
            }, 300);
        });
        
        this.languageFilter.addEventListener('change', () => {
            this.currentPage = 0;
            this.loadTranslations();
        });
        
        this.categoryFilter.addEventListener('change', () => {
            this.currentPage = 0;
            this.loadTranslations();
        });
        
        // Removed notTranslatedFilter event listener
        
        this.saveTranslationBtn.addEventListener('click', () => this.saveTranslation());
        this.cancelEditBtn.addEventListener('click', () => this.closeEditModal());
        
        // Add click event listener to the modal backdrop to close when clicking outside
        const modalBackdrop = this.editTranslationModal.querySelector('.modal-backdrop');
        if (modalBackdrop) {
            modalBackdrop.addEventListener('click', (e) => {
                // Only close if clicking directly on the backdrop, not its children
                if (e.target === modalBackdrop) {
                    this.closeEditModal();
                }
            });
        }
        
        // Make editTranslation available globally
        window.editTranslation = (cell) => this.editTranslation(cell);
        
        // Add pagination navigation
        window.navigateToPage = (page) => this.navigateToPage(page);
    }
    
    /**
     * Navigate to a specific page
     */
    navigateToPage(page) {
        if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
            this.currentPage = page;
            this.loadTranslations();
        }
    }
    
    /**
     * Load translations from the server using the optimized paginated API
     */
    loadTranslations() {
        Logger.info('Translations', '📥 Loading translations');
        
        if (this.isLoading) return;
        this.isLoading = true;
        this.showLoadingState();
        
        // Get supported languages if not already loaded
        if (this.supportedLanguages.length === 0) {
            this.supportedLanguages = Array.from(this.languageFilter.options)
                .filter(option => option.value !== 'all')
                .map(option => option.value);
        }
        
        // Get category filter value
        const category = this.categoryFilter.value;
        const categoryPrefix = category !== 'all' ? category + '.' : '';
        
        // Prepare search parameters
        const searchText = this.searchInput.value.trim();
        const languageCode = this.languageFilter.value;
        // Removed notTranslatedOnly parameter
        
        // Build URL with query parameters
        const url = new URL('/api/admin/translations/paginated', window.location.origin);
        url.searchParams.append('page', this.currentPage);
        url.searchParams.append('size', this.pageSize);
        
        if (languageCode !== 'all') {
            url.searchParams.append('languageCode', languageCode);
        }
        
        if (searchText) {
            url.searchParams.append('searchText', searchText);
        }
        
        // Removed notTranslatedOnly parameter from URL
        
        // Fetch paginated translations
        fetch(url)
            .then(response => response.json())
            .then(data => {
                this.isLoading = false;
                
                if (data.success) {
                    // Update pagination info
                    this.totalPages = data.data.totalPages;
                    this.currentPage = data.data.currentPage;
                    
                    // Transform the data for rendering
                    const translationsByKey = data.data.content;
                    this.allTranslations = Object.keys(translationsByKey).map(key => ({
                        key: key,
                        translations: translationsByKey[key]
                    }));
                    
                    // Filter by category if needed
                    if (category !== 'all') {
                        this.filteredTranslations = this.allTranslations.filter(item => 
                            item.key.startsWith(categoryPrefix)
                        );
                    } else {
                        this.filteredTranslations = this.allTranslations;
                    }
                    
                    Logger.info('Translations', `✅ Loaded ${this.filteredTranslations.length} translations`);
                    this.renderTranslations();
                    this.renderPagination();
                } else {
                    Logger.error('Translations', '❌ Error loading translations', data.message);
                    this.showErrorMessage(data.message || 'Failed to load translations');
                }
            })
            .catch(error => {
                this.isLoading = false;
                Logger.error('Translations', '❌ Error loading translations', error);
                this.showErrorMessage('Failed to load translations');
            });
    }
    
    /**
     * Render pagination controls
     */
    renderPagination() {
        if (!this.paginationContainer) return;
        
        const totalPages = this.totalPages;
        const currentPage = this.currentPage;
        
        if (totalPages <= 1) {
            this.paginationContainer.innerHTML = '';
            return;
        }
        
        let html = '<div class="flex justify-center mt-4 space-x-2">';
        
        // Previous button
        html += `<button 
            onclick="navigateToPage(${currentPage - 1})" 
            class="px-3 py-1 rounded ${currentPage === 0 ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-gray-200 hover:bg-gray-300 text-gray-700'}"
            ${currentPage === 0 ? 'disabled' : ''}>
            <i class="fas fa-chevron-left"></i>
        </button>`;
        
        // Page numbers
        const maxVisiblePages = 5;
        let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);
        
        // Adjust start page if we're near the end
        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(0, endPage - maxVisiblePages + 1);
        }
        
        // First page
        if (startPage > 0) {
            html += `<button onclick="navigateToPage(0)" class="px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 text-gray-700">1</button>`;
            if (startPage > 1) {
                html += `<span class="px-3 py-1">...</span>`;
            }
        }
        
        // Page numbers
        for (let i = startPage; i <= endPage; i++) {
            html += `<button 
                onclick="navigateToPage(${i})" 
                class="px-3 py-1 rounded ${i === currentPage ? 'bg-indigo-600 text-white' : 'bg-gray-200 hover:bg-gray-300 text-gray-700'}">
                ${i + 1}
            </button>`;
        }
        
        // Last page
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                html += `<span class="px-3 py-1">...</span>`;
            }
            html += `<button onclick="navigateToPage(${totalPages - 1})" class="px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 text-gray-700">${totalPages}</button>`;
        }
        
        // Next button
        html += `<button 
            onclick="navigateToPage(${currentPage + 1})" 
            class="px-3 py-1 rounded ${currentPage === totalPages - 1 ? 'bg-gray-200 text-gray-500 cursor-not-allowed' : 'bg-gray-200 hover:bg-gray-300 text-gray-700'}"
            ${currentPage === totalPages - 1 ? 'disabled' : ''}>
            <i class="fas fa-chevron-right"></i>
        </button>`;
        
        html += '</div>';
        
        this.paginationContainer.innerHTML = html;
    }
    
    /**
     * Show loading state
     */
    showLoadingState() {
        this.translationsTableBody.innerHTML = `
            <tr>
                <td colspan="${this.supportedLanguages.length + 1}" class="p-8 text-center text-gray-500">
                    <div class="flex flex-col items-center">
                        <i class="fas fa-spinner fa-spin text-2xl mb-2"></i>
                        <p>Loading translations...</p>
                    </div>
                </td>
            </tr>
        `;
    }
    
    /**
     * Render translations table
     */
    renderTranslations() {
        Logger.debug('Translations', '🎨 Rendering translations table');
        
        if (this.filteredTranslations.length === 0) {
            this.translationsTableBody.innerHTML = `
                <tr>
                    <td colspan="${this.supportedLanguages.length + 1}" class="p-8 text-center text-gray-500">
                        <div class="flex flex-col items-center">
                            <i class="fas fa-search text-2xl mb-2"></i>
                            <p>No translations found matching your filters</p>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }
        
        const searchText = this.searchInput.value.toLowerCase();
        
        let html = '';
        
        this.filteredTranslations.forEach(item => {
            const key = item.key;
            
            html += `<tr class="border-t border-gray-200">
                <td class="p-4 align-top">
                    <div class="translation-key">${this.highlightText(key, searchText)}</div>
                </td>`;
            
            this.supportedLanguages.forEach(lang => {
                const translation = item.translations[lang];
                const text = translation ? translation.translatedText : '';
                
                html += `
                    <td class="p-4 align-top translation-cell editable-cell" 
                        data-key="${key}" 
                        data-lang="${lang}" 
                        data-id="${translation ? translation.id : ''}"
                        onclick="editTranslation(this)">
                        <div class="whitespace-pre-wrap">${text ? this.highlightText(text, searchText) : '<span class="empty-translation">Not translated</span>'}</div>
                    </td>
                `;
            });
            
            html += '</tr>';
        });
        
        this.translationsTableBody.innerHTML = html;
    }
    
    /**
     * Highlight search text
     */
    highlightText(text, searchText) {
        if (!searchText || searchText === '') {
            return text;
        }
        
        const regex = new RegExp(`(${this.escapeRegExp(searchText)})`, 'gi');
        return text.replace(regex, '<span class="search-highlight">$1</span>');
    }
    
    /**
     * Escape special characters for regex
     */
    escapeRegExp(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }
    
    /**
     * Edit translation
     */
    editTranslation(cell) {
        const key = cell.dataset.key;
        const lang = cell.dataset.lang;
        const id = cell.dataset.id;
        const text = cell.querySelector('div').textContent.trim();
        
        Logger.info('Translations', `✏️ Editing translation - Key: ${key}, Language: ${lang}`);
        
        this.currentEditingTranslation = {
            id: id || null,
            key: key,
            languageCode: lang,
            translatedText: text === 'Not translated' ? '' : text
        };
        
        this.translationKey.value = key;
        this.translationLanguage.value = lang;
        this.translationText.value = text === 'Not translated' ? '' : text;
        
        this.editTranslationModal.classList.remove('hidden');
    }
    
    /**
     * Save translation
     * Displays a loading spinner while saving the translation to the server
     */
    saveTranslation() {
        if (!this.currentEditingTranslation) return;
        
        Logger.info('Translations', `💾 Saving translation - Key: ${this.currentEditingTranslation.key}, Language: ${this.currentEditingTranslation.languageCode}`);
        
        // Show loading spinner and disable buttons
        const saveBtn = this.saveTranslationBtn;
        const cancelBtn = this.cancelEditBtn;
        const originalSaveBtnText = saveBtn.innerHTML;
        
        // Add spinner with inline styles to ensure visibility
        saveBtn.innerHTML = '<div class="inline-block w-4 h-4 mr-2 border-2 border-white border-t-transparent rounded-full animate-spin"></div> Saving...';
        saveBtn.disabled = true;
        saveBtn.classList.add('opacity-75', 'cursor-not-allowed');
        cancelBtn.disabled = true;
        cancelBtn.classList.add('opacity-75', 'cursor-not-allowed');
        
        this.currentEditingTranslation.translatedText = this.translationText.value;
        
        fetch('/api/admin/translations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.currentEditingTranslation)
        })
        .then(response => response.json())
        .then(data => {
            // Reset button state
            saveBtn.innerHTML = originalSaveBtnText;
            saveBtn.disabled = false;
            saveBtn.classList.remove('opacity-75', 'cursor-not-allowed');
            cancelBtn.disabled = false;
            cancelBtn.classList.remove('opacity-75', 'cursor-not-allowed');
            
            if (data.success) {
                Logger.info('Translations', `✅ Translation saved successfully - ID: ${data.data.id}`);
                
                // Update the translation in the state
                const item = this.allTranslations.find(item => item.key === this.currentEditingTranslation.key);
                if (item) {
                    item.translations[this.currentEditingTranslation.languageCode] = data.data;
                }
                
                // Close modal and re-render
                this.closeEditModal();
                this.renderTranslations();
                
                // Show success message
                this.showSuccessMessage('Translation updated successfully');
            } else {
                Logger.error('Translations', '❌ Error saving translation', data.message);
                this.showErrorMessage(data.message || 'Failed to save translation');
            }
        })
        .catch(error => {
            // Reset button state
            saveBtn.innerHTML = originalSaveBtnText;
            saveBtn.disabled = false;
            saveBtn.classList.remove('opacity-75', 'cursor-not-allowed');
            cancelBtn.disabled = false;
            cancelBtn.classList.remove('opacity-75', 'cursor-not-allowed');
            
            Logger.error('Translations', '❌ Error saving translation', error);
            this.showErrorMessage('Failed to save translation');
        });
    }
    
    /**
     * Close edit modal
     */
    closeEditModal() {
        Logger.debug('Translations', '🔒 Closing edit modal');
        this.editTranslationModal.classList.add('hidden');
        
        // Reset any active elements to ensure focus is properly released
        if (document.activeElement) {
            document.activeElement.blur();
        }
        
        // Small delay to ensure transitions complete properly
        setTimeout(() => {
            // Additional cleanup if needed
            this.currentEditingTranslation = null;
        }, 50);
    }
    

    /**
     * Show success message
     */
    showSuccessMessage(message) {
        Swal.fire({
            title: 'Success!',
            text: message,
            icon: 'success',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
        });
    }
    
    /**
     * Show error message
     */
    showErrorMessage(message) {
        Swal.fire({
            title: 'Error!',
            text: message,
            icon: 'error',
            confirmButtonText: 'OK'
        });
    }
}

// Initialize when the DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new TranslationsManager();
});

export default TranslationsManager;
