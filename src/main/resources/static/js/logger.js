// Logger Module - Making debugging a breeze!

// Logger class definition
class Logger {
    static LEVELS = {
        DEBUG: '🐛 DEBUG',
        INFO: 'ℹ️ INFO',
        WARN: '⚠️ WARN',
        ERROR: '❌ ERROR'
    };

    // Enable/disable logging
    static enabled = true;

    // Enable/disable console groups
    static grouping = true;

    // Store logs for potential server upload
    static logHistory = [];

    // Maximum number of logs to keep in history
    static maxHistorySize = 1000;

    static debug(component, message, data = null) {
        this._log(this.LEVELS.DEBUG, component, message, data);
    }

    static info(component, message, data = null) {
        this._log(this.LEVELS.INFO, component, message, data);
    }

    static warn(component, message, data = null) {
        this._log(this.LEVELS.WARN, component, message, data);
    }

    static error(component, message, error = null) {
        this._log(this.LEVELS.ERROR, component, message, error);
        if (error && error instanceof Error) {
            console.error(error);
        }
    }

    static group(component, label) {
        if (!this.enabled || !this.grouping) return;
        console.group(`${component} - ${label}`);
    }

    static groupEnd() {
        if (!this.enabled || !this.grouping) return;
        console.groupEnd();
    }

    static _log(level, component, message, data = null) {
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
        this.logHistory.push(logEntry);
        if (this.logHistory.length > this.maxHistorySize) {
            this.logHistory.shift();
        }

        // Format console output
        const prefix = `[${timestamp}] ${level} ${component}:`;
        console.log(prefix, message);
        if (data) {
            console.log(data);
        }
    }

    static getHistory() {
        return [...this.logHistory];
    }

    static clearHistory() {
        this.logHistory = [];
    }
}

// Create a singleton instance
const logger = new Logger();

// Support both default and named exports
export { Logger };
export default Logger;
