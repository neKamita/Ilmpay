// 🎭 Admin Panel Functionality

// 🔄 Sidebar Toggle
function toggleSubmenu(menuId) {
    const menu = document.getElementById(menuId);
    const icon = document.getElementById(menuId + '-icon');
    menu.classList.toggle('hidden');
    icon.classList.toggle('rotate-90');
}

// 🎯 Admin API Functions
const AdminAPI = {
    // 📝 CRUD Operations
    async fetchItem(type, id) {
        const response = await fetch(`/api/admin/${type}s/${id}`);
        const data = await response.json();
        if (!data.success) throw new Error(data.message);
        return data.data;
    },

    async deleteItem(type, id) {
        const response = await fetch(`/api/admin/${type}s/${id}`, {
            method: 'DELETE'
        });
        const data = await response.json();
        if (!data.success) throw new Error(data.message);
        return data;
    }
};

// 🎨 Admin UI Functions
const AdminUI = {
    // 🎯 Show loading state
    showLoading(message = 'Loading...') {
        return Swal.fire({
            title: message,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });
    },

    // ✨ Show success message
    async showSuccess(message) {
        await Swal.fire({
            title: 'Success!',
            text: message,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
        });
    },

    // ❌ Show error message
    async showError(error) {
        await Swal.fire({
            title: 'Error!',
            text: error.message || 'An unexpected error occurred',
            icon: 'error',
            confirmButtonColor: '#4f46e5'
        });
    },

    // 🎯 Show delete confirmation
    async confirmDelete() {
        const result = await Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#4f46e5',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!',
            cancelButtonText: 'Cancel'
        });
        return result.isConfirmed;
    }
};

// 🎯 Admin Event Handlers
const AdminHandlers = {
    // 📝 Edit item
    async handleEdit(type, id) {
        try {
            AdminUI.showLoading();
            const data = await AdminAPI.fetchItem(type, id);
            Swal.close();
            Modal.show(type, data);
        } catch (error) {
            console.error('Error:', error);
            await AdminUI.showError(error);
        }
    },

    // ❌ Delete item
    async handleDelete(type, id) {
        try {
            const confirmed = await AdminUI.confirmDelete();
            if (!confirmed) return;

            await AdminAPI.deleteItem(type, id);
            await AdminUI.showSuccess('Item deleted successfully');
            location.reload();
        } catch (error) {
            console.error('Error:', error);
            await AdminUI.showError(error);
        }
    }
};

// 🎨 Admin Dashboard Charts
const AdminCharts = {
    // 📊 Initialize charts
    initCharts() {
        this.initFeatureUsageChart();
        // Add other chart initializations here
    },

    // 📈 Feature usage chart
    initFeatureUsageChart() {
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

// 🎬 Initialize everything
document.addEventListener('DOMContentLoaded', () => {
    // 🎨 Initialize charts
    AdminCharts.initCharts();

    // 🎯 Initialize Lucide icons
    lucide.createIcons();

    // 🌟 Expose global functions
    window.toggleSubmenu = toggleSubmenu;
    window.editItem = AdminHandlers.handleEdit;
    window.deleteItem = AdminHandlers.handleDelete;
});