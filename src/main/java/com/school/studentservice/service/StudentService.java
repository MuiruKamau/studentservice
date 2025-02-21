package com.school.studentservice.service;

import com.school.studentservice.dto.ParentDetailsDTO;
import com.school.studentservice.dto.StudentRequestDTO;
import com.school.studentservice.dto.StudentResponseDTO;
import com.school.studentservice.entity.ParentDetails; // Corrected import to entity
import com.school.studentservice.entity.Student; // Corrected import to entity
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        String admissionNumber = generateAdmissionNumber();
        while (studentRepository.existsByAdmissionNumber(admissionNumber)) {
            admissionNumber = generateAdmissionNumber();
        }

        ParentDetails parentDetails = new ParentDetails( // Using constructor for ParentDetails entity
                studentRequestDTO.getParentDetails().getParentName(),
                studentRequestDTO.getParentDetails().getContact(),
                studentRequestDTO.getParentDetails().getAddress()
        );

        Student student = new Student( // Using constructor for Student entity
                null, // ID will be auto-generated
                studentRequestDTO.getName(),
                admissionNumber,
                studentRequestDTO.getDateOfBirth(),
                studentRequestDTO.getEnrollmentDate(),
                studentRequestDTO.getAddress(),
                studentRequestDTO.getContactNumber(),
                parentDetails,
                studentRequestDTO.getClassName(),
                studentRequestDTO.getStreamName(),
                null // Exams set will be handled by JPA
        );

        Student savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
    }

    public StudentResponseDTO getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public StudentResponseDTO getStudentByAdmissionNumber(String admissionNumber) {
        Student student = studentRepository.findByAdmissionNumber(admissionNumber);
        return student != null ? mapToDTO(student) : null;
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(studentRequestDTO.getName());
                    existingStudent.setDateOfBirth(studentRequestDTO.getDateOfBirth());
                    existingStudent.setEnrollmentDate(studentRequestDTO.getEnrollmentDate());
                    existingStudent.setAddress(studentRequestDTO.getAddress());
                    existingStudent.setContactNumber(studentRequestDTO.getContactNumber());

                    ParentDetails parentDetails = new ParentDetails( // Using constructor for ParentDetails entity
                            studentRequestDTO.getParentDetails().getParentName(),
                            studentRequestDTO.getParentDetails().getContact(),
                            studentRequestDTO.getParentDetails().getAddress()
                    );
                    existingStudent.setParentDetails(parentDetails);
                    existingStudent.setClassName(studentRequestDTO.getClassName());
                    existingStudent.setStreamName(studentRequestDTO.getStreamName());

                    Student updatedStudent = studentRepository.save(existingStudent);
                    return mapToDTO(updatedStudent);
                })
                .orElse(null);
    }

    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private StudentResponseDTO mapToDTO(Student student) {
        ParentDetailsDTO parentDetailsDTO = new ParentDetailsDTO( // Using constructor for ParentDetailsDTO
                student.getParentDetails().getParentName(),
                student.getParentDetails().getContact(),
                student.getParentDetails().getAddress()
        );

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getDateOfBirth(),
                student.getEnrollmentDate(),
                student.getAddress(),
                student.getContactNumber(),
                parentDetailsDTO,
                student.getClassName(),
                student.getStreamName()
        );
    }

    private String generateAdmissionNumber() {
        String prefix = "SMS";
        String year = String.valueOf(LocalDate.now().getYear());
        long nextId = studentRepository.count() + 1;
        String formattedId = String.format("%04d", nextId);
        return prefix + year + formattedId;
    }
}