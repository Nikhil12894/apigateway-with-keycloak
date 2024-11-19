package com.nk.admin_service.controller;

import com.nk.base.dto.ResponseDto;
import com.nk.base.exception.BadRequestException;
import com.nk.base.exception.IllegalArgumentException;
import com.nk.common.dto.SuccessResponseStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @GetMapping("/test")
    public ResponseEntity<ResponseDto<String>> getUser(@RequestHeader Map<String, String> headers, @RequestParam("name") String name) {
        log.info("headers: {}", headers);
        ResponseDto<String> responseDto = new ResponseDto<>(SuccessResponseStatusMapper.mapToSuccessResponse(HttpStatus.OK));
        if (name == null) {
            throw new BadRequestException("name is required");
        }
        if ("xyz".equals(name)) {
            throw new IllegalArgumentException("name should not be xyz");
        }
        responseDto.setData("test");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
