package com.school.studentservice.service;

import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.dto.*;
import com.school.studentservice.entity.LearningSubject;
import com.school.studentservice.entity.ParentDetails;
import com.school.studentservice.entity.Student;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final ConfigurationServiceClient configurationServiceClient;

    @Autowired
    public StudentService(StudentRepository studentRepository, ConfigurationServiceClient configurationServiceClient) {
        this.studentRepository = studentRepository;
        this.configurationServiceClient = configurationServiceClient;
    }

    private Set<String> getSubjectsForClassAndStream(Long classId, Long streamId) {
        List<LearningSubjectResponseDTO> subjectDTOs;

        if (classId == 11 || classId == 12) { // Form 1 & 2: Class-based subjects
            subjectDTOs = configurationServiceClient.getSubjectsForClass(classId);
        } else if (classId == 13 || classId == 14) { // Form 3 & 4: Stream-based subjects
            subjectDTOs = configurationServiceClient.getSubjectsForStream(streamId);
        } else {
            return new HashSet<>(); // No subjects for other classes
        }

        return subjectDTOs.stream()
                .map(LearningSubjectResponseDTO::getName)
                .collect(Collectors.toSet());
    }


    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        long studentCount = studentRepository.count();
        String prefix = "SMS";
        String year = String.valueOf(LocalDate.now().getYear());
        String admissionNumber = prefix + year + String.format("%04d", studentCount + 1);


        ParentDetails parentDetails = new ParentDetails(
                studentRequestDTO.getParentDetails().getParentName(),
                studentRequestDTO.getParentDetails().getContact(),
                studentRequestDTO.getParentDetails().getAddress()
        );

        Student student = new Student(
                null,
                studentRequestDTO.getName(),
                admissionNumber,
                studentRequestDTO.getDateOfBirth(),
                studentRequestDTO.getEnrollmentDate(),
                studentRequestDTO.getAddress(),
                studentRequestDTO.getContactNumber(),
                parentDetails,
                studentRequestDTO.getClassId(),
                studentRequestDTO.getStreamId(),
                null
        );

        Student savedStudent = studentRepository.save(student);

        // --- Populate Learning Subjects ---
        Set<String> subjectNames = getSubjectsForClassAndStream(studentRequestDTO.getClassId(), studentRequestDTO.getStreamId());
        Set<LearningSubject> learningSubjects = new HashSet<>();

        for (String subjectName : subjectNames) {
            LearningSubjectResponseDTO subjectDTO = configurationServiceClient.getLearningSubjectByName(subjectName);
            if (subjectDTO != null) {
                learningSubjects.add(new LearningSubject(subjectDTO.getId(), subjectDTO.getName()));
            } else {
                System.err.println("Warning: Learning Subject '" + subjectName + "' not found in configuration service.");
            }
        }

        savedStudent.setLearningSubjects(learningSubjects);
        Student savedStudentWithSubjects = studentRepository.save(savedStudent);

        return mapToDTO(savedStudentWithSubjects);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByAdmissionNumber(String admissionNumber) {
        Student student = studentRepository.findByAdmissionNumber(admissionNumber);
        return student != null ? mapToDTO(student) : null;
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(studentRequestDTO.getName());
                    existingStudent.setDateOfBirth(studentRequestDTO.getDateOfBirth());
                    existingStudent.setEnrollmentDate(studentRequestDTO.getEnrollmentDate());
                    existingStudent.setAddress(studentRequestDTO.getAddress());
                    existingStudent.setContactNumber(studentRequestDTO.getContactNumber());
                    existingStudent.setParentDetails(new ParentDetails( // Use constructor here
                            studentRequestDTO.getParentDetails().getParentName(),
                            studentRequestDTO.getParentDetails().getContact(),
                            studentRequestDTO.getParentDetails().getAddress()
                    ));
                    existingStudent.setClassId(studentRequestDTO.getClassId());
                    existingStudent.setStreamId(studentRequestDTO.getStreamId());
                    return mapToDTO(studentRepository.save(existingStudent));
                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private StudentResponseDTO mapToDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getDateOfBirth(),
                student.getEnrollmentDate(),
                student.getAddress(),
                student.getContactNumber(),
                new ParentDetailsDTO( // Use constructor here
                        student.getParentDetails().getParentName(),
                        student.getParentDetails().getParentContact(),
                        student.getParentDetails().getAddress()
                ),
                student.getClassId(),
                student.getStreamId(),
                configurationServiceClient.getClassById(student.getClassId()).getName(),
                configurationServiceClient.getStreamById(student.getStreamId()).getName(),
                student.getLearningSubjects().stream()
                        .map(subject -> new LearningSubjectResponseDTO(subject.getId(), subject.getName()))
                        .collect(Collectors.toList())
        );
    }
}