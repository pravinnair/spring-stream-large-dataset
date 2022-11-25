package com.streaming.largedataset.service;

import com.google.gson.Gson;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.streaming.largedataset.entity.Employee;
import com.streaming.largedataset.model.EmployeeModel;
import com.streaming.largedataset.respository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.sql.DataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DataSource dataSource) {
        this.employeeRepository = employeeRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
//    TODO: commented below code as it was failing because of JDBC connection close issue
//    @Override
//    @Transactional(readOnly = true)
//    public ResponseEntity<StreamingResponseBody> findActiveEmployees(){
//        Stream<EmployeeModel> employeeStream= employeeRepository
//                .getAllEmployees()
//                .map(Employee::toModel);
//        StreamingResponseBody responseBody=httpResponseOutputStream ->{
//            try (Writer writer = new BufferedWriter(
//                    new OutputStreamWriter(httpResponseOutputStream))) {
//                employeeStream.forEach(employee -> {
//                    try {
////                        GsonJsonProvider gson = new GsonJsonProvider();
//                        Gson gson = new Gson();
//                        writer.write(gson.toJson(employee));
//                        logger.info("streamed record");
//                        writer.flush();
//                    } catch (IOException exception) {
//                        logger.error("exception occurred while writing object to stream", exception);
//                    }
//                });
//            } catch (Exception exception) {
//                logger.error("exception occurred while publishing data", exception);
//            }
//            logger.info("finished streaming records");
//        };
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }

    @Override
    public Stream<EmployeeModel> findActiveEmployees() {
        jdbcTemplate.setFetchSize(10);
        return jdbcTemplate.queryForStream(
                "Select FIRST_NAME, LAST_NAME, DOB, EMAIL_ADDRESS, ACTIVE from tbl_employee",
                (resultSet, rowNum) ->
                        new EmployeeModel(
                                resultSet.getString("FIRST_NAME"),
                                resultSet.getString("LAST_NAME"),
                                resultSet.getDate("DOB"),
                                resultSet.getString("EMAIL_ADDRESS"),
                                resultSet.getBoolean("ACTIVE"))
        );
    }

    @Override
    public List<EmployeeModel> findAllEmployees(){
        List<Employee> employees= employeeRepository.findAll();
        List<EmployeeModel> employeesModel = new ArrayList<EmployeeModel>();

        for (Employee employee: employees) {
            EmployeeModel model=new EmployeeModel();
            model.setFirstName(employee.getFirstName());
            model.setLastName(employee.getLastName());
            model.setEmailId(employee.getEmailId());
            model.setDob(employee.getDob());
            employeesModel.add(model);
        }
        return employeesModel;
    }

    @Override
    public void addEmployee(EmployeeModel employeeModel){
        Employee employee= new Employee();
        employee.setFirstName(employeeModel.getFirstName());
        employee.setLastName(employeeModel.getLastName());
        employee.setDob(employeeModel.getDob());
        employee.setEmailId(employeeModel.getEmailId());
        employeeRepository.save(employee);
    }
}
