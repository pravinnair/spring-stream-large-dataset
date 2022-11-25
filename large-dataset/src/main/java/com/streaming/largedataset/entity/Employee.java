package com.streaming.largedataset.entity;

import com.streaming.largedataset.model.EmployeeModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "tbl_employee",
        uniqueConstraints = @UniqueConstraint(
                name = "email_unique",
                columnNames = "email_address")
)
public class Employee {
    @Id
    @SequenceGenerator(
            name = "employee_seq",
            sequenceName = "employee_seq",
            allocationSize = 1)
    @GeneratedValue(
            generator = "employee_seq",
            strategy = GenerationType.SEQUENCE
    )
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Date dob;
    @Column(
            name = "email_address",
            nullable = false,
            unique = true)
    private String emailId;
    private Boolean active=false;

    public EmployeeModel toModel() {
        return EmployeeModel.EmployeeBuilder
                .anEmployee()
                .withFirstName(this.firstName)
                .withLastName(this.lastName)
                .withBirthDate(this.dob.toString())
                .withEmailId(this.emailId)
                .withActive(this.active)
                .build();
    }
}
