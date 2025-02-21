package com.school.studentservice.repository;

import com.school.studentservice.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStudentId(Long studentId);
    List<Exam> findByStudentIdAndTermAndSubject(Long studentId, String term, String subject);
    List<Exam> findByStudentIdAndTerm(Long studentId, String term);
    List<Exam> findByStudentIdAndSubject(Long studentId, String subject);
}
