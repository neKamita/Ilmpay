// 🎯 Custom field configurations
const fieldConfigs = {
    // 🖼️ Support Logo Configuration
    'support-logo': {
        name: {
            type: 'text',
            label: 'Logo Name',
            placeholder: 'Enter logo name',
            required: true,
            help: '👉 Enter a descriptive name for the support logo'
        },
        imageFile: {
            type: 'file',
            label: 'Logo Image File',
            help: '📸 Upload a logo image (PNG, JPG, SVG). If both file and URL are provided, the file will take precedence.',
            required: false,
            accept: 'image/png,image/jpeg,image/jpg,image/svg+xml',
            validate: (value, formData) => {
                const hasUrl = formData.get('imageUrl')?.trim();
                if (!value && !hasUrl) {
                    return 'Please provide either an image file or URL';
                }
                return true;
            }
        },
        imageUrl: {
            type: 'url',
            label: 'Image URL',
            placeholder: 'https://',
            required: false,
            help: '🔗 Or provide a URL to an existing image. Note: If a file is uploaded, it will take precedence over the URL.',
            validate: (value, formData) => {
                const hasFile = formData.get('imageFile');
                if (!value?.trim() && !hasFile) {
                    return 'Please provide either an image file or URL';
                }
                if (value?.trim() && !value.match(/^https?:\/\/.+/)) {
                    return 'Please enter a valid URL starting with http:// or https://';
                }
                return true;
            }
        },
        websiteUrl: {
            type: 'url',
            label: 'Website URL',
            placeholder: 'https://',
            required: true,
            help: '🌐 Enter the website URL associated with this logo',
            validate: (value) => {
                if (!value?.match(/^https?:\/\/.+/)) {
                    return 'Please enter a valid URL starting with http:// or https://';
                }
                return true;
            }
        },
        order: {
            type: 'number',
            label: 'Display Order',
            required: false,
            min: 0,
            step: 1,
            help: 'ℹ️ Set the display order (lower numbers appear first)'
        }
    }
    // Add more configurations for other entity types here
};
