// 🎨 Field Templates Module - Beautiful form fields for every need!
import Logger from './logger.js';

Logger.info('FieldTemplates', '🎨 Loading field templates');

export const fieldTemplates = {
    // 📝 Text field template
    text: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label}</label>
            <input type="text" 
                   name="${field.name}"
                   value="${currentData?.[field.name] || ''}"
                   class="form-input"
                   placeholder="${field.placeholder || ''}"
                   ${field.required ? 'required' : ''}>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
            ${currentData ? `<p class="text-xs text-blue-600 mt-1">Current value: ${currentData[field.name] || 'Not set'}</p>` : ''}
        </div>
    `,
    
    // 📝 Textarea field template
    textarea: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label}</label>
            <textarea name="${field.name}"
                      class="form-textarea"
                      placeholder="${field.placeholder || ''}"
                      rows="${field.rows || 4}"
                      ${field.required ? 'required' : ''}>${currentData?.[field.name] || ''}</textarea>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
            ${currentData ? `<p class="text-xs text-blue-600 mt-1">Current value: ${currentData[field.name] || 'Not set'}</p>` : ''}
        </div>
    `,
    
    // 🔗 URL field template
    url: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label}</label>
            <input type="url" 
                   name="${field.name}"
                   value="${currentData?.[field.name] || ''}"
                   class="form-input"
                   placeholder="${field.placeholder || ''}"
                   ${field.required ? 'required' : ''}>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
            ${currentData ? `<p class="text-xs text-blue-600 mt-1">Current value: ${currentData[field.name] || 'Not set'}</p>` : ''}
        </div>
    `,
    
    // 📸 File upload field template
    file: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label || 'File'}</label>
            ${currentData?.imageUrl ? `
                <div class="mb-3 p-3 bg-gray-50 rounded-lg">
                    <p class="text-sm text-gray-600 mb-2">Current Image:</p>
                    <div class="flex items-center space-x-3">
                        <img src="${currentData.imageUrl}" alt="Current logo" class="w-16 h-16 object-contain rounded border">
                        <div class="text-sm">
                            <p class="text-gray-600">Name: ${currentData.name}</p>
                            <p class="text-gray-600">URL: ${currentData.imageUrl}</p>
                        </div>
                    </div>
                </div>
            ` : ''}
            <input type="file" 
                   name="${field.name}" 
                   ${field.accept ? `accept="${field.accept}"` : ''}
                   class="filepond"
                   ${field.required ? 'required' : ''}>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
        </div>
    `,
    
    // 📸 Image upload field template
    imageFile: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label || 'Image'}</label>
            ${currentData?.imageUrl ? `
                <div class="mb-3 p-3 bg-gray-50 rounded-lg">
                    <p class="text-sm text-gray-600 mb-2">Current Image:</p>
                    <div class="flex items-center space-x-3">
                        <img src="${currentData.imageUrl}" alt="Current logo" class="w-16 h-16 object-contain rounded border">
                        <div class="text-sm">
                            <p class="text-gray-600">Name: ${currentData.name}</p>
                            <p class="text-gray-600">URL: ${currentData.imageUrl}</p>
                        </div>
                    </div>
                </div>
            ` : ''}
            <input type="file" 
                   name="${field.name}" 
                   accept="image/*"
                   class="filepond"
                   ${field.required ? 'required' : ''}>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
        </div>
    `,
    
    // 🔢 Number field template
    number: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label}</label>
            <input type="number" 
                   name="${field.name}"
                   value="${currentData?.[field.name] || ''}"
                   class="form-input"
                   placeholder="${field.placeholder || ''}"
                   ${field.min ? `min="${field.min}"` : ''}
                   ${field.max ? `max="${field.max}"` : ''}
                   ${field.step ? `step="${field.step}"` : ''}
                   ${field.required ? 'required' : ''}>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
            ${currentData ? `<p class="text-xs text-blue-600 mt-1">Current value: ${currentData[field.name] || 'Not set'}</p>` : ''}
        </div>
    `,
    
    // 🖼️ URL field with preview
    imageUrl: (field, value, currentData) => `
        <div class="form-group">
            <label class="form-label">${field.label || 'Image URL'}</label>
            ${currentData?.imageUrl ? `
                <div class="mb-3 p-3 bg-gray-50 rounded-lg">
                    <p class="text-sm text-gray-600 mb-2">Current Image:</p>
                    <div class="flex items-center space-x-3">
                        <img src="${currentData.imageUrl}" alt="Current logo" class="w-16 h-16 object-contain rounded border">
                        <div class="text-sm">
                            <p class="text-gray-600">Name: ${currentData.name}</p>
                            <p class="text-gray-600">URL: ${currentData.imageUrl}</p>
                        </div>
                    </div>
                </div>
            ` : ''}
            <div class="flex space-x-3">
                <input type="url" 
                       name="${field.name}"
                       value="${currentData?.[field.name] || ''}"
                       class="form-input flex-grow"
                       placeholder="${field.placeholder || ''}"
                       ${field.required ? 'required' : ''}>
                ${value ? `
                    <div class="w-12 h-12 rounded-lg border border-gray-200 overflow-hidden">
                        <img src="${value}" alt="Preview" class="w-full h-full object-cover">
                    </div>
                ` : ''}
            </div>
            ${field.help ? `<p class="text-sm text-gray-500 mt-1">${field.help}</p>` : ''}
        </div>
    `
};
