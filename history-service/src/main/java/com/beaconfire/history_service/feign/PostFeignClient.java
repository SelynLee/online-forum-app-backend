package com.beaconfire.history_service.feign;

import com.beaconfire.history_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.beaconfire.history_service.dto.DataResponse;

@FeignClient(name = "posts-service", path = "/posts", configuration = FeignConfig.class)
public interface PostFeignClient {
    @GetMapping("/{postId}")
    DataResponse getPostById(@PathVariable("postId") String postId);
}