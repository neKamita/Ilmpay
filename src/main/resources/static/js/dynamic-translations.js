// 🌍 Dynamic Translations Manager
// Handles client-side dynamic loading and updating of translations
// without requiring a full page reload when changing languages

class DynamicTranslationsManager {
    constructor() {
        // Cache for translations
        this.translations = {};
        this.currentLanguage = document.documentElement.lang || 'en';
        
        // Initialize
        this.bindEvents();
        this.loadTranslations(this.currentLanguage);
        
        console.log('🌍 Dynamic Translations Manager initialized');
    }
    
    /**
     * Bind event listeners for language switching
     */
    bindEvents() {
        console.log('🔗 Binding language switcher events');
        
        // Intercept language switcher clicks
        document.querySelectorAll('.dropdown-item[href*="lang="]').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const lang = link.href.split('lang=')[1];
                console.log(`🔄 Language switch requested: ${lang}`);
                this.changeLanguage(lang);
            });
        });
    }
    
    /**
     * Load translations for a specific language
     */
    async loadTranslations(languageCode) {
        console.log(`📥 Loading translations for language: ${languageCode}`);
        
        // If already cached, use cache
        if (this.translations[languageCode]) {
            console.log(`✅ Using cached translations for ${languageCode}`);
            this.updatePageContent(languageCode);
            return;
        }
        
        try {
            const response = await fetch(`/api/admin/translations/public/${languageCode}`);
            const data = await response.json();
            
            if (data.success) {
                console.log(`✅ Loaded ${Object.keys(data.data).length} translations for ${languageCode}`);
                this.translations[languageCode] = data.data;
                this.updatePageContent(languageCode);
            } else {
                console.error('❌ Failed to load translations:', data.message);
            }
        } catch (error) {
            console.error('❌ Error loading translations:', error);
        }
    }
    
    /**
     * Change the current language
     */
    changeLanguage(languageCode) {
        if (languageCode === this.currentLanguage) return;
        
        console.log(`🔄 Changing language from ${this.currentLanguage} to ${languageCode}`);
        
        // Update URL parameter without reloading
        const url = new URL(window.location);
        const currentHash = url.hash; // Save the current hash
        url.searchParams.set('lang', languageCode);
        
        // Preserve the hash if it exists
        if (currentHash) {
            // We need to manually append the hash because URL.hash is read-only
            window.history.pushState({}, '', url.toString() + currentHash);
        } else {
            window.history.pushState({}, '', url);
        }
        
        // Update language attribute
        document.documentElement.lang = languageCode;
        
        // Update current language
        this.currentLanguage = languageCode;
        
        // Load translations and update content
        this.loadTranslations(languageCode);
    }
    
    /**
     * Update page content with new translations
     */
    updatePageContent(languageCode) {
        const translations = this.translations[languageCode];
        if (!translations) return;
        
        console.log(`🎨 Updating page content with ${languageCode} translations`);
        
        // Update all elements with data-translation-key attribute
        document.querySelectorAll('[data-translation-key]').forEach(element => {
            const key = element.dataset.translationKey;
            if (translations[key]) {
                element.textContent = translations[key];
                console.log(`✅ Updated translation for key: ${key}`);
            }
        });
        
        // Update page title if available
        if (translations['app.title']) {
            document.title = translations['app.title'];
        }
    }
}

// Initialize when the DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.translationsManager = new DynamicTranslationsManager();
});
