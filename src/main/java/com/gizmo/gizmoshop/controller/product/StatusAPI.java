package com.gizmo.gizmoshop.controller.product;

import com.gizmo.gizmoshop.dto.reponseDto.StatusDto;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.entity.StatusProduct;
import com.gizmo.gizmoshop.service.product.StatusService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StatusAPI {
    @Autowired
    StatusService statusService;
    @GetMapping("/list/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<StatusProduct>>> getInventory() {
        ResponseWrapper<List<StatusProduct>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", statusService.getAllStatus());
        return ResponseEntity.ok(responseWrapper);
    }
}
