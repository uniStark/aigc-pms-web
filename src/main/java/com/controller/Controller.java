package com.controller;

import com.common.api.Action;
import com.common.api.CommonResult;
import com.model.Entity;
import com.service.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author BeamStark
 * @date 2023-10-30-01:39
 */
@Slf4j
@RestController
@RequestMapping("/aigc")
public class Controller {

    @Resource
    private Service service;

    @Action
    @PostMapping("todo")
    public CommonResult todo(@RequestBody Entity entity) {
        if (entity.getApiKey() == null || entity.getContext() == null) {
            return CommonResult.failed("不能为空");
        }
        entity.setTimeout(entity.getTimeout() * 1000);
        return CommonResult.success(service.todo(entity));
    }

    @Action
    @GetMapping("stop")
    public CommonResult stop() {
        service.stopdo();
        return CommonResult.success("done");
    }

}
