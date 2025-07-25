package com.example.controller;

import com.example.entity.Employee;
import com.example.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository repo;

    @GetMapping
    public List<Employee> list() {
        return repo.findAll();
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return repo.save(employee);
    }
}