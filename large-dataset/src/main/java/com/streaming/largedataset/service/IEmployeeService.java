package com.streaming.largedataset.service;

import com.streaming.largedataset.model.EmployeeModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.stream.Stream;

public interface IEmployeeService {
//    public ResponseEntity<StreamingResponseBody> findActiveEmployees();
    public Stream<EmployeeModel> findActiveEmployees();
    public List<EmployeeModel> findAllEmployees();
    public void addEmployee(EmployeeModel employeeModel);
}
