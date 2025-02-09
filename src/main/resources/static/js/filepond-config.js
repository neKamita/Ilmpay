// 🎨 FilePond Configuration - Making file uploads as smooth as a duck on a pond! 🦆
import Logger from './logger.js';

document.addEventListener('DOMContentLoaded', function() {
    Logger.info('FilePond', '🎣 Setting up the file fishing pond');

    // Register plugins - The dream team of file upload! 🌟
    FilePond.registerPlugin(
        FilePondPluginImagePreview,
        FilePondPluginImageEdit,
        FilePondPluginFileValidateType
    );
    Logger.debug('FilePond', '🔌 Plugins registered and ready to rock!');

    // 🎨 Set default FilePond options
    FilePond.setOptions({
        // 🖼️ Visual settings - Making it look sharp!
        labelIdle: '🖼️ Drag & Drop your logo or <span class="filepond--label-action">Browse</span>',
        imagePreviewHeight: 170,
        acceptedFileTypes: ['image/*'],
        stylePanelLayout: 'integrated',
        styleLoadIndicatorPosition: 'center bottom',
        styleProgressIndicatorPosition: 'right bottom',
        styleButtonRemoveItemPosition: 'right top',
        styleButtonProcessItemPosition: 'right bottom',
        imageCropAspectRatio: '1:1',    // Square aspect ratio for logos
        imageResizeTargetWidth: 200,     // Reasonable size for logos
        imageResizeTargetHeight: 200,    // Keeping it square
        allowMultiple: false,            // Only one logo at a time
        allowRevert: true,               // Allow removing the file
        instantUpload: false,            // Don't upload immediately
        allowProcess: true,              // Process files before submitting
        
        // 🎯 File processing configuration
        beforeAddFile: (item) => {
            Logger.debug('FilePond', '🔍 Checking file before add:', {
                hasFile: !!item.file,
                fileType: item.file ? item.file.constructor.name : null,
                fileSize: item.file ? item.file.size : null
            });
            
            // Ensure we're getting a proper file
            if (!item.file || !(item.file instanceof File)) {
                Logger.error('FilePond', '❌ Invalid file object:', item);
                return false;
            }
            return true;
        },

        // 🎨 File processing settings
        imageTransformOutputQuality: 90, // Good quality, reasonable size
        imageTransformOutputMimeType: 'image/jpeg',
        imageResizeMode: 'cover',
        imageResizeUpscale: false,       // Don't make small images bigger
        
        // 🎯 File processing configuration
        fileRenameFunction: (file) => {
            // Generate a unique name for the file
            const extension = file.extension || '';
            const timestamp = new Date().getTime();
            return `logo-${timestamp}.${extension}`;
        },

        // 🗣️ Speaking the user's language - Fun and friendly messages!
        labelFileProcessing: 'Preparing your logo... 🎨',
        labelFileProcessingComplete: 'Logo ready! 🎉',
        labelFileProcessingAborted: 'Logo upload cancelled 😅',
        labelFileProcessingError: 'Oops! Something went wrong with the logo 🙈',
        labelTapToCancel: 'tap to cancel 🛑',
        labelTapToRetry: 'tap to try again 🔄',
        labelTapToUndo: 'tap to remove ↩️',

        // 🎭 Event callbacks - The drama unfolds!
        onaddfile: (error, file) => {
            if (error) {
                Logger.error('FilePond', '❌ Failed to add file:', error);
                Swal.fire({
                    title: 'Oops!',
                    text: 'Failed to add the file. Please try again.',
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
                return;
            }
            Logger.info('FilePond', '🎬 New actor joined the stage!', { 
                filename: file.filename,
                fileSize: file.fileSize,
                hasFile: !!file.file,
                fileType: file.file ? file.file.constructor.name : null
            });
        },

        // 🎭 File processing events
        onpreparefile: (file, output) => {
            Logger.info('FilePond', '🎨 Preparing your masterpiece...', { 
                filename: file.filename,
                outputType: output ? output.constructor.name : null,
                outputSize: output ? output.size : null
            });
            
            // Add a processing class to the file item
            file.element.classList.add('is-processing');
            
            // Ensure the file object is properly set
            if (output && output instanceof Blob) {
                file.file = new File([output], file.filename, { 
                    type: output.type || 'image/jpeg'
                });
                Logger.debug('FilePond', '✨ File object created:', {
                    name: file.file.name,
                    type: file.file.type,
                    size: file.file.size
                });
            }
        },

        onprocessfile: (error, file) => {
            if (error) {
                Logger.error('FilePond', '❌ Failed to process file:', error);
                file.element.classList.add('is-error');
                Swal.fire({
                    title: 'Processing Failed',
                    text: 'Could not process the image. Please try again.',
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000
                });
                return;
            }

            // Ensure we have a valid file object
            if (!file.file || !(file.file instanceof File)) {
                Logger.error('FilePond', '❌ Invalid file object after processing:', {
                    hasFile: !!file.file,
                    fileType: file.file ? file.file.constructor.name : null
                });
                file.element.classList.add('is-error');
                return;
            }

            // Add success styling
            file.element.classList.remove('is-processing');
            file.element.classList.add('is-success');

            // Show success toast
            Swal.fire({
                title: 'Success!',
                text: 'Image processed successfully! 🎉',
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 2000
            });

            Logger.info('FilePond', '✨ File processed successfully:', { 
                filename: file.filename,
                fileSize: file.fileSize,
                hasFile: !!file.file,
                fileType: file.file ? file.file.constructor.name : null
            });
        },

        onprocessfilerevert: (file) => {
            Logger.info('FilePond', '↩️ Reverting file changes:', { filename: file.filename });
            // Remove any status classes
            file.element.classList.remove('is-success', 'is-error', 'is-processing');
        },

        // 🎭 File removal
        onremovefile: (error, file) => {
            if (error) {
                Logger.error('FilePond', '❌ Failed to remove file:', error);
                return;
            }
            Logger.info('FilePond', '👋 File removed:', { filename: file.filename });
            // Clear any associated URL input when file is removed
            const form = file.element.closest('form');
            if (form) {
                const urlInput = form.querySelector('[name="imageUrl"]');
                if (urlInput) {
                    urlInput.value = '';
                }
            }
        }
    });

    Logger.info('FilePond', '✨ FilePond is ready for its performance!');
});
