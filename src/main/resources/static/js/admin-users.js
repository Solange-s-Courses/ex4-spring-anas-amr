// Admin Users Page JavaScript - Optimized Version
class AdminUsersManager {
  constructor() {
    this.currentForm = null;
    this.elements = {};
    this.init();
  }

  // Cache DOM elements for better performance
  cacheElements() {
    this.elements = {
      // Modal elements
      blockUserModal: document.getElementById("blockUserModal"),
      fixStatusModal: document.getElementById("fixStatusModal"),
      changePasswordModal: document.getElementById("changePasswordModal"),

      // Form elements
      changePasswordForm: document.getElementById("changePasswordForm"),
      fixStatusForm: document.querySelector(".fix-status-form"),

      // Input elements
      modalNewPassword: document.getElementById("modalNewPassword"),
      modalConfirmPassword: document.getElementById("modalConfirmPassword"),
      modalUserId: document.getElementById("modalUserId"),
      modalUserName: document.getElementById("modalUserName"),

      // Button elements
      confirmBlockAction: document.getElementById("confirmBlockAction"),
      confirmFixStatus: document.getElementById("confirmFixStatus"),
      submitPasswordChange: document.getElementById("submitPasswordChange"),

      // Message elements
      blockModalMessage: document.getElementById("blockModalMessage"),
      passwordMismatch: document.getElementById("passwordMismatch"),
      userActionMessage: document.getElementById("userActionMessage"),

      // Toast elements
      passwordChangeToast: document.getElementById("passwordChangeToast"),
      userActionToast: document.getElementById("userActionToast"),
    };
  }

  // Initialize the application
  init() {
    this.cacheElements();
    this.bindEvents();
    this.handleSuccessMessages();
  }

  // Bind all event listeners
  bindEvents() {
    // Use event delegation for better performance
    document.addEventListener("click", (e) => {
      this.handleDocumentClick(e);
    });

    document.addEventListener("input", (e) => {
      this.handleDocumentInput(e);
    });

    document.addEventListener("submit", (e) => {
      this.handleDocumentSubmit(e);
    });
  }

  // Handle document-level click events
  handleDocumentClick(e) {
    const { target } = e;

    if (target.id === "confirmBlockAction") {
      this.handleBlockConfirmation();
    } else if (target.id === "confirmFixStatus") {
      this.handleFixStatusConfirmation();
    } else if (target.closest("[data-action]")) {
      this.handleUserAction(target.closest("[data-action]"));
    }
  }

  // Handle document-level input events
  handleDocumentInput(e) {
    const { target } = e;

    if (
      target.id === "modalNewPassword" ||
      target.id === "modalConfirmPassword"
    ) {
      this.validatePasswordMatch();
    }
  }

  // Handle document-level submit events
  handleDocumentSubmit(e) {
    if (e.target.id === "changePasswordForm") {
      if (!this.validatePasswordMatch()) {
        e.preventDefault();
        return false;
      }
    }
  }

  // Handle success messages on page load
  handleSuccessMessages() {
    const successMessage = window.successMessage || "";

    if (!successMessage) return;

    if (this.isPasswordMessage(successMessage)) {
      this.showToast("passwordChangeToast");
    } else {
      this.showUserActionMessage(successMessage);
    }
  }

  // Check if message is password-related
  isPasswordMessage(message) {
    return message.toLowerCase().includes("password");
  }

  // Show user action message
  showUserActionMessage(message) {
    if (this.elements.userActionMessage) {
      this.elements.userActionMessage.textContent = message;
      this.showToast("userActionToast");
    }
  }

  // Handle block/unblock confirmation
  handleBlockConfirmation() {
    if (this.currentForm) {
      this.currentForm.submit();
      this.currentForm = null;
    }
    this.hideModal("blockUserModal");
  }

  // Handle fix status confirmation
  handleFixStatusConfirmation() {
    if (this.elements.fixStatusForm) {
      this.elements.fixStatusForm.submit();
    }
    this.hideModal("fixStatusModal");
  }

  // Handle user actions (block/unblock/password change)
  handleUserAction(button) {
    const action = button.getAttribute("data-action");

    switch (action) {
      case "block":
      case "unblock":
        this.showBlockConfirmModal(button);
        break;
      case "changePassword":
        this.showChangePasswordModal(button);
        break;
      case "fixStatus":
        this.showFixStatusModal();
        break;
    }
  }

  // Show toast notification with auto-hide
  showToast(toastId) {
    const toastElement = this.elements[toastId];

    if (!toastElement) {
      console.warn(`Toast element ${toastId} not found`);
      return;
    }

    try {
      const toast = bootstrap.Toast.getOrCreateInstance(toastElement);
      toast.show();

      // Auto-hide after 5 seconds
      setTimeout(() => {
        toast.hide();
      }, 5000);
    } catch (error) {
      console.error("Error showing toast:", error);
    }
  }

  // Validate password match with improved logic
  validatePasswordMatch() {
    const {
      modalNewPassword,
      modalConfirmPassword,
      passwordMismatch,
      submitPasswordChange,
    } = this.elements;

    if (!modalNewPassword || !modalConfirmPassword) return true;

    const newPassword = modalNewPassword.value;
    const confirmPassword = modalConfirmPassword.value;

    const isValid = this.checkPasswordsMatch(newPassword, confirmPassword);

    this.updatePasswordValidationUI(isValid);

    return isValid;
  }

  // Check if passwords match
  checkPasswordsMatch(newPassword, confirmPassword) {
    return !confirmPassword || newPassword === confirmPassword;
  }

  // Update password validation UI
  updatePasswordValidationUI(isValid) {
    const { passwordMismatch, submitPasswordChange } = this.elements;

    if (passwordMismatch) {
      passwordMismatch.style.display = isValid ? "none" : "block";
    }

    if (submitPasswordChange) {
      submitPasswordChange.disabled = !isValid;
    }
  }

  // Show block confirmation modal
  showBlockConfirmModal(button) {
    const username = button.getAttribute("data-username");
    const action = button.getAttribute("data-action");
    const form = button.closest("form");

    if (!username || !action || !form) {
      console.error("Missing required attributes for block modal");
      return;
    }

    this.currentForm = form;
    this.setupBlockModal(username, action);
    this.showModal("blockUserModal");
  }

  // Setup block modal content
  setupBlockModal(username, action) {
    const { blockModalMessage, confirmBlockAction } = this.elements;

    const message = `Are you sure you want to ${action} user "${username}"?`;
    const isBlock = action === "block";

    if (blockModalMessage) {
      blockModalMessage.textContent = message;
    }

    if (confirmBlockAction) {
      confirmBlockAction.className = isBlock
        ? "btn btn-danger"
        : "btn btn-success";
      confirmBlockAction.textContent = isBlock ? "Block User" : "Unblock User";
    }
  }

  // Show fix status modal
  showFixStatusModal() {
    this.showModal("fixStatusModal");
  }

  // Show change password modal
  showChangePasswordModal(button) {
    const userId = button.getAttribute("data-user-id");
    const username = button.getAttribute("data-username");

    if (!userId || !username) {
      console.error("Missing required attributes for password modal");
      return;
    }

    this.setupPasswordModal(userId, username);
    this.showModal("changePasswordModal");
  }

  // Setup password modal content
  setupPasswordModal(userId, username) {
    const {
      modalUserId,
      modalUserName,
      modalNewPassword,
      modalConfirmPassword,
      passwordMismatch,
      submitPasswordChange,
    } = this.elements;

    // Set user information
    if (modalUserId) modalUserId.value = userId;
    if (modalUserName) modalUserName.value = username;

    // Clear password fields
    if (modalNewPassword) modalNewPassword.value = "";
    if (modalConfirmPassword) modalConfirmPassword.value = "";

    // Reset validation state
    if (passwordMismatch) passwordMismatch.style.display = "none";
    if (submitPasswordChange) submitPasswordChange.disabled = false;
  }

  // Show modal with error handling
  showModal(modalId) {
    const modalElement = this.elements[modalId];

    if (!modalElement) {
      console.error(`Modal ${modalId} not found`);
      return;
    }

    try {
      const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
      modal.show();
    } catch (error) {
      console.error(`Error showing modal ${modalId}:`, error);
    }
  }

  // Hide modal with error handling
  hideModal(modalId) {
    const modalElement = this.elements[modalId];

    if (!modalElement) return;

    try {
      const modal = bootstrap.Modal.getInstance(modalElement);
      if (modal) {
        modal.hide();
      }
    } catch (error) {
      console.error(`Error hiding modal ${modalId}:`, error);
    }
  }
}

// Global functions for HTML onclick handlers (maintain backward compatibility)
let adminUsersManager;

function showBlockConfirmModal(button) {
  adminUsersManager?.showBlockConfirmModal(button);
}

function showFixStatusModal() {
  adminUsersManager?.showFixStatusModal();
}

function showChangePasswordModal(button) {
  adminUsersManager?.showChangePasswordModal(button);
}

function validatePasswordMatch() {
  return adminUsersManager?.validatePasswordMatch() ?? true;
}

function showToast(toastId) {
  adminUsersManager?.showToast(toastId);
}

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  adminUsersManager = new AdminUsersManager();
});
