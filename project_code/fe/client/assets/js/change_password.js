function resetForm() {
    document.getElementById('oldPassword').value = '';
    document.getElementById('newPassword').value = '';
    document.getElementById('confirmNewPassword').value = '';
}

// Hàm xử lý đổi mật khẩu
function changePassword() {
    const userData = JSON.parse(localStorage.getItem('user'));
    if (!userData || !userData.id) {
        alert('Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!');
        window.location.href = 'login.html';
        return;
    }
    const currentUserId = userData.id;

    const oldPassword = document.getElementById('oldPassword').value.trim();
    const newPassword = document.getElementById('newPassword').value.trim();
    const confirmNewPassword = document.getElementById('confirmNewPassword').value.trim();

    if (!oldPassword) {
        alert('Vui lòng nhập mật khẩu cũ!');
        return;
    }
    if (!newPassword) {
        alert('Vui lòng nhập mật khẩu mới!');
        return;
    }
    if (!confirmNewPassword) {
        alert('Vui lòng nhập lại mật khẩu mới!');
        return;
    }

    if (newPassword !== confirmNewPassword) {
        alert('Mật khẩu mới và xác nhận mật khẩu không khớp!');
        return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
        alert('Không tìm thấy token. Vui lòng đăng nhập lại!');
        window.location.href = 'login.html';
        return;
    }

    const passwordData = {
        oldPassword: oldPassword,
        newPassword: newPassword,
        confirmPassword: confirmNewPassword
    };

    console.log('Đang gửi yêu cầu đổi mật khẩu...', {
        userId: currentUserId,
        oldPassword: '***',
        newPassword: '***',
        confirmPassword: '***'
    });

    fetch(`http://localhost:8080/event-management/users/update-password/${currentUserId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(passwordData)
    })
        .then(async response => {
            console.log('Response status:', response.status);
            console.log('Response headers:', Object.fromEntries(response.headers.entries()));

            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                throw new Error('Phản hồi không hợp lệ từ server');
            }

            const responseData = await response.json();
            console.log('Response data:', responseData);

            if (!response.ok) {
                
                switch (response.status) {
                    case 400:
                        throw new Error(responseData.message || 'Dữ liệu không hợp lệ');
                    case 401:
                        throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại');
                    case 403:
                        throw new Error('Bạn không có quyền thực hiện thao tác này');
                    case 404:
                        throw new Error('Không tìm thấy người dùng');
                    default:
                        throw new Error(responseData.message || 'Không thể đổi mật khẩu');
                }
            }

            if (!responseData.result || responseData.result !== "Password updated successfully") {
                throw new Error(responseData.message || 'Lỗi không xác định');
            }

            return responseData;
        })
        .then(data => {
            console.log('Đổi mật khẩu thành công:', data);
            alert('Đổi mật khẩu thành công!');
            resetForm();
        })
        .catch(error => {
            console.error('Lỗi khi đổi mật khẩu:', error);
            alert(`Không thể đổi mật khẩu: ${error.message}`);
        });
}
