package com.beaconfire.history_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.beaconfire.history_service.dto.DataResponse;

@FeignClient(name = "USERS-SERVICE", path = "/users")
public interface UserFeignClient {
    @GetMapping("/{id}")
    DataResponse getUserById(@PathVariable("id") Integer userId);
}
