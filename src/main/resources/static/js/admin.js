// 📦 Import our trusty Logger - The debugger's best friend
import { Logger } from './logger.js';

// 🎭 Admin Panel Functionality - Where the magic happens! ✨

// 🔄 Sidebar Toggle - The hide and seek champion
// Make toggleSubmenu globally accessible since it's used in HTML onclick handlers
window.toggleSubmenu = (menuId) => {
    Logger.debug('Admin', '🎪 Playing peek-a-boo with submenu', { menuId });
    const menu = document.getElementById(menuId);
    const icon = document.getElementById(menuId + '-icon');
    menu.classList.toggle('hidden');
    icon.classList.toggle('rotate-90');
};

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
            Logger.info('AdminAPI', '✨ Poof! It is  gone', { type, id });
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
        this.initVisitorStatsChart();
        this.initActivityHeatmap(); // Commenting out heatmap chart to isolate error
    },

    initVisitorStatsChart() {
        Logger.debug('AdminCharts', '📊 Creating a masterpiece with chart data');
        const el = document.querySelector('#visitorChart');
        if (!el) return;

        const initialDays = document.querySelector('#visitorTrendsPeriod').value; // Get initial value from dropdown

        fetch(`/api/analytics/visitor-stats?days=${initialDays}`)
            .then(response => response.json())
            .then(data => {
                console.log("Visitor Stats Data:", data); // Added log
                const options = {
                    chart: {
                        type: 'area',
                        height: '100%',
                        toolbar: { show: false },
                        zoom: { enabled: false }
                    },
                    series: [{
                        name: 'Daily Visitors',
                        data: data.dailyVisitors.map(point => [new Date(point.date).getTime(), point.visitors])
                    }],
                    xaxis: {
                        type: 'datetime',
                        labels: {
                            format: 'dd MMM',
                            style: { colors: '#718096' }
                        },
                        min: new Date(data.dailyVisitors[0].date).getTime(), // Set initial x-axis range
                        max: new Date(data.dailyVisitors[data.dailyVisitors.length - 1].date).getTime(),
                    },
                    yaxis: {
                        labels: {
                            formatter: val => Math.floor(val),
                            style: { colors: '#718096' }
                        }
                    },
                    colors: ['#4F46E5'],
                    fill: {
                        type: 'gradient',
                        gradient: {
                            shadeIntensity: 1,
                            opacityFrom: 0.45,
                            opacityTo: 0.05,
                            stops: [50, 100, 100]
                        }
                    },
                    stroke: { width: 2 },
                    tooltip: {
                        theme: 'dark',
                        x: { format: 'dd MMM yyyy' }
                    }
                };

                this.visitorChart = new ApexCharts(el, options);
                this.visitorChart.render();
            });
        },

    initActivityHeatmap() {
        Logger.debug('AdminCharts', '🗺️ Creating a heatmap of user activity');
        const el = document.querySelector('#heatmapChart');
        if (!el) return;

        fetch(`/api/analytics/activity-heatmap?days=7`)
            .then(response => response.json())
            .then(data => {
                const options = {
                    chart: {
                        height: '100%',
                        type: 'heatmap',
                        toolbar: { show: false },
                        fontFamily: 'Inter, sans-serif',
                        background: '#fff',
                        animations: {
                            speed: 500
                        }
                    },
                    series: this.transformHeatmapData(data),
                    plotOptions: {
                        heatmap: {
                            enableShades: true,
                            distributed: true,
                            colorScale: {
                                ranges: [
                                    { from: 0, to: 0, name: 'None', color: '#F7F7F7' },
                                    { from: 1, to: 15, name: 'Very Low', color: '#E0E0E0' },
                                    { from: 16, to: 30, name: 'Low', color: '#BDBDBD' },
                                    { from: 31, to: 50, name: 'Medium', color: '#9E9E9E' },
                                    { from: 51, to: 75, name: 'High', color: '#616161' },
                                    { from: 76, to: 100, name: 'Very High', color: '#424242' }
                                ]
                            }
                        }
                    },
                    dataLabels: { enabled: false },
                    xaxis: {
                        categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                        tooltip: { enabled: false } // Disable xaxis tooltip
                    },
                    yaxis: {
                        reversed: true
                    },
                };

                this.heatmapChart = new ApexCharts(el, options); // Store the chart instance
                this.heatmapChart.render();

            });
        },

        transformHeatmapData(data) {
            if (!data || !Array.isArray(data)) {
                return this.generateEmptyHeatmapData();
            }

            const timeBlocks = [];
            for (let hour = 0; hour < 24; hour += 3) {
                timeBlocks.push({
                    name: `${String(hour).padStart(2, '0')}:00-${String(hour + 3).padStart(2, '0')}:00`,
                    data: Array(7).fill(0) // Initialize with zeros for each day
                });
            }

            // Fill in actual data
            data.forEach(point => {
                if (point && typeof point.hour === 'number' && typeof point.dayOfWeek === 'number') {
                    const blockIndex = Math.floor(point.hour / 3);
                    // PostgreSQL's EXTRACT(DOW) returns 0-6 for Sun-Sat, we need 1-7 for Mon-Sun
                    const dayIndex = (point.dayOfWeek + 6) % 7; // Convert Sunday=0 to Sunday=6
                    if (timeBlocks[blockIndex]) {
                        timeBlocks[blockIndex].data[dayIndex] = point.count || 0;
                    }
                }
            });

            return timeBlocks;
        },
        generateEmptyHeatmapData() {
            return Array.from({ length: 8 }, (_, i) => ({
                name: `${String(i * 3).padStart(2, '0')}:00-${String((i * 3) + 3).padStart(2, '0')}:00`,
                data: Array(7).fill(0)
            }));
        }

};

// 🎬 Initialize everything - The grand opening!
document.addEventListener('DOMContentLoaded', () => {
    Logger.group('Admin', '🎬 Opening the admin panel');
    try {
        // 🎭 Initialize Lucide icons
        lucide.createIcons();

        // 🎨 Initialize charts - Moved to after Lucide icons
        AdminCharts.initCharts();

        //Move existing functions into the AdminCharts object
        AdminCharts.updateVisitorStats = async function(days = 30) {
            try {
                const response = await fetch(`/api/analytics/visitor-stats?days=${days}`);
                const data = await response.json();

                const series = [{
                    name: 'Daily Visitors',
                    data: data.dailyVisitors.map(point => [
                        new Date(point.date).getTime(),
                        point.visitors
                    ])
                }];

                if (this.visitorChart) {
                    this.visitorChart.updateSeries(series);

                    // Calculate the x-axis range based on 'days'
                    const endDate = new Date();
                    const startDate = new Date();
                    startDate.setDate(endDate.getDate() - days);

                    this.visitorChart.updateOptions({
                        xaxis: {
                            min: startDate.getTime(),
                            max: endDate.getTime(),
                            tickAmount: days <= 30 ? days : 30, // Adjust tick amount for longer periods
                            //If no data use this
                            type: 'datetime',
                                labels: {
                                    format: 'dd MMM',
                                    style: { colors: '#718096' }
                                },
                        }
                    });
                }

                const avgDuration = data.avgSessionDuration != null ? data.avgSessionDuration : 0;
                const avgSessionElement = document.getElementById('avgSessionDuration');

                if (avgDuration > 0 || avgSessionElement.textContent === '0m') {
                    avgSessionElement.textContent = this.formatDuration(avgDuration);
                }

                // Update rate changes
                this.updateRateChanges(data);
            } catch (error) {
                console.error('Error fetching visitor stats:', error);
                // Don't update values on error to preserve existing data
            }
        };

        AdminCharts.updateHeatmap = async function (days = 7) {
            try {
                const response = await fetch(`/api/analytics/activity-heatmap?days=${days}`);
                if (!response.ok) {
                    throw new Error(`Server responded with status: ${response.status}`);
                }
                const data = await response.json();

                const transformedData = this.transformHeatmapData(data);

                if (this.heatmapChart) {
                    this.heatmapChart.updateSeries(transformedData);
                }

            } catch (error) {
                console.error('Error fetching heatmap data:', error);
                // Set empty data with proper structure when API fails
                const emptyData = this.generateEmptyHeatmapData();
                if (this.heatmapChart) {
                    this.heatmapChart.updateSeries(emptyData);
                }
            }
        };

        // Helper function to format duration
        AdminCharts.formatDuration = function(seconds) {
            if (!seconds) return '0m';
            const minutes = Math.round(seconds / 60);
            if (minutes < 60) return `${minutes}m`;
            const hours = Math.floor(minutes / 60);
            const remainingMinutes = minutes % 60;
            return remainingMinutes > 0 ? `${hours}h ${remainingMinutes}m` : `${hours}h`;
        };

        // Helper function to update rate changes display
        AdminCharts.updateRateChanges = function(data) {
            document.querySelectorAll('.rate-change').forEach(element => {
                const changeType = element.dataset.change;
                const value = data[changeType];
                Logger.debug('AdminCharts', '📈 Updating rate change element', { changeType, value, element }); // Added log

                if (value !== undefined) {
                    const type =  changeType === 'durationChange' ? 'duration' : 'increase';

                    element.textContent = this.formatRateChange(value, type);
                    element.classList.remove('text-green-400', 'text-red-400');
                    element.classList.add(value >= 0 ? 'text-green-400' : 'text-red-400');

                }
            });
        };

        // Helper function to format rate changes
        AdminCharts.formatRateChange = function (change, type = 'increase') {
            const isNegative = change < 0;
            const arrow = isNegative ? '↓' : '↑';
            const absChange = Math.abs(change).toFixed(1);

            let description;
            switch(type) {
                case 'duration':
                    description = 'from last period';
                    break;
                default:
                    description = 'from last period';
            }

            return `${arrow} ${absChange}% ${description}`;
        };

        // Make functions globally available
        window.updateVisitorStats = AdminCharts.updateVisitorStats.bind(AdminCharts);
        window.updateHeatmap = AdminCharts.updateHeatmap.bind(AdminCharts);


        // Initial load
        AdminCharts.updateVisitorStats();
        AdminCharts.updateHeatmap();

        // Refresh every 5 minutes
        setInterval(() => {
            AdminCharts.updateVisitorStats();
            AdminCharts.updateHeatmap();
        }, 5 * 60 * 1000);


        Logger.info('Admin', '✨ Admin panel is ready for action!');
    } catch (error) {
        Logger.error('Admin', '😅 Houston, we have a problem', error);
    } finally {
        Logger.groupEnd();
    }
});
