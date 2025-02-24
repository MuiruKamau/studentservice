package com.school.studentservice.repository;

import com.school.studentservice.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByStudentId(Long studentId);
    // Corrected method name: use SubjectId instead of Subject
    List<Exam> findByStudentIdAndTermAndSubjectId(Long studentId, String term, Long subjectId); // Renamed to SubjectId
    List<Exam> findByStudentIdAndTerm(Long studentId, String term);
    // Corrected method name: use SubjectId instead of Subject
    List<Exam> findByStudentIdAndSubjectId(Long studentId, Long subjectId); // Renamed to SubjectId
}
