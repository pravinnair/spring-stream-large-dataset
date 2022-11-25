package com.streaming.largedataset.controller;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.streaming.largedataset.model.EmployeeModel;
import com.streaming.largedataset.service.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.net.ssl.SSLEngineResult;
import java.io.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequestMapping(path = "api/v1/employees")
public class StreamEmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private EmployeeModel employeeModel;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //    @GetMapping(value = "/stream")
//    public ResponseEntity<StreamingResponseBody> findAllEmployee() throws ExecutionException, InterruptedException {
//        LoggerFactory.getLogger(getClass()).info("request received to fetch all employee details");
//        return employeeService.findActiveEmployees();
//    }
    @GetMapping(value = "/stream")
    public ResponseEntity<StreamingResponseBody> findAllEmployee() {
        logger.info("request received to fetch all employee details");
        Instant start = Instant.now();
        Stream<EmployeeModel> employees = employeeService.findActiveEmployees();
        StreamingResponseBody responseBody = httpResponseOutputStream -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
                employees.forEach(employee -> {
                    try {
                        writer.write(new Gson().toJson(employee));
                        writer.flush();
                    } catch (IOException exception) {
                        logger.error("exception occurred while writing object to stream", exception);
                    }
                });
            } catch (Exception exception) {
                logger.error("exception occurred while publishing data", exception);
            }
            logger.info("finished streaming records");
        };

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time taken to STREAM all 10k empls:" + duration);
        return ResponseEntity.status(HttpStatus.OK).
                contentType(MediaType.APPLICATION_JSON).
                body(responseBody);
    }

    @GetMapping("/employee")
    public List<EmployeeModel> findAllEmployees() {
        Instant start = Instant.now();
        List<EmployeeModel> employees = employeeService.findAllEmployees();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Time taken to fetch all 10k empls:" + duration);
        return employees;
    }

    @GetMapping(value = "/stream-employees")
    @ExceptionHandler
    public List<EmployeeModel> getEmployees(){
        RestTemplate restTemplate=new RestTemplate();
        List<EmployeeModel> employeeModels = new ArrayList<>();
        String getUrl="http://localhost:8080/api/v1/employees/stream";
        Gson gson = new Gson();
        try {
            logger.info("Inside TRY BLOCK\n");
            restTemplate.execute(
                    getUrl,
                    HttpMethod.GET,
                    null,
                    response -> {
                        InputStream body = response.getBody();
                        JsonReader jsonReader = new JsonReader(new InputStreamReader(body));
                        jsonReader.setLenient(true);
                        while (jsonReader.hasNext() && jsonReader.peek() !=
                                JsonToken.END_DOCUMENT) {
                            EmployeeModel employeeModel = gson.fromJson(jsonReader, EmployeeModel.class);
                            employeeModels.add(employeeModel);
//                        logger.info("found [{}] employee", employeeModel);
                        }
                        logger.info("found [{}] employee", employeeModels);
                        return employeeModels;
                    });
        }
        catch(IllegalStateException e){
            logger.error("IllegalStateException Error:",e.getMessage());
        }
        catch(Exception e){
            logger.error("System Error:",e);
        }
        return null;
    }

    @PostMapping
    public void addEmployee(@RequestBody EmployeeModel employeeModel) {
        for (int i = 0; i < 10000; i++) {
            employeeModel.setEmailId("mail-daemon" + i + "@gmail.com");
            employeeModel.setFirstName("Bot-" + i + "-first");
            employeeModel.setLastName("Bot-Last-" + i);
            employeeService.addEmployee(employeeModel);
        }
    }
}
/*
*
Time taken to fetch all 10k empls:1,34,08,46,200 ms
Hibernate: select e1_0.employee_id,e1_0.active,e1_0.dob,e1_0.email_address,e1_0.first_name,e1_0.last_name from tbl_employee e1_0
Time taken to fetch all 10k empls:83,25,40,400 ms
*
* Time taken to STREAM all 10k empls:2,51,97,700 ms
2022-11-25T08:32:31.543-05:00  INFO 6456 --- [         task-1] c.s.l.c.StreamEmployeeController         : finished streaming records
2022-11-25T08:33:22.777-05:00  INFO 6456 --- [nio-8080-exec-6] c.s.l.c.StreamEmployeeController         : request received to fetch all employee details
Time taken to STREAM all 10k empls:2,88,45,700 ms
* */
