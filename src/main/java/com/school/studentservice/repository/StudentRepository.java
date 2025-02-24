package com.school.studentservice.repository;

import com.school.studentservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByAdmissionNumber(String admissionNumber);
    Student findByAdmissionNumber(String admissionNumber);
    //List<Student> findByClassName(String className); // You might still have this
    List<Student> findByClassId(Long classId);     // ADD THIS METHOD - find by classId
}
