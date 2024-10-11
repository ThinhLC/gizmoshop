package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.StatusDto;
import com.gizmo.gizmoshop.entity.StatusProduct;
import com.gizmo.gizmoshop.repository.StatusProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    StatusProductRepository statusProductRepository;
    public List<StatusProduct> getAllStatus() {
        return statusProductRepository.findAll();
    }
}
