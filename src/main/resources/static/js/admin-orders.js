// Admin Orders Page JavaScript - Optimized Version
class AdminOrdersManager {
  constructor() {
    this.statusChanges = {};
    this.elements = {};
    this.init();
  }

  // Cache DOM elements for better performance
  cacheElements() {
    this.elements = {
      saveButton: document.getElementById("saveAllButton"),
      changeCount: document.getElementById("changeCount"),
      bulkUpdateForm: document.getElementById("bulkUpdateForm"),
      statusUpdatesInput: document.getElementById("statusUpdatesJson"),
      orderItemsModal: document.getElementById("orderItemsModal"),
      modalTitle: document.querySelector("#orderItemsModal .modal-title"),
      modalBody: document.querySelector("#orderItemsModal #orderItemsContent"),
    };
  }

  // Initialize the application
  init() {
    this.cacheElements();
    this.bindEvents();
    this.initializeStatusSelects();
    this.setupAutoAlertDismissal();
  }

  // Bind all event listeners
  bindEvents() {
    // Use event delegation for better performance with dynamic content
    document.addEventListener("change", (e) => {
      if (e.target.classList.contains("status-select")) {
        this.handleStatusChange(e.target);
      }
    });

    document.addEventListener("click", (e) => {
      if (e.target.closest(".view-items-btn")) {
        this.handleViewOrderItems(e.target.closest(".view-items-btn"));
      }
    });
  }

  // Handle status change with optimized logic
  handleStatusChange(select) {
    const orderId = select.dataset.orderId;
    const originalStatus = select.dataset.originalStatus;
    const newStatus = select.value;
    const orderCard = document.querySelector(`[data-order-id="${orderId}"]`);

    if (!orderCard) return;

    // Use toggle for better performance
    const hasChanged = originalStatus !== newStatus;

    if (hasChanged) {
      this.statusChanges[orderId] = newStatus;
    } else {
      delete this.statusChanges[orderId];
    }

    // Batch DOM updates
    this.updateElementClasses(select, orderCard, hasChanged);
    this.updateSaveButton();
    this.updateSelectColor(select);
  }

  // Batch DOM class updates for better performance
  updateElementClasses(select, orderCard, hasChanged) {
    const method = hasChanged ? "add" : "remove";
    select.classList[method]("status-changed");
    orderCard.classList[method]("status-changed");
  }

  // Update save button state
  updateSaveButton() {
    const changesCount = Object.keys(this.statusChanges).length;
    const { saveButton, changeCount } = this.elements;

    saveButton.style.display = changesCount > 0 ? "block" : "none";
    changeCount.textContent = changesCount;
  }

  // Save all status changes
  saveAllStatuses() {
    const changesCount = Object.keys(this.statusChanges).length;

    if (changesCount === 0) {
      this.showAlert("No status changes to save.", "warning");
      return;
    }

    this.elements.statusUpdatesInput.value = JSON.stringify(this.statusChanges);
    this.elements.bulkUpdateForm.submit();
  }

  // Handle view order items with optimized modal management
  handleViewOrderItems(button) {
    const orderId = button.getAttribute("data-order-id");

    if (!orderId) {
      console.error("Order ID not found");
      return;
    }

    this.showModal(orderId);
    this.fetchOrderItems(orderId);
  }

  // Show modal with loading state
  showModal(orderId) {
    const { orderItemsModal, modalTitle, modalBody } = this.elements;

    // Update modal title
    modalTitle.innerHTML = this.createModalTitle(orderId);

    // Show loading state
    modalBody.innerHTML = this.createLoadingSpinner();

    // Show modal with proper accessibility
    const bsModal = bootstrap.Modal.getOrCreateInstance(orderItemsModal);
    bsModal.show();
  }

  // Create modal title HTML
  createModalTitle(orderId) {
    return `<i class="fas fa-pizza-slice me-2"></i>Order Items - Order #${orderId.substring(
      0,
      8
    )}`;
  }

  // Create loading spinner HTML
  createLoadingSpinner() {
    return `
      <div class="d-flex justify-content-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    `;
  }

  // Fetch order items with improved error handling
  async fetchOrderItems(orderId) {
    try {
      const response = await fetch(`/orders/api/admin/${orderId}`);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const data = await response.json();
      this.renderOrderItems(data);
    } catch (error) {
      console.error("Error fetching order items:", error);
      this.showErrorInModal(error.message);
    }
  }

  // Render order items in modal
  renderOrderItems(data) {
    const { modalBody } = this.elements;

    if (!data.items?.length) {
      modalBody.innerHTML = this.createEmptyState();
      return;
    }

    const itemsHtml = data.items
      .map((item, index) => this.createItemCard(item, index))
      .join("");
    modalBody.innerHTML = itemsHtml;
  }

  // Create individual item card HTML
  createItemCard(item, index) {
    const toppingsHtml = this.createToppingsHtml(item.toppings);

    return `
      <div class="order-item-card p-3 mb-3" data-item-index="${index}">
        <div class="d-flex align-items-center gap-3">
          <img src="${
            item.productImageUrl || "/assets/images/default-pizza.jpg"
          }" 
               alt="${item.productName || "Item"}" 
               class="order-item-image"
               loading="lazy">
          <div class="flex-grow-1">
            <h6 class="mb-1">${item.productName || "Unknown Item"}</h6>
            <div class="mb-2">
              <span class="size-badge">${item.size || "N/A"}</span>
            </div>
            <div class="d-flex flex-wrap gap-1">
              ${toppingsHtml}
            </div>
          </div>
          <div class="text-end">
            <div class="price-badge">₪${(item.subtotal || 0).toFixed(2)}</div>
            <small class="text-muted d-block mt-1">Qty: ${
              item.quantity || 1
            }</small>
          </div>
        </div>
      </div>
    `;
  }

  // Create toppings HTML
  createToppingsHtml(toppings) {
    if (!toppings?.length) return "";

    return toppings
      .map(
        (topping) =>
          `<span class="topping-badge">${topping.name || topping} ${
            topping.price ? `+₪${topping.price}` : ""
          }</span>`
      )
      .join("");
  }

  // Create empty state HTML
  createEmptyState() {
    return '<p class="text-muted text-center py-4">No items found for this order.</p>';
  }

  // Show error in modal
  showErrorInModal(message) {
    this.elements.modalBody.innerHTML = `<p class="text-danger text-center py-4">Error loading order items: ${message}</p>`;
  }

  // Update select color based on status
  updateSelectColor(select) {
    const statusClasses = [
      "text-warning",
      "text-info",
      "text-success",
      "text-danger",
    ];
    const statusMap = {
      PENDING: "text-warning",
      CONFIRMED: "text-info",
      DELIVERED: "text-success",
      CANCELLED: "text-danger",
    };

    // Remove all status classes efficiently
    select.classList.remove(...statusClasses);

    // Add appropriate class
    const statusClass = statusMap[select.value];
    if (statusClass) {
      select.classList.add(statusClass);
    }
  }

  // Initialize all status selects
  initializeStatusSelects() {
    document.querySelectorAll(".status-select").forEach((select) => {
      this.updateSelectColor(select);
    });
  }

  // Setup auto-dismissal for alerts
  setupAutoAlertDismissal() {
    const alerts = document.querySelectorAll(".alert[data-bs-dismiss]");
    if (!alerts.length) return;

    setTimeout(() => {
      alerts.forEach((alert) => {
        try {
          const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
          bsAlert.close();
        } catch (error) {
          console.warn("Could not close alert:", error);
        }
      });
    }, 5000);
  }

  // Utility method to show alerts
  showAlert(message, type = "info") {
    // This could be enhanced to create dynamic alerts
    alert(message);
  }
}

// Global functions for HTML onclick handlers (maintain backward compatibility)
let adminOrdersManager;

function trackStatusChange(select) {
  adminOrdersManager?.handleStatusChange(select);
}

function saveAllStatuses() {
  adminOrdersManager?.saveAllStatuses();
}

function viewOrderItems(button) {
  adminOrdersManager?.handleViewOrderItems(button);
}

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  adminOrdersManager = new AdminOrdersManager();
});
