// 🎨 Support Logos Event Handlers
// Making logo management a work of art!
import { deleteItem } from './crud.js';

// Make deleteItem available globally for HTML onclick handlers
window.deleteItem = deleteItem;

// Export for module usage
export { deleteItem };
