let currentForm = null;

document.addEventListener("DOMContentLoaded", function () {
  // Handle all success messages
  var successMessage = window.successMessage || "";
  if (successMessage) {
    if (successMessage.toLowerCase().includes("password")) {
      // Show password change toast
      showToast("passwordChangeToast");
    } else {
      // Show general user action toast
      document.getElementById("userActionMessage").textContent = successMessage;
      showToast("userActionToast");
    }
  }

  // Handle block confirmation modal
  document
    .getElementById("confirmBlockAction")
    .addEventListener("click", function () {
      if (currentForm) {
        currentForm.submit();
      }
      var modal = bootstrap.Modal.getInstance(
        document.getElementById("blockUserModal")
      );
      modal.hide();
    });

  // Handle fix status confirmation modal
  document
    .getElementById("confirmFixStatus")
    .addEventListener("click", function () {
      document.querySelector(".fix-status-form").submit();
      var modal = bootstrap.Modal.getInstance(
        document.getElementById("fixStatusModal")
      );
      modal.hide();
    });

  // Handle password confirmation validation
  document
    .getElementById("modalConfirmPassword")
    .addEventListener("input", function () {
      validatePasswordMatch();
    });

  document
    .getElementById("modalNewPassword")
    .addEventListener("input", function () {
      validatePasswordMatch();
    });

  // Handle change password form submission
  document
    .getElementById("changePasswordForm")
    .addEventListener("submit", function (e) {
      if (!validatePasswordMatch()) {
        e.preventDefault();
        return false;
      }
      // If validation passes, the form will submit normally and redirect
    });
});

function showToast(toastId) {
  var toastEl = document.getElementById(toastId);
  var toast = new bootstrap.Toast(toastEl);
  toast.show();
  setTimeout(function () {
    toast.hide();
  }, 5000);
}

function validatePasswordMatch() {
  const newPassword = document.getElementById("modalNewPassword").value;
  const confirmPassword = document.getElementById("modalConfirmPassword").value;
  const mismatchDiv = document.getElementById("passwordMismatch");
  const submitButton = document.getElementById("submitPasswordChange");

  if (confirmPassword && newPassword !== confirmPassword) {
    mismatchDiv.style.display = "block";
    submitButton.disabled = true;
    return false;
  } else {
    mismatchDiv.style.display = "none";
    submitButton.disabled = false;
    return true;
  }
}

function showBlockConfirmModal(button) {
  const username = button.getAttribute("data-username");
  const action = button.getAttribute("data-action");
  const form = button.closest("form");

  currentForm = form;

  const message = `Are you sure you want to ${action} user "${username}"?`;
  document.getElementById("blockModalMessage").textContent = message;

  const confirmButton = document.getElementById("confirmBlockAction");
  confirmButton.className =
    action === "block" ? "btn btn-danger" : "btn btn-success";
  confirmButton.textContent =
    action === "block" ? "Block User" : "Unblock User";

  var modal = new bootstrap.Modal(document.getElementById("blockUserModal"));
  modal.show();
}

function showFixStatusModal() {
  var modal = new bootstrap.Modal(document.getElementById("fixStatusModal"));
  modal.show();
}

function showChangePasswordModal(button) {
  const userId = button.getAttribute("data-user-id");
  const username = button.getAttribute("data-username");

  // Set the user information in the modal
  document.getElementById("modalUserId").value = userId;
  document.getElementById("modalUserName").value = username;

  // Clear the password fields
  document.getElementById("modalNewPassword").value = "";
  document.getElementById("modalConfirmPassword").value = "";

  // Hide password mismatch message and enable submit button
  document.getElementById("passwordMismatch").style.display = "none";
  document.getElementById("submitPasswordChange").disabled = false;

  var modal = new bootstrap.Modal(
    document.getElementById("changePasswordModal")
  );
  modal.show();
}
