package com.example.myevent_be.controller;


import com.example.myevent_be.dto.request.ServiceRequest;
import com.example.myevent_be.dto.response.PageResponse;
import com.example.myevent_be.dto.response.ResponseData;
import com.example.myevent_be.dto.response.ResponseError;
import com.example.myevent_be.dto.response.ServiceResponse;
import com.example.myevent_be.service.Services;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ServiceController {

    private final Services services;
    private static final String ERROR_MESSAGE = "errorMessage={}";

    // Lấy danh sách dịch vụ
    @GetMapping("/list")
    public ResponseData<PageResponse> getServices(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                 @Min(5) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        log.info("Request get services, pageNo={}, pageSize={}", pageNo, pageSize);

        try {
            PageResponse<?> service = services.getServices(pageNo, pageSize);
            return new ResponseData<>(HttpStatus.OK.value(), "success", service);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE,e.getMessage(),e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    // Lấy chi tiết dịch vụ
    @GetMapping("/{id}")
    public ResponseData<ServiceResponse> getServiceById(@PathVariable String id) {
        log.info("Request get service detail, serviceId={}", id);

        try {
            ServiceResponse service = services.getService(id);
            return new ResponseData<>(HttpStatus.OK.value(), "service", service);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


    @PreAuthorize("hasAuthority('SUPPLIER')")
    @PostMapping(value = "/new")
    public ResponseData<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        log.info("Request add service, {}",request.getName());

        try {
            ServiceResponse response = services.createService(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Service added successfully",response);
        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Service added fail");
        }
    }

    @PreAuthorize("hasAuthority('SUPPLIER')")
    @PutMapping("/{id}")
    public ResponseData<Void> updateService(@PathVariable String id, @Valid @RequestBody ServiceRequest request){
        log.info("Request update ServiceId={}", id);
        try {
            services.updateService(request,id);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Service updated successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update Service fail");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteService(@PathVariable String id) {
        log.info("Request delete ServiceID={}", id);

        try {
            services.deleteService(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Service deleted successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete Service fail");
        }
    }
}
