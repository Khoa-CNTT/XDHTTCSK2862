document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.getElementById("registerForm");
    const verifyForm = document.getElementById("verifyForm");

    if (registerForm) {
        registerForm.addEventListener("submit", handleRegister);
    }
    if (verifyForm) {
        verifyForm.addEventListener("submit", handleVerify);
        setupVerificationInputs();
    }

    setupValidation("lastName", "Vui lòng nhập họ!");
    setupValidation("firstName", "Vui lòng nhập tên!");
    setupValidation("email", "Vui lòng nhập email!", /^[^\s@]+@[^\s@]+\.[^\s@]+$/, "Email không hợp lệ!");
    setupValidation("phone", "Vui lòng nhập số điện thoại!", /^(03|05|07|08|09)\d{8}$/, "Số điện thoại không hợp lệ!");
    setupValidation("password", "Vui lòng nhập mật khẩu!");
});

function setupVerificationInputs() {
    const inputs = document.querySelectorAll('.verification-input');
    inputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            if (e.target.value.length === 1 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });
}
async function handleVerify(event) {
    event.preventDefault();
    const verificationCode = [
        getValue("code1"),
        getValue("code2"),
        getValue("code3"),
        getValue("code4"),
        getValue("code5"),
        getValue("code6")
    ].join('');
    const tempUser = JSON.parse(localStorage.getItem("tempUser"));
    if (verificationCode.length !== 6) return showAlert("Vui lòng nhập đầy đủ 6 ký tự mã xác thực!", "danger");
    if (!tempUser) return showAlert("Dữ liệu người dùng tạm thời không tồn tại!", "danger");

    try {
        const url = `http://localhost:8080/event-management/api/verification/verify?code=${verificationCode}`;
        console.log("Request URL:", url);

        const response = await fetch(url, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        const data = await response.json();
        console.log("Response status:", response.status);
        console.log("Response data:", data);

        if (response.ok) {
            showAlert("Đăng ký thành công!", "success");
            localStorage.setItem("token", data.token);
            localStorage.setItem("user", JSON.stringify(data.user));
            localStorage.removeItem("tempUser");

            if (typeof window.updateHeader === "function") {
                window.updateHeader();
            }

            setTimeout(() => (window.location.href = "login.html"), 2000);
        } else {
            showAlert(data.message || "Mã xác thực không đúng!", "danger");
        }
    } catch (error) {
        showAlert("Lỗi kết nối đến máy chủ! Vui lòng thử lại.", "danger");
        console.error("Lỗi trong handleVerify:", error);
    }
}

