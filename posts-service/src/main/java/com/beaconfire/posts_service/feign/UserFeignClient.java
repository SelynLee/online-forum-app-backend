package com.beaconfire.posts_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.beaconfire.posts_service.dto.DataResponse;

@FeignClient(name = "USERS-SERVICE")
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    DataResponse getUserById(@PathVariable("id") Integer userId);
    
    @GetMapping("/users/{id}/permissions")
    DataResponse getUserPermissions(@PathVariable("id") Integer userId);
}
