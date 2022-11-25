package com.streaming.largedataset.respository;

import com.streaming.largedataset.entity.Employee;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.HibernateHints.HINT_CACHEABLE;
import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @QueryHints(value = {
            @QueryHint(name=HINT_FETCH_SIZE, value="50"),
            @QueryHint(name=HINT_CACHEABLE, value="false"),
            @QueryHint(name=READ_ONLY, value="true"),
    })
    @Query(
            value = "select * from tbl_employee emp",
            nativeQuery = true)
    Stream<Employee> getAllEmployees();
}
