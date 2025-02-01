// 🎭 Admin Panel Functionality - Where the magic happens! ✨

// 🔄 Sidebar Toggle - The hide and seek champion
function toggleSubmenu(menuId) {
    Logger.debug('Admin', '🎪 Playing peek-a-boo with submenu', { menuId });
    const menu = document.getElementById(menuId);
    const icon = document.getElementById(menuId + '-icon');
    menu.classList.toggle('hidden');
    icon.classList.toggle('rotate-90');
}

// 🎯 Admin API Functions - The data whisperer
const AdminAPI = {
    // 📝 CRUD Operations - Create, Read, Update, Delete (and sometimes Delete fails and hides 😅)
    async fetchItem(type, id) {
        Logger.debug('AdminAPI', '🎣 Fishing for data', { type, id });
        try {
            const response = await fetch(`/api/admin/${type}s/${id}`);
            const data = await response.json();
            if (!data.success) throw new Error(data.message);
            Logger.info('AdminAPI', '🎯 Found the treasure!', data.data);
            return data.data;
        } catch (error) {
            Logger.error('AdminAPI', '🎣 The big one got away', error);
            throw error;
        }
    },

    async deleteItem(type, id) {
        Logger.debug('AdminAPI', '🗑️ Taking out the digital trash', { type, id });
        try {
            const response = await fetch(`/api/admin/${type}s/${id}`, {
                method: 'DELETE'
            });
            const data = await response.json();
            if (!data.success) throw new Error(data.message);
            Logger.info('AdminAPI', '✨ Poof! It\'s gone', { type, id });
            return data;
        } catch (error) {
            Logger.error('AdminAPI', '😅 This item is playing hard to get', error);
            throw error;
        }
    }
};

// 🎨 Admin UI Functions - Making things pretty since 2025
const AdminUI = {
    // 🎯 Show loading state - The "please wait while I do my thing" dance
    showLoading(message = 'Loading...') {
        Logger.debug('AdminUI', '💃 Starting the loading dance', { message });
        return Swal.fire({
            title: message,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
    },

    // ✨ Show success message - Time to celebrate!
    async showSuccess(message) {
        Logger.info('AdminUI', '🎉 Time to celebrate!', { message });
        await Swal.fire({
            title: 'Success!',
            text: message,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
        });
    },

    // ❌ Show error message - Oops, we goofed!
    async showError(error) {
        Logger.error('AdminUI', '🙈 Oopsie! Something went wrong', error);
        await Swal.fire({
            title: 'Oops! 😅',
            text: error.message || 'Something went wrong',
            icon: 'error'
        });
    },

    // 🎯 Show delete confirmation - The "are you really, really sure?" check
    async confirmDelete() {
        Logger.debug('AdminUI', '🤔 User is contemplating deletion');
        const result = await Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'No, keep it!'
        });
        Logger.debug('AdminUI', '🎲 Delete decision made', { confirmed: result.isConfirmed });
        return result.isConfirmed;
    }
};

// 🎯 Admin Event Handlers - Where clicks become actions
const AdminHandlers = {
    // 📝 Edit item - Time to give it a makeover!
    async handleEdit(type, id) {
        Logger.group('AdminHandlers', '✏️ Starting edit operation');
        try {
            AdminUI.showLoading('Fetching data...');
            const item = await AdminAPI.fetchItem(type, id);
            Modal.show(type, item);
            Logger.info('AdminHandlers', '💅 Item ready for its makeover', { type, id });
        } catch (error) {
            Logger.error('AdminHandlers', '😅 Makeover failed', error);
            AdminUI.showError(error);
        } finally {
            Logger.groupEnd();
        }
    },

    // ❌ Delete item - The Marie Kondo method
    async handleDelete(type, id) {
        Logger.group('AdminHandlers', '🗑️ Starting delete operation');
        try {
            if (!await AdminUI.confirmDelete()) {
                Logger.info('AdminHandlers', '💝 Item sparks joy, keeping it!', { type, id });
                return;
            }

            AdminUI.showLoading('Deleting...');
            await AdminAPI.deleteItem(type, id);
            await AdminUI.showSuccess('Item deleted successfully');
            window.location.reload();
            Logger.info('AdminHandlers', '🧹 Cleanup complete!', { type, id });
        } catch (error) {
            Logger.error('AdminHandlers', '🙈 Cleanup failed', error);
            AdminUI.showError(error);
        } finally {
            Logger.groupEnd();
        }
    }
};

// 🎨 Admin Dashboard Charts - Making data beautiful
const AdminCharts = {
    // 📊 Initialize charts - Let's make some art!
    initCharts() {
        Logger.info('AdminCharts', '🎨 Time to paint with data!');
        this.initFeatureUsageChart();
    },

    // 📈 Feature usage chart - Where numbers become eye candy
    initFeatureUsageChart() {
        Logger.debug('AdminCharts', '📊 Creating a masterpiece with chart data');
        const el = document.querySelector('#featureUsageChart');
        if (!el) return;

        const options = {
            chart: { 
                type: 'bar',
                height: '100%',
                toolbar: {
                    show: false
                }
            },
            colors: ['#8B5CF6'],
            plotOptions: {
                bar: {
                    borderRadius: 8,
                    horizontal: true,
                }
            },
            dataLabels: {
                enabled: false
            },
            xaxis: {
                categories: ['Feature 1', 'Feature 2', 'Feature 3', 'Feature 4', 'Feature 5'],
            }
        };

        const chart = new ApexCharts(el, options);
        chart.render();
    }
};

// 🎬 Initialize everything - The grand opening!
document.addEventListener('DOMContentLoaded', () => {
    Logger.group('Admin', '🎬 Opening the admin panel');
    try {
        // 🎨 Initialize charts
        AdminCharts.initCharts();
        
        // 🎭 Initialize Lucide icons
        lucide.createIcons();
        
        Logger.info('Admin', '✨ Admin panel is ready for action!');
    } catch (error) {
        Logger.error('Admin', '😅 Houston, we have a problem', error);
    } finally {
        Logger.groupEnd();
    }
});