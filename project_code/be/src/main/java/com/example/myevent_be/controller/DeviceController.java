package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.DeviceRequest;
import com.example.myevent_be.dto.response.DeviceResponse;
import com.example.myevent_be.dto.response.PageResponse;
import com.example.myevent_be.dto.response.ResponseData;
import com.example.myevent_be.dto.response.ResponseError;
import com.example.myevent_be.service.DeviceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@Validated
@Slf4j
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private static final String ERROR_MESSAGE = "errorMessage={}";

    // Lấy danh sách thiết bị
    @GetMapping("/list")
    public ResponseData<PageResponse> getDevices(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @Min(5) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        log.info("Request get devices, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> device = deviceService.getDevices(pageNo, pageSize);
            return new ResponseData<>(HttpStatus.OK.value(), "success", device);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE,e.getMessage(),e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    // Lấy chi tiết thiết bị
    @GetMapping("/{id}")
    public ResponseData<DeviceResponse> getDeviceById(@PathVariable String id) {
        log.info("Request get device detail, deviceId={}", id);

        try {
            DeviceResponse device = deviceService.getDevice(id);
            return new ResponseData<>(HttpStatus.OK.value(), "device", device);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


    @PreAuthorize("hasAuthority('SUPPLIER')")
    @PostMapping(value = "/new")
    public ResponseData<DeviceResponse> createDevice(@Valid @RequestBody DeviceRequest request) {
        log.info("Request add Device, {}",request.getName());

        try {
            DeviceResponse deviceResponse = deviceService.createDevice(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Device added successfully",deviceResponse);
        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Device added fail");
        }
    }

    @PreAuthorize("hasAuthority('SUPPLIER')")
    @PutMapping("/{id}")
    public ResponseData<Void> updateDevice(@PathVariable String id, @Valid @RequestBody DeviceRequest request){
        log.info("Request update DeviceId={}", id);
        try {
            deviceService.updateDevice(request,id);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Device updated successfully");
    } catch (Exception e) {
        log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update device fail");
    }
    }

    // Chỉ Admin mới được xóa thiết bị
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteDevice(@PathVariable String id) {
        log.info("Request delete deviceId={}", id);

        try {
            deviceService.deleteDevice(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Device deleted successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete device fail");
        }
    }
}
