package com.team.comma.controller;

import com.team.comma.entity.TestEntity;
import com.team.comma.repository.TestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {
    @Autowired
    TestRepository testrepos;

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public List<TestEntity> results() {
        List<TestEntity> result = testrepos.findAll();

        return result;
    }

}
