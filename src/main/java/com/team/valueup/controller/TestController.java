package com.team.valueup.controller;

import com.team.valueup.entity.TestEntity;
import com.team.valueup.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
