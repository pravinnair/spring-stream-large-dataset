package com.streaming.largedataset.model;

import com.streaming.largedataset.entity.Employee;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
//@Getter
//@Setter
public class EmployeeModel {
    private String firstName;
    private String lastName;
    private String dob;
    private String emailId;
    private Boolean active;

    public static final class EmployeeBuilder {
        private String firstName;
        private String lastName;
        private String emailId;
        private String dob;
        private Boolean active;
        private EmployeeBuilder() {
        }

        public static EmployeeBuilder anEmployee() {
            return new EmployeeBuilder();
        }

        public EmployeeBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public EmployeeBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public EmployeeBuilder withEmailId(String emailId) {
            this.emailId = emailId;
            return this;
        }


        public EmployeeBuilder withBirthDate(String dob) {
            this.dob = dob;
            return this;
        }

        public EmployeeBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public EmployeeModel build() {
            return new EmployeeModel(firstName, lastName, dob, emailId, active);
        }
    }


    @Override
    public String toString() {
        return "EmployeeModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", emailId=" + emailId +
                ", active='" + active + '\'' +
                '}';
    }
}
