package com.streaming.largedataset.controller;

import com.google.gson.Gson;
import com.streaming.largedataset.model.EmployeeModel;
import com.streaming.largedataset.service.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.net.ssl.SSLEngineResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequestMapping(path ="api/v1/employees")
public class StreamEmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;


    @Autowired
    private EmployeeModel employeeModel;

//    @GetMapping(value = "/stream")
//    public ResponseEntity<StreamingResponseBody> findAllEmployee() throws ExecutionException, InterruptedException {
//        LoggerFactory.getLogger(getClass()).info("request received to fetch all employee details");
//        return employeeService.findActiveEmployees();
//    }
@GetMapping(value = "/stream")
public ResponseEntity<StreamingResponseBody> findAllEmployee() {
    LoggerFactory.getLogger(getClass()).info("request received to fetch all employee details");
    Instant start=Instant.now();
    Stream<EmployeeModel> employees = employeeService.findActiveEmployees();
    StreamingResponseBody responseBody = httpResponseOutputStream -> {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
            employees.forEach(employee -> {
                try {
                    writer.write(new Gson().toJson(employee));
                    writer.flush();
                } catch (IOException exception) {
                    LoggerFactory.getLogger(getClass()).error("exception occurred while writing object to stream", exception);
                }
            });
        } catch (Exception exception) {
            LoggerFactory.getLogger(getClass()).error("exception occurred while publishing data", exception);
        }
        LoggerFactory.getLogger(getClass()).info("finished streaming records");
    };

    Instant end=Instant.now();
    Duration duration= Duration.between(start,end);
    System.out.println("Time taken to STREAM all 10k empls:"+duration );
    return ResponseEntity.status(HttpStatus.OK).
            contentType(MediaType.APPLICATION_JSON).
            body(responseBody);
}
    @GetMapping("/employee")
    public List<EmployeeModel> findAllEmployees(){
//        long start= System.nanoTime();
        Instant start=Instant.now();
        List<EmployeeModel> employees= employeeService.findAllEmployees();
//        long end= System.nanoTime();
        Instant end=Instant.now();
        Duration duration= Duration.between(start,end);
        System.out.println("Time taken to fetch all 10k empls:"+duration );
        return employees;
    }

    @PostMapping
    public void addEmployee(@RequestBody EmployeeModel employeeModel){
        for (int i=0; i<10000; i++){
            employeeModel.setEmailId("mail-daemon"+i+"@gmail.com");
            employeeModel.setFirstName("Bot-"+i+"-first");
            employeeModel.setLastName("Bot-Last-"+i);
            employeeService.addEmployee(employeeModel);
        }
    }
}
/*
*
Time taken to fetch all 10k empls:1,34,08,46,200
Hibernate: select e1_0.employee_id,e1_0.active,e1_0.dob,e1_0.email_address,e1_0.first_name,e1_0.last_name from tbl_employee e1_0
Time taken to fetch all 10k empls:83,25,40,400
*
* Time taken to STREAM all 10k empls:2,51,97,700
2022-11-25T08:32:31.543-05:00  INFO 6456 --- [         task-1] c.s.l.c.StreamEmployeeController         : finished streaming records
2022-11-25T08:33:22.777-05:00  INFO 6456 --- [nio-8080-exec-6] c.s.l.c.StreamEmployeeController         : request received to fetch all employee details
Time taken to STREAM all 10k empls:2,88,45,700
* */
