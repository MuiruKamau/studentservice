package com.school.studentservice.repository;

import com.school.studentservice.entity.Exam;
import com.school.studentservice.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStudentId(Long studentId);
    List<Exam> findByStudentIdAndTermAndSubjectId(Long studentId, Term term, Long subjectId);
    List<Exam> findByStudentIdAndTerm(Long studentId, Term term);
    List<Exam> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
