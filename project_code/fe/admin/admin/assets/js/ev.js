
var EventAPI = 'http://localhost:3000/event';
var EventTypeAPI = 'http://localhost:3000/event_type';
var DeviceAPI = 'http://localhost:3000/device';
var DeviceTypeAPI = 'http://localhost:3000/device_type';
var ServiceAPI = 'http://localhost:3000/service';
var RentalAPI = 'http://localhost:3000/rental';
var DeviceRental = 'http://localhost:3000/device_rental';
var ServiceRental = 'http://localhost:3000/service_rental';
var Timeline = 'http://localhost:3000/timeline';
var UsersAPI = 'http://localhost:3000/user';
let rentalId;
function start() {
    getData((events, eventTypes, devices, deviceTypes, services, users) => {
        renderEvents(events, eventTypes);
        setupDeviceTable(deviceTypes);
        window.devices = devices;  // Lưu dữ liệu vào biến toàn cục
        window.deviceTypes = deviceTypes;
        window.users = users;  // Lưu lại đúng danh sách user
        window.events = events;
        window.eventTypes = eventTypes;
        populateDeviceTypes(deviceTypes); // Đảm bảo lấy đúng deviceTypes
        // Setup dịch vụ
        window.services = services; // Lưu vào biến toàn cục
        setupServiceTable(services);
        populateService(services);
        if (document.querySelector("#selectEventTypes")) {
            populateEventTypes(eventTypes);
        }
    });
    handleCreateForm();
    //handleCreateDeviceRentalForm();
    if (document.querySelector("#saveEventType")) {
        handleCreateEventType();
    }
    handleAddEventType(); // Thêm xử lý cho nút "+"
    setupDeviceTable();
    setupTimelineTable();
    var editEventId = localStorage.getItem("editEventId");


}
start();
function getData(callback) {
    Promise.all([
        fetch(EventAPI).then(res => res.json()),
        fetch(EventTypeAPI).then(res => res.json()),
        fetch(DeviceAPI).then(res => res.json()),
        fetch(DeviceTypeAPI).then(res => res.json()),
        fetch(ServiceAPI).then(res => res.json()),
        fetch(RentalAPI).then(res => res.json()),
        fetch(DeviceRental).then(res => res.json()),
        fetch(ServiceRental).then(res => res.json()),
        fetch(Timeline).then(res => res.json()),
        fetch(UsersAPI).then(res => res.json()),
    ])
        .then(([events, eventTypes, devices, deviceTypes, services, rental, deviceRental, serviceRental, timeline, users]) => {
            callback(events, eventTypes, devices, deviceTypes, services, rental, deviceRental, serviceRental, timeline, users);
        })
        .catch(error => console.error("Lỗi khi lấy dữ liệu:", error));
}
//_____________________________Event____________________________________//
//Tạo sự kiện mới
function  createEventType(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(EventTypeAPI, options)
        .then(function (respone) {
            respone.json();
        })
        .then(callback);
}
//Xoá event
function handleDeleteEvent(id) {
    var options = {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },

    };
    fetch(EventAPI + '/' + id, options)
        .then(function (respone) {
            return respone.json();
        })
        .then(function () {
            var listEvent = document.querySelector('.list-event-' + id)
            if (listEvent) {
                listEvent.remove();
            }
            alert("Xoá sự kiện thành công!");
        })
        .catch(function () {
            alert("Xoá không thành công!");
        });

}
//Cập nhật event
function handleUpdateEvent(eventId) {
    localStorage.setItem("editEventId", eventId); // Lưu ID vào localStorage
    window.location.href = "form-event.html"; // Chuyển đến form cập nhật
}
//Tạo  sự kiện
function createEvent(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(EventAPI, options)
        .then(function (response) {
            return response.json(); // Trả về dữ liệu JSON từ server
        })
        .then(function (eventResponse) {
            callback(eventResponse); // Truyền dữ liệu (bao gồm event_id) vào callback
        })
        .catch(function (error) {
            console.error("Lỗi khi tạo sự kiện:", error);
        });
}
//Render cho table-event
function renderEvents(events, eventTypes) {
    var listEvenstBlock = document.querySelector('#list-event tbody');
    if (!listEvenstBlock) return;

    // Hủy DataTables nếu đã khởi tạo
    if ($.fn.DataTable.isDataTable('#list-event')) {
        $('#list-event').DataTable().destroy();
    }

    var htmls = events.map(function (event) {
        var eventType = eventTypes.find(type => type.id === event.event_type_id);
        var eventTypeName = eventType ? eventType.name : "Không xác định";
        return `
            <tr class="list-event-${event.id}">
                <td>${event.name}</td>
                <td>${eventTypeName}</td>
                <td style="width: 40%;">${event.description}</td>
                <td>${event.created_at}</td>
                <td class="text-center">
                    <div class="action-dropdown">
                        <button class="btn btn-light action-btn">...</button>
                        <div class="dropdown-content">
                            <button class="dropdown-item delete-btn" data-id="${event.id}">Xoá</button>
                            <button class="dropdown-item update-btn" data-id="${event.id}">Cập nhật</button>
                        </div>
                    </div>
                </td>
            </tr>
        `;
    });

    listEvenstBlock.innerHTML = htmls.join('');

    // Khởi tạo lại DataTables
    var table = $('#list-event').DataTable({
        "order": [[3, "desc"]],
        "language": {
            "search": "Tìm kiếm:",
            "lengthMenu": "Hiển thị _MENU_ sự kiện",
            "info": "Hiển thị _START_ đến _END_ của _TOTAL_ dịch vụ",
            "infoEmpty": "Không có dữ liệu",
            "zeroRecords": "Không tìm thấy kết quả",
            "paginate": {
                "first": "Đầu",
                "last": "Cuối",
                "next": "Tiếp",
                "previous": "Trước"
            }
        }
    });

    // 🛠 Gán sự kiện dùng delegate để hoạt động trên tất cả các trang
    $('#list-event tbody').on('click', '.action-btn', function (event) {
        let dropdown = $(this).next('.dropdown-content');
        $('.dropdown-content').not(dropdown).hide(); // Ẩn các dropdown khác
        dropdown.toggle();
        event.stopPropagation();
    });

    // Xử lý sự kiện cập nhật
    $('#list-event tbody').on('click', '.update-btn', function () {
        let eventId = $(this).data('id');
        handleUpdateEvent(eventId);
    });

    // Xử lý sự kiện xoá
    $('#list-event tbody').on('click', '.delete-btn', function () {
        let eventId = $(this).data('id');
        handleDeleteEvent(eventId);
    });

    // Đóng dropdown khi bấm ra ngoài
    $(document).click(function () {
        $('.dropdown-content').hide();
    });
}

//Tạo mới form event
function handleCreateForm() {
    var createBtn = document.querySelector('#create');
    var editEventId = localStorage.getItem("editEventId");

    if (editEventId) return; // Nếu có ID thì chỉ cập nhật, không tạo mới

    createBtn.onclick = function () {
        var pictureInput = document.querySelector('input[name="picture"]');
        var img = pictureInput.files.length > 0 ? pictureInput.files[0].name : null;
        var name = document.querySelector('input[name="name"]').value;
        var description = document.querySelector('input[name="description"]').value;
        var eventTypeID = document.querySelector('select[name="eventype"]').value;
        var detail = document.querySelector('textarea[name="detail"]').value;

        var eventData = {
            img: img,
            name: name,
            description: description,
            event_type_id: eventTypeID,
            detail: detail,
            created_at: new Date().toISOString().split('T')[0], // YYYY-MM-DD
            updated_at: new Date().toISOString().split('T')[0]
        };

        // Tạo sự kiện
        createEvent(eventData, function (eventResponse) {
            var eventId = eventResponse.id; // Lấy event_id từ phản hồi
            console.log("Event vừa tạo có ID:", eventId);

            // Gọi hàm tạo rental với eventId vừa nhận được
            createRentalWithEventId(eventId);

            // Chuyển hướng sau khi hoàn tất
            // window.location.href = "table-event.html"; // Tạm comment để debug
        });
    };
}
// // Hàm xử lý cập nhật sự kiện
// function loadEditForm(editEventId) {
//     if (!editEventId) return;

//     console.log("Chỉnh sửa sự kiện ID:", editEventId);

//     // Bước 1: Lấy danh sách loại sự kiện
//     fetch(EventTypeAPI)
//         .then(response => response.json())
//         .then(eventTypes => {
//             var selectEventType = document.querySelector('select[name="eventype"]');
//             selectEventType.innerHTML = "";

//             // Thêm option vào select
//             eventTypes.forEach(type => {
//                 var option = document.createElement("option");
//                 option.value = type.id;  // Giá trị là ID
//                 option.textContent = type.name;  // Hiển thị tên loại sự kiện
//                 selectEventType.appendChild(option);
//             });

//             // Bước 2: Lấy thông tin sự kiện cần chỉnh sửa
//             return fetch(`${EventAPI}/${editEventId}`);
//         })
//         .then(response => response.json())
//         .then(event => {
//             document.querySelector('input[name="name"]').value = event.name || "";
//             document.querySelector('input[name="description"]').value = event.description || "";
//             document.querySelector('textarea[name="detail"]').value = event.detail || "";

//             var selectEventType = document.querySelector('select[name="eventype"]');
//             selectEventType.value = event.event_type_id;

//             var inputPicture = document.querySelector('input[name="picture"]');
//             var imagePreview = document.getElementById("image"); // Ảnh hiển thị

//             //  Load ảnh cũ khi chỉnh sửa
//             imagePreview.src = event.img || "assets/img/card.jpg";

//             // Sự kiện chọn ảnh mới
//             inputPicture.addEventListener("change", function (event) {
//                 const file = event.target.files[0];
//                 if (file) {
//                     const reader = new FileReader();
//                     reader.onload = function (e) {
//                         imagePreview.src = e.target.result;
//                     };
//                     reader.readAsDataURL(file);
//                 }
//             });
//             // Bước 3: Tải rental, devicerental, servicerental và timeline
//             return Promise.all([
//                 fetch(`${RentalAPI}?event_id=${editEventId}`).then(res => res.json()),
//                 fetch(`${DeviceRental}?rental_id=${event.rental_id}`).then(res => res.json()),
//                 fetch(`${ServiceRental}?rental_id=${event.rental_id}`).then(res => res.json()),
//                 fetch(`${Timeline}?rental_id=${event.rental_id}`).then(res => res.json())
//             ]);
//         })
//         .then(([rentals, deviceRentals, serviceRentals, timelines]) => {
//             // Cập nhật rental
//             if (rentals.length > 0) {
//                 rentalId = rentals[0].id; // Gán rentalId toàn cục
//                 console.log("Rental ID:", rentalId);
//             } else {
//                 console.warn("Không tìm thấy rental cho event_id:", editEventId);
//             }
//             // Cập nhật devicerental
//             deviceRentals.forEach(deviceRental => {
//                 addDeviceRow(deviceRental.device_id, deviceRental.quantity);
//             });
//             serviceRentals.forEach(serviceRental => {
//                 addServiceRow(serviceRental.service_id, serviceRental.quantity);
//             });
//             timelines.forEach(timeline => {
//                 addTimelineRow(timeline.time_start, timeline.description);
//             });
//             // Cập nhật nút "Cập nhật"
//             document.querySelector("#create").textContent = "Cập nhật";
//             document.querySelector("#create").onclick = function () {
//                 var inputName = document.querySelector('input[name="name"]').value;
//                 var inputDescription = document.querySelector('input[name="description"]').value;
//                 var inputEventTypeID = document.querySelector('select[name="eventype"]').value;
//                 var inputDetail = document.querySelector('textarea[name="detail"]').value;

//                 var img = inputPicture.files.length > 0 ? imagePreview.src : event.img;

//                 var updatedEvent = {
//                     img: img,
//                     name: inputName,
//                     description: inputDescription,
//                     event_type_id: inputEventTypeID,
//                     detail: inputDetail,
//                     created_at: event.created_at,
//                     updated_at: new Date().toISOString().split('T')[0]
//                 };

//                 fetch(`${EventAPI}/${editEventId}`, {
//                     method: 'PUT',
//                     headers: { "Content-Type": "application/json" },
//                     body: JSON.stringify(updatedEvent)
//                 })
//                     .then(response => response.json())
//                     .then(() => {
//                         // Cập nhật rental
//                         updateRental(rentalId, editEventId);

//                         // Cập nhật device_rental, service_rental, timeline
//                         updateDeviceRentals(rentalId, deviceRentals);
//                         updateServiceRentals(rentalId, serviceRentals);
//                         updateTimelines(rentalId, timelines);

//                         console.log("Cập nhật thành công!");
//                         window.location.href = "table-event.html";
//                     })
//                     .catch(error => console.error("Lỗi khi cập nhật sự kiện:", error));
//             };
//         })
//         .catch(error => console.error("Lỗi khi lấy dữ liệu sự kiện:", error));
// }
function loadEditForm(editEventId) {
    if (!editEventId) return;

    console.log("Chỉnh sửa sự kiện ID:", editEventId);
    const inputPicture = document.querySelector('input[name="picture"]');
    const imagePreview = document.getElementById("image");

    // Lấy danh sách loại sự kiện (event types)
    fetch(EventTypeAPI)
        .then(response => response.json())
        .then(eventTypes => {
            var selectEventType = document.querySelector('select[name="eventype"]');
            selectEventType.innerHTML = "";
            eventTypes.forEach(type => {
                var option = document.createElement("option");
                option.value = type.id;
                option.textContent = type.name;
                selectEventType.appendChild(option);
            });

            // Lấy danh sách loại thiết bị (device types)
            return Promise.all([
                fetch(`${EventAPI}/${editEventId}`), // Lấy thông tin sự kiện
                fetch(DeviceTypeAPI).then(res => res.json()) // Lấy danh sách loại thiết bị
            ]);
        })
        .then(([eventResponse, deviceTypes]) => {
            const event = eventResponse.json();
            window.deviceTypes = deviceTypes; // Gán dữ liệu loại thiết bị vào window.deviceTypes

            return event.then(event => {
                document.querySelector('input[name="name"]').value = event.name || "";
                document.querySelector('input[name="description"]').value = event.description || "";
                document.querySelector('textarea[name="detail"]').value = event.detail || "";
                document.querySelector('select[name="eventype"]').value = event.event_type_id;
                imagePreview.src = event.img || "assets/img/card.jpg";

                inputPicture.addEventListener("change", function (event) {
                    const file = event.target.files[0];
                    if (file) {
                        const reader = new FileReader();
                        reader.onload = function (e) {
                            imagePreview.src = e.target.result;
                        };
                        reader.readAsDataURL(file);
                    }
                });

                return Promise.all([
                    fetch(`${RentalAPI}?event_id=${editEventId}`).then(res => res.json()),
                    fetch(`${DeviceAPI}`).then(res => res.json()),
                    fetch(`${ServiceAPI}`).then(res => res.json()),
                    fetch(`${UsersAPI}`).then(res => res.json())
                ]);
            });
        })
        .then(([rentals, devices, services, users]) => {
            if (rentals.length > 0) {
                rentalId = rentals[0].id;
                console.log("Rental ID:", rentalId);
            } else {
                console.warn("Không tìm thấy rental cho event_id:", editEventId);
            }

            window.devices = devices;
            window.services = services;
            window.users = users;

            return Promise.all([
                Promise.resolve(rentals),
                fetch(`${DeviceRental}?rental_id=${rentalId}`).then(res => res.json()),
                fetch(`${ServiceRental}?rental_id=${rentalId}`).then(res => res.json()),
                fetch(`${Timeline}?rental_id=${rentalId}`).then(res => res.json()),
                Promise.resolve(devices),
                Promise.resolve(services),
                Promise.resolve(users)
            ]);
        })
        .then(([rentals, deviceRentals, serviceRentals, timelines, devices, services, users]) => {
            document.querySelector("#deviceTable tbody").innerHTML = "";
            document.querySelector("#serviceTable tbody").innerHTML = "";
            document.querySelector("#timeTable tbody").innerHTML = "";

            deviceRentals.forEach(deviceRental => {
                const device = devices.find(d => d.id === deviceRental.device_id);
                if (device) {
                    addDeviceRow(deviceRental.device_id, deviceRental.quantity, device.device_types_id, device.hourly_rental_fee, device.user_id);
                }
            });

            serviceRentals.forEach(serviceRental => {
                const service = services.find(s => s.id === serviceRental.service_id);
                if (service) {
                    addServiceRow(serviceRental.service_id, serviceRental.quantity, service.hourly_salary, service.user_id);
                }
            });

            timelines.forEach(timeline => {
                addTimelineRow(timeline.time_start, timeline.description);
            });

            document.querySelector("#create").textContent = "Cập nhật";
            document.querySelector("#create").onclick = function () {
                const inputPicture = document.querySelector('input[name="picture"]');
                const inputName = document.querySelector('input[name="name"]').value;
                const inputDescription = document.querySelector('input[name="description"]').value;
                const inputEventTypeID = document.querySelector('select[name="eventype"]').value;
                const inputDetail = document.querySelector('textarea[name="detail"]').value;
                const img = inputPicture.files.length > 0 ? imagePreview.src : event.img;

                const updatedEvent = {
                    img: img,
                    name: inputName,
                    description: inputDescription,
                    event_type_id: inputEventTypeID,
                    detail: inputDetail,
                    created_at: event.created_at,
                    updated_at: new Date().toISOString().split('T')[0]
                };

                fetch(`${EventAPI}/${editEventId}`, {
                    method: 'PUT',
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedEvent)
                })
                    .then(response => response.json())
                    .then(() => {
                        updateRental(rentalId, editEventId);
                        updateDeviceRentals(rentalId, deviceRentals);
                        updateServiceRentals(rentalId, serviceRentals);
                        updateTimelines(rentalId, timelines);
                        console.log("Cập nhật thành công!");
                        window.location.href = "table-event.html";
                    })
                    .catch(error => console.error("Lỗi khi cập nhật sự kiện:", error));
            };
        })
        .catch(error => console.error("Lỗi khi lấy dữ liệu sự kiện:", error));
}
//_________________Updated device, service, timelien_______//
// Hàm cập nhật rental
function updateRental(rentalId, eventId) {
    const totalPrice = calculateTotalPrice();
    const user = JSON.parse(localStorage.getItem("user"));
    const userId = user ? user.user_id : null; // user.id

    if (!rentalId) {
        console.error("Không có rentalId để cập nhật!");
        return;
    }

    const updatedRental = {
        custom_location: "Địa điểm tùy chỉnh",
        rental_start_time: new Date().toISOString(),
        rental_end_time: new Date(Date.now() + 86400000).toISOString(),
        total_price: totalPrice,
        event_id: eventId,
        user_id: userId,
        updated_at: new Date().toISOString()
    };

    fetch(`${RentalAPI}/${rentalId}`, {
        method: 'PUT',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedRental)
    })
        .then(response => response.json())
        .catch(error => console.error("Lỗi khi cập nhật rental:", error));
}

// Hàm cập nhật device_rental
function updateDeviceRentals(rentalId, oldDeviceRentals) {
    const currentRows = document.querySelectorAll("#deviceTable tbody tr");
    const currentDeviceRentals = Array.from(currentRows).map(row => ({
        device_id: row.querySelector('select[name="devicename"]').value,
        quantity: row.querySelector('input[name="quantitydevice"]').value
    }));

    // Xóa các bản ghi không còn trong form
    oldDeviceRentals.forEach(old => {
        if (!currentDeviceRentals.some(current => current.device_id === old.device_id)) {
            fetch(`${DeviceRental}/${old.id}`, {
                method: 'DELETE',
                headers: { "Content-Type": "application/json" }
            })
                .catch(error => console.error("Lỗi khi xóa device_rental:", error));
        }
    });

    // Thêm hoặc cập nhật
    currentDeviceRentals.forEach(current => {
        const oldRecord = oldDeviceRentals.find(old => old.device_id === current.device_id);
        if (oldRecord) {
            if (oldRecord.quantity !== current.quantity) {
                fetch(`${DeviceRental}/${oldRecord.id}`, {
                    method: 'PUT',
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        rental_id: rentalId,
                        device_id: current.device_id,
                        quantity: current.quantity,
                        updated_at: new Date().toISOString()
                    })
                })
                    .catch(error => console.error("Lỗi khi cập nhật device_rental:", error));
            }
        } else {
            createDeviceRental({
                rental_id: rentalId,
                device_id: current.device_id,
                quantity: current.quantity,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            }, () => console.log("Thêm device_rental thành công"));
        }
    });
}

// Hàm cập nhật service_rental
function updateServiceRentals(rentalId, oldServiceRentals) {
    const currentRows = document.querySelectorAll("#serviceTable tbody tr");
    const currentServiceRentals = Array.from(currentRows).map(row => ({
        service_id: row.querySelector('select[name="servicetype"]').value,
        quantity: row.querySelector('input[name="quantity"]').value
    }));

    // Xóa các bản ghi không còn trong form
    oldServiceRentals.forEach(old => {
        if (!currentServiceRentals.some(current => current.service_id === old.service_id)) {
            fetch(`${ServiceRental}/${old.id}`, {
                method: 'DELETE',
                headers: { "Content-Type": "application/json" }
            })
                .catch(error => console.error("Lỗi khi xóa service_rental:", error));
        }
    });

    // Thêm hoặc cập nhật
    currentServiceRentals.forEach(current => {
        const oldRecord = oldServiceRentals.find(old => old.service_id === current.service_id);
        if (oldRecord) {
            if (oldRecord.quantity !== current.quantity) {
                fetch(`${ServiceRental}/${oldRecord.id}`, {
                    method: 'PUT',
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        rental_id: rentalId,
                        service_id: current.service_id,
                        quantity: current.quantity,
                        updated_at: new Date().toISOString()
                    })
                })
                    .catch(error => console.error("Lỗi khi cập nhật service_rental:", error));
            }
        } else {
            createServiceRental({
                rental_id: rentalId,
                service_id: current.service_id,
                quantity: current.quantity,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            }, () => console.log("Thêm service_rental thành công"));
        }
    });
}

// Hàm cập nhật timeline
function updateTimelines(rentalId, oldTimelines) {
    const currentRows = document.querySelectorAll("#timeTable tbody tr");
    const currentTimelines = Array.from(currentRows).map(row => ({
        time_start: row.querySelector('input[name="timeline"]').value,
        description: row.querySelector('textarea[name="descriptiontime"]').value
    }));

    // Xóa các bản ghi không còn trong form
    oldTimelines.forEach(old => {
        if (!currentTimelines.some(current => current.time_start === old.time_start && current.description === old.description)) {
            fetch(`${Timeline}/${old.id}`, {
                method: 'DELETE',
                headers: { "Content-Type": "application/json" }
            })
                .catch(error => console.error("Lỗi khi xóa timeline:", error));
        }
    });

    // Thêm hoặc cập nhật
    currentTimelines.forEach(current => {
        const oldRecord = oldTimelines.find(old => old.time_start === current.time_start && old.description === current.description);
        if (!oldRecord) {
            createTimeline({
                rental_id: rentalId,
                time_start: current.time_start,
                description: current.description,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            }, () => console.log("Thêm timeline thành công"));
        }
    });
}
//_________________________________end update device, service, timeline_________________________//
// Hàm thêm dòng thiết bị
function addDeviceRow(deviceId, quantity, deviceTypeId, price, userId) {
    const tbody = document.querySelector("#deviceTable tbody");
    const newRow = document.createElement("tr");
    newRow.innerHTML = `
        <td>
            <select class="form-select w-auto" name="devicetype"></select>
        </td>
        <td>
            <select class="form-select" name="devicename"></select>
        </td>
        <td>
            <select class="form-select" name="namesuplier"></select>
        </td>
        <td><input type="number" class="form-control" name="pricedevice" value="${price || 0}" min="0" step="1000" readonly></td>
        <td><input type="number" class="form-control" value="${quantity}" min="1" name="quantitydevice"></td>
        <td><input type="text" class="form-control" readonly name="totalmoneydevice"></td>
        <td class="text-center">
            <button class="btn btn-outline-danger remove-row">🗑</button>
        </td>
    `;
    tbody.appendChild(newRow);
    // Cập nhật giá và tổng tiền
    //updateDeviceOptions(newRow.querySelector('select[name="devicetype"]').value, newRow);
    // Populate danh sách loại thiết bị và chọn giá trị hiện tại
    populateDeviceTypes(window.deviceTypes, newRow);
    const deviceTypeSelect = newRow.querySelector('select[name="devicetype"]');
    deviceTypeSelect.value = deviceTypeId; // Đặt giá trị hiện tại

    // Populate danh sách thiết bị theo loại và chọn giá trị hiện tại
    updateDeviceOptions(deviceTypeId, newRow);
    newRow.querySelector('select[name="devicename"]').value = deviceId;

    // Populate danh sách nhà cung cấp và chọn giá trị hiện tại
    const supplierSelect = newRow.querySelector('select[name="namesuplier"]');
    supplierSelect.innerHTML = window.users.map(user =>
        `<option value="${user.id}" ${user.id === userId ? 'selected' : ''}>${user.last_name} ${user.first_name}</option>`
    ).join('');

    // Tính tổng tiền
    updateTotalPrice(newRow);

    // Gán sự kiện thay đổi
    newRow.querySelector('select[name="devicetype"]').addEventListener("change", function () {
        updateDeviceOptions(this.value, newRow);
    });
    newRow.querySelector('select[name="devicename"]').addEventListener("change", handleDeviceChange);
    newRow.querySelector('input[name="quantitydevice"]').addEventListener("input", () => updateTotalPrice(newRow));
}

// Hàm thêm dòng dịch vụ
function addServiceRow(serviceId, quantity, price, userId) {
    const tbody = document.querySelector("#serviceTable tbody");
    const newRow = document.createElement("tr");
    newRow.innerHTML = `
        <td>
            <select class="form-select w-auto" name="servicetype"></select>
        </td>
        <td>
            <select class="form-select w-auto" name="namesuplier"></select>
        </td>
        <td><input type="number" class="form-control" name="price" value="${price || 0}" min="0" step="1000" readonly></td>
        <td><input type="number" class="form-control" value="${quantity}" min="1" name="quantity"></td>
        <td><input type="text" class="form-control" readonly name="totalmoney"></td>
        <td class="text-center">
            <button class="btn btn-outline-danger remove-row">🗑</button>
        </td>
    `;
    tbody.appendChild(newRow);
    // Cập nhật giá và tổng tiền
    // Populate danh sách dịch vụ và chọn giá trị hiện tại
    populateService(window.services, newRow);
    newRow.querySelector('select[name="servicetype"]').value = serviceId;

    // Populate danh sách nhà cung cấp và chọn giá trị hiện tại
    const supplierSelect = newRow.querySelector('select[name="namesuplier"]');
    supplierSelect.innerHTML = window.users.map(user =>
        `<option value="${user.id}" ${user.id === userId ? 'selected' : ''}>${user.last_name} ${user.first_name}</option>`
    ).join('');

    // Tính tổng tiền
    updateServiceTotal(newRow);

    // Gán sự kiện thay đổi
    newRow.querySelector('select[name="servicetype"]').addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];
        newRow.querySelector('input[name="price"]').value = selectedOption.dataset.price || "";
        updateServiceTotal(newRow);
        const supplierSelect = newRow.querySelector('select[name="namesuplier"]');
        const user = window.users.find(u => u.id === selectedOption.dataset.userId);
        supplierSelect.innerHTML = user
            ? `<option value="${user.id}">${user.last_name} ${user.first_name}</option>`
            : `<option value="">Không xác định</option>`;
    });
    newRow.querySelector('input[name="quantity"]').addEventListener("input", () => updateServiceTotal(newRow));
}

// Hàm thêm dòng timeline
function addTimelineRow(timeStart, description) {
    const tbody = document.querySelector("#timeTable tbody");
    const newRow = document.createElement("tr");
    newRow.innerHTML = `
        <td><input type="datetime-local" class="form-control" name="timeline" value="${timeStart}"></td>
        <td><textarea class="form-control" name="descriptiontime" style="min-width: 500px">${description}</textarea></td>
        <td class="text-center">
            <button class="btn btn-outline-danger remove-row">🗑</button>
        </td>
    `;
    tbody.appendChild(newRow);
}
//-----------------
document.addEventListener("DOMContentLoaded", function () {
    handleCreateEventType();
});

function handleCreateEventType() {
    var createBtn = document.querySelector("#saveEventType");

    if (!createBtn) {
        console.warn(" Cảnh báo: #saveEventType không tồn tại trong DOM.");
        return;
    }

    createBtn.onclick = function () {
        var eventTypeName = document.querySelector("#newEventTypeInput").value;

        if (!eventTypeName.trim()) {
            alert("Vui lòng nhập loại sự kiện!");
            return;
        }

        var Data = {
            name: eventTypeName,
            created_at: new Date().toISOString().split("T")[0],
            updated_at: new Date().toISOString().split("T")[0]
        };

        createEventType(Data, function (newEventType) {
            getData((events, eventTypes) => {
                populateEventTypes(eventTypes);
            });

            var modalElement = document.getElementById("eventTypeModal");
            if (modalElement) {
                var modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
                modal.hide();
            }
        });
    };
}

//Su kien render ra eventtype
function populateEventTypes(eventTypes) {
    var select = document.querySelector('#selectEventTypes');
    select.innerHTML = `<option value="">Chọn một tùy chọn</option>`; // Xóa tùy chọn cũ

    // Kiểm tra nếu eventTypes không phải mảng, ta chuyển thành mảng
    var eventList = Array.isArray(eventTypes) ? eventTypes : [eventTypes];

    eventList.forEach(type => {
        var option = document.createElement('option');
        option.value = type.id;  // Lưu ID
        option.textContent = type.name; // Hiển thị tên
        select.appendChild(option);
    });
}
//Sự kiện thêm loại sự kiện qua pop up
function handleAddEventType() {
    document.addEventListener("DOMContentLoaded", function () {
        var modalElement = document.getElementById("eventTypeModal");
        var modal = new bootstrap.Modal(modalElement);

        document.querySelector("#addEventType").addEventListener("click", function () {
            modal.show();
        });

        document.querySelector("#saveEventType").addEventListener("click", function () {
            var newEventType = document.querySelector("#newEventTypeInput").value;
            if (newEventType) {
                var select = document.querySelector('select[name="eventype"]');
                var option = document.createElement("option");
                option.value = newEventType.toLowerCase().replace(/\s+/g, "-");
                option.textContent = newEventType;
                select.appendChild(option);
                select.value = option.value;

                modal.hide();
            }
        });

        // Khi modal đóng, đảm bảo xóa backdrop và reset trạng thái
        modalElement.addEventListener("hidden.bs.modal", function () {
            document.getElementById("newEventTypeInput").value = ""; // Reset input
            document.querySelectorAll(".modal-backdrop").forEach(el => el.remove()); // Xóa backdrop thừa
            document.body.classList.remove("modal-open"); // Loại bỏ class khóa cuộn trang
            document.body.style.overflow = ""; // Khôi phục cuộn trang
        });
    });
}
document.getElementById("inputImage").addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById("image").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});
//____________________________________End Event____________//
//___________________________________DEVICE_______________________________________//
//sự kiện thêm dòng tr khi nhân button thêm thiết bị 
function setupDeviceTable(deviceTypes) {
    const addButton = document.querySelector("#buttonAddDevice");
    const tbody = document.querySelector("#deviceTable tbody");

    if (!addButton || !tbody) return;

    addButton.onclick = function () {
        const newRow = document.createElement("tr");

        newRow.innerHTML = `
            <td>
                <select class="form-select w-auto " name="devicetype">
                    <option value="">Chọn loại thiết bị</option>
                </select>
            </td>
            <td>
                <select class="form-select "style="width: 150px;" name="devicename">
                    <option value="">Chọn thiết bị</option>
                </select>
            </td>
            <td><select class="form-select "  style="width: 170px"; name="namesuplier">
                          <option value="">Chọn tên</option>
            </select></td>
            <td><input type="number" class="form-control" name="pricedevice" min="0" step="1000" readonly></td>
            <td><input type="number" class="form-control"value="1" min="1" name="quantitydevice"></td>
            <td><input type="text" class="form-control" readonly name="totalmoneydevice"></td>
            <td class="text-center">
                <button class="btn btn-outline-danger remove-row">🗑</button>
            </td>
        `;

        tbody.appendChild(newRow);

        // Cập nhật danh sách loại thiết bị
        populateDeviceTypes(deviceTypes, newRow);

        // Gán sự kiện cập nhật tổng tiền cho dòng mới
        newRow.querySelector('input[name="pricedevice"]').addEventListener("input", () => updateTotalPrice(newRow));
        newRow.querySelector('input[name="quantitydevice"]').addEventListener("input", () => {
            const quantity = parseInt(newRow.querySelector('input[name="quantitydevice"]').value);
            const deviceId = newRow.querySelector('select[name="devicename"]').value;
            const availableQuantity = getAvailableQuantity(deviceId); // Lấy số lượng có sẵn

            if (quantity > availableQuantity) {
                alert(`Không đủ số lượng thiết bị. Số lượng có sẵn: ${availableQuantity}`);
                newRow.querySelector('input[name="quantitydevice"]').value = availableQuantity; // Đặt lại số lượng
            } else {
                updateTotalPrice(newRow);
            }
        });

        // Gán sự kiện chọn thiết bị để cập nhật nhà cung cấp
        const deviceSelect = newRow.querySelector('select[name="devicename"]');
        if (deviceSelect) {
            deviceSelect.addEventListener("change", function (event) {
                handleDeviceChange(event); // Gọi hàm cập nhật nhà cung cấp
            });
        }
    };

    // Sự kiện xóa dòng
    tbody.addEventListener("click", function (event) {
        if (event.target.classList.contains("remove-row")) {
            event.target.closest("tr").remove();
        }
    });
}
// Hàm lấy số lượng thiết bị có sẵn
function getAvailableQuantity(deviceId) {
    const device = window.devices.find(device => device.id === deviceId);
    return device ? device.quantity : 0; // Trả về số lượng có sẵn hoặc 0 nếu không tìm thấy
}

// Hàm cập nhật danh sách loại thiết bị và gắn sự kiện chọn thiết bị
function populateDeviceTypes(deviceTypes, row = document) {
    const selectElements = row.querySelectorAll('select[name="devicetype"]');
    if (!selectElements.length) return;

    const options = deviceTypes.map(type => `<option value="${type.id}">${type.name}</option>`).join("");

    selectElements.forEach(select => {
        select.innerHTML = `<option value="">Chọn loại thiết bị</option>` + options;
        select.addEventListener("change", function () {
            const row = this.closest("tr");
            updateDeviceOptions(this.value, row);
        });
    });
}


// Hàm cập nhật danh sách thiết bị theo loại
function updateDeviceOptions(typeId, row) {
    const deviceSelect = row.querySelector('select[name="devicename"]');
    const supplierSelect = row.querySelector('select[name="namesuplier"]');

    deviceSelect.innerHTML = `<option value="">Chọn thiết bị</option>`;
    supplierSelect.innerHTML = `<option value="">Chọn tên</option>`;

    const filteredDevices = window.devices.filter(device => device.device_types_id == typeId);

    if (!filteredDevices.length) {
        deviceSelect.innerHTML = `<option value="">Không có thiết bị</option>`;
        return;
    }

    deviceSelect.innerHTML += filteredDevices.map(device =>
        `<option value="${device.id}" data-user-id="${device.user_id}" data-price="${device.hourly_rental_fee}">
            ${device.name}
        </option>`).join("");

    deviceSelect.onchange = function () {
        handleDeviceChange(row, deviceSelect);
    };
}


// Hàm xử lý khi chọn thiết bị
function handleDeviceChange(event) {
    const deviceSelect = event.target;
    if (!deviceSelect) return;

    const row = deviceSelect.closest("tr");
    if (!row) return;

    const selectedDevice = deviceSelect.options[deviceSelect.selectedIndex];
    if (!selectedDevice) return;

    const priceInput = row.querySelector('input[name="pricedevice"]');
    const supplierSelect = row.querySelector('select[name="namesuplier"]');

    if (!priceInput || !supplierSelect) return;

    priceInput.value = selectedDevice.dataset.price || "";
    updateTotalPrice(row);

    // Cập nhật nhà cung cấp
    const userId = selectedDevice.dataset.userId;
    const user = window.users.find(user => user.id === userId);

    supplierSelect.innerHTML = user
        ? `<option value="${user.id}">${user.last_name} ${user.first_name}</option>`
        : `<option value="">Không xác định</option>`;
}


// Gán sự kiện một cách an toàn
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('select[name="devicename"]').forEach(select => {
        select.addEventListener("change", handleDeviceChange);
    });
});

// Hàm tính tổng tiền
function updateTotalPrice(row) {
    let priceInput = row.querySelector('input[name="pricedevice"]');
    let quantityInput = row.querySelector('input[name="quantitydevice"]');
    let totalInput = row.querySelector('input[name="totalmoneydevice"]');

    let price = parseFloat(priceInput.value) || 0;
    let quantity = parseInt(quantityInput.value) || 0;
    let total = price * quantity;

    totalInput.value = total.toLocaleString("vi-VN") + " VND"; // Định dạng tiền VND
}

// Chạy setup khi trang load
document.addEventListener("DOMContentLoaded", function () {
    fetch(DeviceTypeAPI)
        .then(response => response.json())
        .then(deviceTypes => {
            fetch(DeviceAPI)
                .then(response => response.json())
                .then(devices => {
                    window.deviceTypes = deviceTypes;
                    window.devices = devices;

                    setupDeviceTable(deviceTypes);
                    populateDeviceTypes(deviceTypes);
                });
        });
    fetch(UsersAPI)
        .then(response => response.json())
        .then(users => {
            window.users = users; // Cập nhật danh sách user đúng
            console.log("Updated users:", users);
        });

    // Gắn sự kiện tính tổng tiền cho dòng ban đầu
    document.querySelectorAll("#deviceTable tbody tr").forEach(row => {
        row.querySelector('input[name="pricedevice"]').addEventListener("input", () => updateTotalPrice(row));
        row.querySelector('input[name="quantitydevice"]').addEventListener("input", () => {
            const quantity = parseInt(row.querySelector('input[name="quantitydevice"]').value);
            const deviceId = row.querySelector('select[name="devicename"]').value;
            const availableQuantity = getAvailableQuantity(deviceId); // Lấy số lượng có sẵn

            if (quantity > availableQuantity) {
                alert(`Không đủ số lượng thiết bị. Số lượng có sẵn: ${availableQuantity}`);
                row.querySelector('input[name="quantitydevice"]').value = availableQuantity; // Đặt lại số lượng
            } else {
                updateTotalPrice(row);
            }
        });
    });
});
//___________________________________End DEVICE_______________________________________//

//___________________________________SERVICE_______________________________________//
function setupServiceTable(services) {
    const addButton = document.querySelector("#buttonAddService");
    const tbody = document.querySelector("#serviceTable tbody");

    if (!addButton || !tbody) return;

    function handleRowEvents(row) {
        const serviceSelect = row.querySelector('select[name="servicetype"]');
        const priceInput = row.querySelector('input[name="price"]');
        const quantityInput = row.querySelector('input[name="quantity"]');

        // Gán danh sách dịch vụ vào select
        populateService(services, row);

        // Xử lý chọn dịch vụ
        serviceSelect.addEventListener("change", function () {
            const selectedOption = this.options[this.selectedIndex];
            priceInput.value = selectedOption.dataset.price || "";
            updateServiceTotal(row);

            // Cập nhật nhà cung cấp
            const supplierSelect = row.querySelector('select[name="namesuplier"]');
            const userId = selectedOption.dataset.userId;
            const user = window.users.find(user => user.id === userId);

            supplierSelect.innerHTML = user
                ? `<option value="${user.id}">${user.last_name} ${user.first_name}</option>`
                : `<option value="">Không xác định</option>`;
        });

        // Gán sự kiện thay đổi số lượng
        quantityInput.addEventListener("input", () => {
            const quantity = parseInt(quantityInput.value);
            const serviceId = serviceSelect.value;
            const availableQuantity = getAvailableServiceQuantity(serviceId); // Lấy số lượng có sẵn

            if (quantity > availableQuantity) {
                alert(`Không đủ số lượng dịch vụ. Số lượng có sẵn: ${availableQuantity}`);
                quantityInput.value = availableQuantity; // Đặt lại số lượng
            } else {
                updateServiceTotal(row);
            }
        });
    }

    function addServiceRow() {
        const newRow = document.createElement("tr");

        newRow.innerHTML = `
            <td>
                <select class="form-select w-auto" name="servicetype">
                    <option value="">Chọn dịch vụ</option>
                </select>
            </td>
          <td><select class="form-select w-auto" name="namesuplier">
                            <option value="">Chọn tên</option>
            </select></td>
            <td><input type="number" class="form-control" name="price" min="0" step="1000" readonly></td>
            <td><input type="number" class="form-control" value="1" min="1" name="quantity"></td>
            <td><input type="text" class="form-control" readonly name="totalmoney"></td>
            <td class="text-center">
                <button class="btn btn-outline-danger remove-row">🗑</button>
            </td>
        `;

        tbody.appendChild(newRow);
        handleRowEvents(newRow);
    }

    // Xử lý sự kiện khi nhấn nút thêm
    addButton.onclick = addServiceRow;

    // Xóa dòng khi nhấn nút 🗑
    tbody.addEventListener("click", function (event) {
        if (event.target.classList.contains("remove-row")) {
            event.target.closest("tr").remove();
        }
    });

    // Xử lý dòng đầu tiên (nếu có)
    if (tbody.children.length > 0) {
        handleRowEvents(tbody.children[0]);
    }
}
// Hàm lấy số lượng dịch vụ có sẵn
function getAvailableServiceQuantity(serviceId) {
    const service = window.services.find(service => service.id === serviceId);
    return service ? service.quantity : 0; // Trả về số lượng có sẵn hoặc 0 nếu không tìm thấy
}


// Hàm gán danh sách dịch vụ cho select
function populateService(services, row = document) {
    const selectElements = row.querySelectorAll('select[name="servicetype"]');

    if (!selectElements.length) return;

    const options = services.map(service =>
        `<option value="${service.id}" data-price="${service.hourly_salary}" data-user-id="${service.user_id}">
            ${service.name}
        </option>`
    ).join("");

    selectElements.forEach(select => {
        select.innerHTML = `<option value="">Chọn dịch vụ</option>` + options;
    });
}


// Hàm tính tổng tiền
function updateServiceTotal(row) {
    let priceInput = row.querySelector('input[name="price"]');
    let quantityInput = row.querySelector('input[name="quantity"]');
    let totalInput = row.querySelector('input[name="totalmoney"]');

    let price = parseFloat(priceInput.value) || 0;
    let quantity = parseInt(quantityInput.value) || 0;
    let total = price * quantity;

    totalInput.value = total.toLocaleString("vi-VN") + " VND";
}
//___________________________________End SERVICE_______________________________________//

//___________________________________Timeline_______________________________________//
//Sự kiên thêm dòng tr khi nhấn button thêm timeline
function setupTimelineTable() {
    const addButton = document.querySelector("#buttonAddTime");
    const tbody = document.querySelector("#timeTable tbody");

    if (!addButton || !tbody) return;

    addButton.addEventListener("click", function () {
        const newRow = document.createElement("tr");
        newRow.innerHTML = `
            <td><input type="datetime-local" class="form-control" name="timeline"></td>
            <td><textarea class="form-control" name="descriptiontime" style="min-width: 500px"></textarea></td>
            <td class="text-center">
                <button class="btn btn-outline-danger remove-row">🗑</button>
            </td>
        `;
        tbody.appendChild(newRow);
    });

    tbody.addEventListener("click", function (event) {
        if (event.target.classList.contains("remove-row")) {
            event.target.closest("tr").remove();
        }
    });
}

//___________________________________End Timeline_______________________________________//
//_______xử lý rental, devicerental, servicerental, timeline_____________//
// Hàm xử lý khi nhấn nút "Lưu"
function createRentalWithEventId(eventId) {
    const totalPrice = calculateTotalPrice();
    const user = JSON.parse(localStorage.getItem("user"));
    const userId = user ? user.user_id : null; //const userId = user ? user.id : null;  (nếu dùng token login kh connect be)

    if (!userId) {
        toastr.error("Bạn cần đăng nhập để thực hiện hành động này!", "Lỗi");
        return;
    }

    console.log("ID sự kiện đang sử dụng:", eventId);

    const rentalData = {
        custom_location: "Địa điểm tùy chỉnh",
        rental_start_time: new Date().toISOString(),
        rental_end_time: new Date(Date.now() + 86400000).toISOString(),
        total_price: totalPrice,
        event_id: eventId,
        user_id: userId,
        updated_at: new Date().toISOString()
    };

    createRental(rentalData, function (rentalResponse) {
        const newRentalId = rentalResponse.id;
        handleDeviceRentals(newRentalId);
        handleServiceRentals(newRentalId);
        handleTimelines(newRentalId);
        window.location.href = "table-event.html";
    });
}

// Gán sự kiện cho nút "Lưu"
document.getElementById("create").addEventListener("click", function () {
    var editEventId = localStorage.getItem("editEventId");

    if (editEventId) {
        // Trường hợp cập nhật: Gọi loadEditForm hoặc xử lý trực tiếp trong onclick
        loadEditForm(editEventId); // Gọi lại để đảm bảo logic cập nhật chạy
    } else {
        // Trường hợp tạo mới: Logic đã được xử lý trong handleCreateForm
    }
});
// //cập nhật sự kiện trong form-event
document.addEventListener("DOMContentLoaded", function () {
    var editEventId = localStorage.getItem("editEventId");

    if (editEventId) {
        loadEditForm(editEventId);
    } else {
        handleCreateForm();
    }
});
// Hàm tính tổng giá trị từ thiết bị và dịch vụ
function calculateTotalPrice() {
    let total = 0;

    // Tính tổng từ bảng thiết bị
    const deviceRows = document.querySelectorAll("#deviceTable tbody tr");
    deviceRows.forEach(row => {
        const totalMoneyInput = row.querySelector('input[name="totalmoneydevice"]');
        const totalMoney = parseFloat(totalMoneyInput.value.replace(/[^0-9.-]+/g, "")) || 0; // Chuyển đổi sang số
        total += totalMoney;
    });

    // Tính tổng từ bảng dịch vụ
    const serviceRows = document.querySelectorAll("#serviceTable tbody tr");
    serviceRows.forEach(row => {
        const totalMoneyInput = row.querySelector('input[name="totalmoney"]');
        const totalMoney = parseFloat(totalMoneyInput.value.replace(/[^0-9.-]+/g, "")) || 0; // Chuyển đổi sang số
        total += totalMoney;
    });

    return total;
}

// Hàm tạo rental
function createRental(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(RentalAPI, options)
        .then(response => response.json())
        .then(callback)
        .catch(error => console.error("Lỗi khi tạo rental:", error));
}

// Hàm xử lý tạo device rental
function handleDeviceRentals(rentalId) {
    const deviceRows = document.querySelectorAll("#deviceTable tbody tr");
    deviceRows.forEach(row => {
        const deviceId = row.querySelector('select[name="devicename"]').value;
        const quantity = row.querySelector('input[name="quantitydevice"]').value;

        if (deviceId && quantity) {
            const deviceRentalData = {
                rental_id: rentalId,
                device_id: deviceId,
                quantity: quantity,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            };

            createDeviceRental(deviceRentalData, function () {
                console.log("Device rental created successfully");
            });
        }
    });
}

// Hàm xử lý tạo service rental
function handleServiceRentals(rentalId) {
    const serviceRows = document.querySelectorAll("#serviceTable tbody tr");
    serviceRows.forEach(row => {
        const serviceId = row.querySelector('select[name="servicetype"]').value; // Tên trường đúng
        const quantity = row.querySelector('input[name="quantity"]').value; // Tên trường đúng

        if (serviceId && quantity) {
            const serviceRentalData = {
                rental_id: rentalId,
                service_id: serviceId,
                quantity: quantity,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            };

            createServiceRental(serviceRentalData, function () {
                console.log("Service rental created successfully");
            });
        } else {
            console.warn("Service ID hoặc quantity không hợp lệ:", { serviceId, quantity });
        }
    });
}

// Hàm xử lý tạo timeline
function handleTimelines(rentalId) {
    const timelineRows = document.querySelectorAll("#timeTable tbody tr");
    timelineRows.forEach(row => {
        const timeline = row.querySelector('input[name="timeline"]').value;
        const description = row.querySelector('textarea[name="descriptiontime"]').value;

        if (timeline && description) {
            const timelineData = {
                rental_id: rentalId,
                time_start: timeline,
                description: description,
                created_at: new Date().toISOString(),
                updated_at: new Date().toISOString()
            };

            createTimeline(timelineData, function () {
                console.log("Timeline created successfully");
            });
        }
    });
}

// Hàm tạo device rental
function createDeviceRental(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(DeviceRental, options)
        .then(response => {
            if (!response.ok) {
                throw new Error("Lỗi khi tạo device rental: " + response.statusText);
            }
            return response.json();
        })
        .then(callback)
        .catch(error => console.error(error));
}

function createServiceRental(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(ServiceRental, options)
        .then(response => {
            if (!response.ok) {
                throw new Error("Lỗi khi tạo service rental: " + response.statusText);
            }
            return response.json();
        })
        .then(callback)
        .catch(error => console.error(error));
}

// Hàm tạo timeline
function createTimeline(data, callback) {
    var options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    };
    fetch(Timeline, options)
        .then(response => response.json())
        .then(callback)
        .catch(error => console.error("Lỗi khi tạo timeline:", error));
}