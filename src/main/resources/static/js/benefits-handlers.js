// 🎮 Benefits Event Handlers
// Making benefit management smooth as butter!
import { deleteItem } from './crud.js';

// Make deleteItem available globally for HTML onclick handlers
window.deleteItem = deleteItem;

// Export for module usage
export { deleteItem };
