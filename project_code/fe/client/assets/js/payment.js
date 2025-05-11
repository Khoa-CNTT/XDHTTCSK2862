const CONTRACT_API_URL = "https://67eabf6734bcedd95f647797.mockapi.io/Contract";
const VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
const VNP_TMN_CODE = "";
const VNP_HASH_SECRET = "";
const VNP_RETURN_URL = window.location.href;

function getToken() {
    return localStorage.getItem("token");
}

function formatCurrency(value) {
    return value.toLocaleString('vi-VN') + " ₫";
}

function formatDateTime(isoString) {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleString('vi-VN', { dateStyle: 'short', timeStyle: 'short' });
}

async function fetchData(url, method = "GET", data = null) {
    const token = getToken();
    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json"
        }
    };
    if (token) options.headers["Authorization"] = `Bearer ${token}`;
    if (data) options.body = JSON.stringify(data);

    const response = await fetch(url, options);
    if (!response.ok) {
        throw new Error(`${method} failed! Status: ${response.status}`);
    }
    return response.json();
}

async function fetchContractDetails(contractId) {
    try {
        const contract = await fetchData(`${CONTRACT_API_URL}/${contractId}`);
        return {
            id: contract.id,
            contractName: contract.name,
            value: contract.total_price || 0,
            status: contract.status || 'draft'
        };
    } catch (error) {
        alert(`Lỗi khi lấy thông tin hợp đồng: ${error.message}`);
        return null;
    }
}

async function updateContractStatus(contractId, status) {
    try {
        const contract = await fetchData(`${CONTRACT_API_URL}/${contractId}`);
        contract.status = status;
        await fetchData(`${CONTRACT_API_URL}/${contractId}`, "PUT", contract);
        return true;
    } catch (error) {
        console.error(`Lỗi khi cập nhật trạng thái hợp đồng: ${error.message}`);
        return false;
    }
}

function calculateDepositAmount(totalValue) {
    return totalValue * 0.1; // 10% giá trị hợp đồng
}

function calculateRemainingAmount(totalValue) {
    return totalValue * 0.9; // 90% giá trị hợp đồng
}

function generateVNPAYUrl(amount, orderId, orderInfo) {
    const params = new URLSearchParams();
    params.append("vnp_Version", "2.1.0");
    params.append("vnp_Command", "pay");
    params.append("vnp_TmnCode", VNP_TMN_CODE);
    params.append("vnp_Amount", amount * 100); // VNPAY yêu cầu số tiền nhân 100
    params.append("vnp_CurrCode", "VND");
    params.append("vnp_TxnRef", orderId);
    params.append("vnp_OrderInfo", orderInfo);
    params.append("vnp_OrderType", "250000"); // Mã ngành hàng
    params.append("vnp_Locale", "vn");
    params.append("vnp_ReturnUrl", VNP_RETURN_URL);
    params.append("vnp_IpAddr", "127.0.0.1");
    params.append("vnp_CreateDate", new Date().toISOString().replace(/[-:T.]/g, "").slice(0, 14));

    let signData = params.toString();
    let signed = CryptoJS.HmacSHA512(signData, VNP_HASH_SECRET).toString(CryptoJS.enc.Hex);
    params.append("vnp_SecureHash", signed);

    return `${VNPAY_URL}?${params.toString()}`;
}

function verifyVNPAYResponse(params) {
    const secureHash = params.get("vnp_SecureHash");
    params.delete("vnp_SecureHash");
    params.delete("vnp_SecureHashType");

    const sortedParams = new URLSearchParams([...params.entries()].sort());
    let signData = sortedParams.toString();
    let signed = CryptoJS.HmacSHA512(signData, VNP_HASH_SECRET).toString(CryptoJS.enc.Hex);

    return secureHash === signed;
}

async function processPaymentResult() {
    const urlParams = new URLSearchParams(window.location.search);
    const resultMessage = document.getElementById("resultMessage");
    const paymentButton = document.getElementById("paymentButton");
    const vnp_ResponseCode = urlParams.get("vnp_ResponseCode");
    const contractId = urlParams.get("vnp_TxnRef")?.split("_")[1];
    const paymentType = urlParams.get("vnp_TxnRef")?.startsWith("DEP_") ? "deposit" : "remaining";

    if (!vnp_ResponseCode) return;

    resultMessage.style.display = "block";
    paymentButton.style.display = "none";

    if (!verifyVNPAYResponse(urlParams)) {
        resultMessage.className = "result-message error";
        resultMessage.innerHTML = "Lỗi: Chữ ký không hợp lệ. Giao dịch không được xác thực!";
        return;
    }

    if (vnp_ResponseCode === "00" && contractId) {
        const newStatus = paymentType === "deposit" ? "deposit_paid" : "waiting_paid";
        const updated = await updateContractStatus(contractId, newStatus);
        resultMessage.className = "result-message success";
        resultMessage.innerHTML = updated
            ? `Thanh toán ${paymentType === "deposit" ? "đặt cọc" : "khoản còn lại"} thành công! Hợp đồng đã được cập nhật trạng thái.`
            : `Thanh toán thành công nhưng lỗi khi cập nhật trạng thái hợp đồng!`;
        localStorage.setItem("lastTransaction", JSON.stringify({
            type: paymentType,
            transactionNo: urlParams.get("vnp_TransactionNo"),
            amount: parseFloat(urlParams.get("vnp_Amount")) / 100,
            date: urlParams.get("vnp_PayDate")
        }));
        loadContractInfo(); // Tải lại thông tin hợp đồng để cập nhật giao diện
    } else {
        resultMessage.className = "result-message error";
        resultMessage.innerHTML = `Thanh toán ${paymentType === "deposit" ? "đặt cọc" : "khoản còn lại"} thất bại! Mã lỗi: ${vnp_ResponseCode}`;
        paymentButton.style.display = "inline-block";
    }
}

async function proceedToPayment() {
    const contractId = localStorage.getItem("contractId");
    const contractStatus = localStorage.getItem("contractStatus");
    const amount = parseFloat(localStorage.getItem("paymentAmount"));
    const isDeposit = contractStatus === "draft";
    const orderId = `${isDeposit ? "DEP" : "PAY"}_${contractId}_${Date.now()}`;
    const orderInfo = `Thanh toan ${isDeposit ? "dat coc" : "khoan con lai"} hop dong ${contractId}`;

    const paymentUrl = generateVNPAYUrl(amount, orderId, orderInfo);
    window.location.href = paymentUrl;
}

async function loadContractInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    const contractId = urlParams.get('id');
    if (!contractId) {
        alert("Không tìm thấy ID hợp đồng!");
        window.location.href = "ListContract.html";
        return;
    }

    const contract = await fetchContractDetails(contractId);
    if (!contract) {
        window.location.href = "ListContract.html";
        return;
    }

    const statusMessage = document.getElementById("statusMessage");
    const contractInfo = document.getElementById("contractInfo");
    const depositLabel = document.getElementById("depositLabel");
    const depositAmountInput = document.getElementById("depositAmount");
    const amountLabel = document.getElementById("amountLabel");
    const amountInput = document.getElementById("amount");
    const paymentMethodSection = document.getElementById("paymentMethodSection");
    const paymentButton = document.getElementById("paymentButton");
    const transactionInfo = document.getElementById("transactionInfo");

    document.getElementById("contractName").value = contract.contractName;
    document.getElementById("contractValue").value = formatCurrency(contract.value);
    localStorage.setItem("contractId", contractId);
    localStorage.setItem("contractStatus", contract.status);

    statusMessage.style.display = "none";
    depositLabel.style.display = "none";
    depositAmountInput.style.display = "none";
    paymentMethodSection.style.display = "none";
    paymentButton.style.display = "none";
    transactionInfo.style.display = "none";

    const depositAmount = calculateDepositAmount(contract.value);

    if (contract.status === "draft") {
        // Chưa đặt cọc
        amountLabel.textContent = "Số Tiền Đặt Cọc (10%):";
        amountInput.value = formatCurrency(depositAmount);
        paymentMethodSection.style.display = "block";
        paymentButton.style.display = "inline-block";
        paymentButton.textContent = "Thanh Toán Đặt Cọc";
        paymentButton.onclick = proceedToPayment;
        localStorage.setItem("paymentAmount", depositAmount);
    } else if (contract.status === "completed") {
        // Hoàn thành
        statusMessage.style.display = "block";
        statusMessage.className = "status-message completed";
        statusMessage.innerHTML = "Hợp đồng đã thanh toán đầy đủ!";
        depositLabel.style.display = "block";
        depositAmountInput.style.display = "block";
        depositAmountInput.value = formatCurrency(depositAmount);
        amountLabel.textContent = "Số Tiền Còn Lại:";
        amountInput.value = formatCurrency(0);
        const lastTransaction = JSON.parse(localStorage.getItem("lastTransaction") || "{}");
        if (lastTransaction && lastTransaction.transactionNo) {
            transactionInfo.style.display = "block";
            transactionInfo.innerHTML = `
                        <strong>Thông Tin Giao Dịch:</strong><br>
                        Mã giao dịch: ${lastTransaction.transactionNo}<br>
                        Số tiền: ${formatCurrency(lastTransaction.amount)}<br>
                        Thời gian: ${formatDateTime(lastTransaction.date)}<br>
                        Loại: ${lastTransaction.type === "deposit" ? "Đặt cọc" : "Thanh toán khoản còn lại"}
                    `;
        }
    } else {
        // Các trạng thái khác: deposit_paid, inprogress, waiting_paid
        statusMessage.style.display = "block";
        statusMessage.className = "status-message deposit";
        statusMessage.innerHTML = "Hợp đồng đã đặt cọc thành công!";
        depositLabel.style.display = "block";
        depositAmountInput.style.display = "block";
        depositAmountInput.value = formatCurrency(depositAmount);
        amountLabel.textContent = "Số Tiền Còn Lại (90%):";
        const remainingAmount = calculateRemainingAmount(contract.value);
        amountInput.value = formatCurrency(remainingAmount);
        paymentMethodSection.style.display = "block";
        paymentButton.style.display = "inline-block";
        paymentButton.textContent = "Thanh Toán Khoản Còn Lại";
        paymentButton.onclick = proceedToPayment;
        localStorage.setItem("paymentAmount", remainingAmount);
        const lastTransaction = JSON.parse(localStorage.getItem("lastTransaction") || "{}");
        if (lastTransaction && lastTransaction.transactionNo) {
            transactionInfo.style.display = "block";
            transactionInfo.innerHTML = `
                        <strong>Thông Tin Giao Dịch:</strong><br>
                        Mã giao dịch: ${lastTransaction.transactionNo}<br>
                        Số tiền: ${formatCurrency(lastTransaction.amount)}<br>
                        Thời gian: ${formatDateTime(lastTransaction.date)}<br>
                        Loại: ${lastTransaction.type === "deposit" ? "Đặt cọc" : "Thanh toán khoản còn lại"}
                    `;
        }
    }
}

function goBack() {
    const urlParams = new URLSearchParams(window.location.search);
    const vnp_ResponseCode = urlParams.get("vnp_ResponseCode");
    const redirectUrl = vnp_ResponseCode === "00" ? "ListContract.html?payment=success" : "ListContract.html";
    window.location.href = redirectUrl;
}

window.onload = async function () {
    await loadContractInfo();
    await processPaymentResult();
};