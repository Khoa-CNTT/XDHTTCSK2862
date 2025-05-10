
let suppliers = [];
let deviceTypes = [];

function getSupplierName(supplierId) {
    const supplier = suppliers.find(s => s.id === supplierId);
    return supplier ? `${supplier.first_name} ${supplier.last_name}` : "Không xác định";
}

function getDeviceTypeName(typeId) {
    const type = deviceTypes.find(t => t.id === typeId);
    return type ? type.name : "Không xác định";
}

document.addEventListener("DOMContentLoaded", async function () {
    const preloader = document.getElementById("preloader");
    const itemDetailsContainer = document.getElementById("itemDetailsContainer");
    const itemImage = document.getElementById("itemImage");
    const registerButton = document.getElementById("registerButton");

    const urlParams = new URLSearchParams(window.location.search);
    let id = urlParams.get("id");
    const category = urlParams.get("category");

    try {
        id = decodeURIComponent(id).trim().toLowerCase();
        console.log("ID sau khi giải mã:", id);
    } catch (e) {
        console.error("Lỗi giải mã id:", e);
    }

    if (!id || !category) {
        document.getElementById("itemName").textContent = "Lỗi: Thiếu thông tin!";
        document.getElementById("itemDescription").textContent = "Vui lòng quay lại trang danh sách.";
        itemDetailsContainer.innerHTML = "";
        preloader.style.display = "none";
        return;
    }

    try {
        // Lấy danh sách loại thiết bị
        if (category === "device") {
            const deviceTypesResponse = await fetch("http://localhost:8080/event-management/deviceType/list");
            if (!deviceTypesResponse.ok) {
                throw new Error(`Lỗi khi lấy danh sách loại thiết bị: ${deviceTypesResponse.statusText}`);
            }
            deviceTypes = await deviceTypesResponse.json();
        }

        // Xác định endpoint dựa trên category
        let endpoint;
        if (category === "device") {
            endpoint = `http://localhost:8080/event-management/devices/${id}`;
        } else if (category === "services") {
            endpoint = `http://localhost:8080/event-management/services/${id}`;
        } else if (category === "location") {
            endpoint = `http://localhost:8080/event-management/locations/${id}`;
        } else {
            throw new Error("Danh mục không hợp lệ");
        }

        // Lấy chi tiết mục
        const itemResponse = await fetch(endpoint);
        if (!itemResponse.ok) {
            throw new Error(`Lỗi khi lấy dữ liệu mục: ${itemResponse.statusText}`);
        }
        var b = await itemResponse.json();
        const item = b.data;


        if (!item) {
            console.warn(`Không tìm thấy ${category} với id = ${id}`);
            document.getElementById("itemName").textContent = "Không tìm thấy!";
            document.getElementById("itemDescription").textContent = `Không có ${category === "device" ? "thiết bị" : category === "service" ? "dịch vụ" : "địa điểm"} với tên ${id}.`;
            itemImage.style.display = "none";
            registerButton.style.display = "none";
            itemDetailsContainer.innerHTML = "";
        } else {
            const itemData = Array.isArray(item) ? item[0] : item;
            window.selectedItem = { ...itemData, category };
            document.getElementById("itemName").textContent = itemData.name || "Không xác định";
            document.getElementById("itemDescription").textContent = itemData.description || "Không có mô tả";
            itemImage.src = itemData.img || itemData.image || "./assets/img/placeholder.jpg";
            itemImage.style.display = "block";

            const detailsList = document.createElement("ul");
            detailsList.className = "list-group";
            if (category === "device") {
                detailsList.innerHTML = `
                            <li class="list-group-item"><strong>Phí thuê/ngày:</strong> ${(item.hourlyRentalFee || 0).toLocaleString()} VND</li>
                            <li class="list-group-item"><strong>Số lượng:</strong> ${item.quantity || "Không xác định"}</li>
                            <li class="list-group-item"><strong>Loại thiết bị:</strong> ${item.device_type_id}</li>
                            <li class="list-group-item"><strong>Nhà cung cấp:</strong> ${getSupplierName(item.userid)}</li>
                            <li class="list-group-item"><strong>Thành phố:</strong> ${item.place}</li>
                        `;
            } else if (category === "service") {
                detailsList.innerHTML = `
                            <li class="list-group-item"><strong>Lương/ngày:</strong> ${(item.hourly_salary || 0).toLocaleString()} VND</li>
                            <li class="list-group-item"><strong>Số lượng:</strong> ${item.quantity || "Không xác định"}</li>
                            <li class="list-group-item"><strong>Nhà cung cấp:</strong> ${getSupplierName(item.userID)}</li>
                            <li class="list-group-item"><strong>Thành phố:</strong> ${item.place}</li>
                        `;
            } else if (category === "location") {
                detailsList.innerHTML = `
                            <li class="list-group-item"><strong>Phí thuê/ngày:</strong> ${(itemData.hourly_rental_fee || 0).toLocaleString()} VND</li>
                            <li class="list-group-item"><strong>Nhà cung cấp:</strong> ${getSupplierName(itemData.user_id)}</li>
                        `;
            }
            itemDetailsContainer.innerHTML = "";
            itemDetailsContainer.appendChild(detailsList);
            registerButton.style.display = "block";
        }

        if (localStorage.getItem("openContractAfterLogin") === "true") {
            localStorage.removeItem("openContractAfterLogin");
            setTimeout(openContractModal, 0);
        }
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu:", error);
        document.getElementById("itemName").textContent = "Lỗi tải dữ liệu!";
        document.getElementById("itemDescription").textContent = "Vui lòng thử lại sau.";
        itemDetailsContainer.innerHTML = "";
        itemImage.style.display = "none";
        registerButton.style.display = "none";
    } finally {
        preloader.style.display = "none";
    }
});

function checkLoginAndOpenContract() {
    const token = localStorage.getItem("token");
    if (!token) {
        localStorage.setItem("openContractAfterLogin", "true");
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get("id");
        const category = urlParams.get("category");
        localStorage.setItem("redirectAfterLogin", `item_detail.html?id=${encodeURIComponent(id)}&category=${category}`);
        window.location.href = "login.html";
    } else {
        openContractModal();
    }
}

function openContractModal() {
    const iframe = document.getElementById("contractIframe");
    iframe.src = "contract.html";

    const modal = new bootstrap.Modal(document.getElementById("iframeModal"), {});
    modal.show();

    iframe.onload = function () {
        if (window.selectedItem) {
            iframe.contentWindow.postMessage({
                type: "preloadItem",
                item: window.selectedItem
            }, "*");
        }
    };

    window.addEventListener("message", function closeModal(event) {
        if (event.data === "closeIframe") {
            modal.hide();
            window.removeEventListener("message", closeModal);
        }
    }, { once: true });
}