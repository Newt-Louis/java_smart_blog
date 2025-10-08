window.EventBus = (function () {
    const events = {};

    return {
        /**
         * Đăng ký lắng nghe sự kiện
         * @param {string} eventName Tên sự kiện
         * @param {Function} handler Hàm xử lý khi sự kiện được phát
         */
        on(eventName, handler) {
            if (!events[eventName]) {
                events[eventName] = [];
            }
            events[eventName].push(handler);
        },

        /**
         * Hủy lắng nghe sự kiện
         * @param {string} eventName Tên sự kiện
         * @param {Function} handler Hàm đã đăng ký
         */
        off(eventName, handler) {
            if (!events[eventName]) return;
            events[eventName] = events[eventName].filter(h => h !== handler);
        },

        /**
         * Phát (emit) một sự kiện
         * @param {string} eventName Tên sự kiện
         * @param {any} payload Dữ liệu gửi kèm
         */
        emit(eventName, payload = null) {
            if (!events[eventName]) return;
            // Gọi từng listener tương ứng
            events[eventName].forEach(handler => {
                try {
                    handler(payload);
                } catch (err) {
                    console.error(`EventBus handler for "${eventName}" failed:`, err);
                }
            });
        },

        /**
         * Xem tất cả sự kiện đang đăng ký (debug)
         */
        listEvents() {
            return Object.keys(events);
        }
    };
})();
