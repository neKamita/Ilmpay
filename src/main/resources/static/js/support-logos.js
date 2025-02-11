// 🎨 Support Logos Core Module
// This module initializes and manages the support logos functionality

import { Logger } from './logger.js';

// Initialize the page
document.addEventListener('DOMContentLoaded', () => {
    Logger.info('SupportLogos', '🎨 Support Logos page initialized');

    // Initialize Lucide icons
    if (typeof lucide !== 'undefined') {
        lucide.createIcons();
    }
});
