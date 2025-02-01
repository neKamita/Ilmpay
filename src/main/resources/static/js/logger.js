// 🎯 Logger Utility - Making debugging a breeze!

const Logger = {
    // Log levels
    LEVELS: {
        DEBUG: '🐛 DEBUG',
        INFO: 'ℹ️ INFO',
        WARN: '⚠️ WARN',
        ERROR: '❌ ERROR'
    },

    // Enable/disable logging
    enabled: true,

    // Enable/disable console groups
    grouping: true,

    // Store logs for potential server upload
    logHistory: [],

    // Maximum number of logs to keep in history
    maxHistorySize: 1000,

    /**
     * 📝 Log a debug message
     * @param {string} component - Component name
     * @param {string} message - Log message
     * @param {any} data - Optional data to log
     */
    debug(component, message, data = null) {
        this._log(this.LEVELS.DEBUG, component, message, data);
    },

    /**
     * ℹ️ Log an info message
     * @param {string} component - Component name
     * @param {string} message - Log message
     * @param {any} data - Optional data to log
     */
    info(component, message, data = null) {
        this._log(this.LEVELS.INFO, component, message, data);
    },

    /**
     * ⚠️ Log a warning message
     * @param {string} component - Component name
     * @param {string} message - Log message
     * @param {any} data - Optional data to log
     */
    warn(component, message, data = null) {
        this._log(this.LEVELS.WARN, component, message, data);
    },

    /**
     * ❌ Log an error message
     * @param {string} component - Component name
     * @param {string} message - Log message
     * @param {Error|any} error - Error object or data
     */
    error(component, message, error = null) {
        this._log(this.LEVELS.ERROR, component, message, error);
        if (error instanceof Error) {
            console.error(error);
        }
    },

    /**
     * 📊 Start a grouped log section
     * @param {string} component - Component name
     * @param {string} label - Group label
     */
    group(component, label) {
        if (!this.enabled || !this.grouping) return;
        console.group(`${component} - ${label}`);
    },

    /**
     * 📊 End a grouped log section
     */
    groupEnd() {
        if (!this.enabled || !this.grouping) return;
        console.groupEnd();
    },

    /**
     * 🔄 Internal logging function
     * @private
     */
    _log(level, component, message, data = null) {
        if (!this.enabled) return;

        const timestamp = new Date().toISOString();
        const logEntry = {
            timestamp,
            level,
            component,
            message,
            data
        };

        // Add to history
        this.logHistory.unshift(logEntry);
        if (this.logHistory.length > this.maxHistorySize) {
            this.logHistory.pop();
        }

        // Format console output
        const prefix = `[${timestamp}] ${level} [${component}]`;
        
        if (data) {
            console.log(prefix, message, data);
        } else {
            console.log(prefix, message);
        }
    },

    /**
     * 💾 Get log history
     * @returns {Array} Array of log entries
     */
    getHistory() {
        return this.logHistory;
    },

    /**
     * 🧹 Clear log history
     */
    clearHistory() {
        this.logHistory = [];
    }
};

// Export for use in other modules
window.Logger = Logger;
