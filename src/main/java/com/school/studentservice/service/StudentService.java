package com.school.studentservice.service;

import com.school.studentservice.dto.*;
import com.school.studentservice.entity.ParentDetails;
import com.school.studentservice.client.ConfigurationServiceClient;
import com.school.studentservice.entity.Student;
import com.school.studentservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
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

    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        // Get the count once outside the loop
        long studentCount = studentRepository.count();
        String prefix = "SMS";
        String year = String.valueOf(LocalDate.now().getYear());

        String admissionNumber;
        boolean exists;

        do {
            studentCount++;
            String formattedId = String.format("%04d", studentCount);
            admissionNumber = prefix + year + formattedId;
            exists = studentRepository.existsByAdmissionNumber(admissionNumber);
        } while (exists);

        ParentDetails parentDetails = new ParentDetails(
                studentRequestDTO.getParentDetails().getParentName(),
                studentRequestDTO.getParentDetails().getContact(),
                studentRequestDTO.getParentDetails().getAddress()
        );

        Student student = new Student(
                null, // ID will be auto-generated
                studentRequestDTO.getName(),
                admissionNumber,
                studentRequestDTO.getDateOfBirth(),
                studentRequestDTO.getEnrollmentDate(),
                studentRequestDTO.getAddress(),
                studentRequestDTO.getContactNumber(),
                parentDetails,
                studentRequestDTO.getClassId(),
                studentRequestDTO.getStreamId(),
                null // Exams set will be handled by JPA
        );

        Student savedStudent = studentRepository.save(student);
        return mapToDTO(savedStudent);
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

                    ParentDetails parentDetails = new ParentDetails(
                            studentRequestDTO.getParentDetails().getParentName(),
                            studentRequestDTO.getParentDetails().getContact(),
                            studentRequestDTO.getParentDetails().getAddress()
                    );
                    existingStudent.setParentDetails(parentDetails);
                    existingStudent.setClassId(studentRequestDTO.getClassId());
                    existingStudent.setStreamId(studentRequestDTO.getStreamId());

                    Student updatedStudent = studentRepository.save(existingStudent);
                    return mapToDTO(updatedStudent);
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
        ParentDetailsDTO parentDetailsDTO = new ParentDetailsDTO(
                student.getParentDetails().getParentName(),
                student.getParentDetails().getContact(),
                student.getParentDetails().getAddress()
        );

        // Fetch Class Name from Configuration Service
        SchoolClassResponseDTO classDto = configurationServiceClient.getClassById(student.getClassId());
        String className = (classDto != null) ? classDto.getName() : "N/A";

        // Fetch Stream Name from Configuration Service
        StreamResponseDTO streamDto = configurationServiceClient.getStreamById(student.getStreamId());
        String streamName = (streamDto != null) ? streamDto.getName() : "N/A";

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getAdmissionNumber(),
                student.getDateOfBirth(),
                student.getEnrollmentDate(),
                student.getAddress(),
                student.getContactNumber(),
                parentDetailsDTO,
                student.getClassId(),
                student.getStreamId(),
                className,
                streamName
        );
    }

    /**
     * This method is no longer called directly but is kept for reference
     * or potential future use with a different implementation.
     */
    private String generateAdmissionNumber() {
        String prefix = "SMS";
        String year = String.valueOf(LocalDate.now().getYear());
        long nextId = studentRepository.count() + 1;
        String formattedId = String.format("%04d", nextId);
        return prefix + year + formattedId;
    }
}




//package com.school.studentservice.service;
//
//import com.school.studentservice.dto.*;
//import com.school.studentservice.entity.ParentDetails;
//import com.school.studentservice.client.ConfigurationServiceClient;
//import com.school.studentservice.entity.Student;
//import com.school.studentservice.repository.StudentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class StudentService {
//    private final StudentRepository studentRepository;
//    private final ConfigurationServiceClient configurationServiceClient;
//
//    @Autowired // Only one constructor now
//    public StudentService(StudentRepository studentRepository, ConfigurationServiceClient configurationServiceClient) {
//        this.studentRepository = studentRepository;
//        this.configurationServiceClient = configurationServiceClient; // Initialization
//    }
//
//    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
//        String admissionNumber = generateAdmissionNumber();
//        while (studentRepository.existsByAdmissionNumber(admissionNumber)) {
//            admissionNumber = generateAdmissionNumber();
//        }
//
//        ParentDetails parentDetails = new ParentDetails( // Using constructor for ParentDetails entity
//                studentRequestDTO.getParentDetails().getParentName(),
//                studentRequestDTO.getParentDetails().getContact(),
//                studentRequestDTO.getParentDetails().getAddress()
//        );
//
//        Student student = new Student( // Using constructor for Student entity
//                null, // ID will be auto-generated
//                studentRequestDTO.getName(),
//                admissionNumber,
//                studentRequestDTO.getDateOfBirth(),
//                studentRequestDTO.getEnrollmentDate(),
//                studentRequestDTO.getAddress(),
//                studentRequestDTO.getContactNumber(),
//                parentDetails,
//                studentRequestDTO.getClassId(),
//                studentRequestDTO.getStreamId(),
//                null // Exams set will be handled by JPA
//        );
//
//        Student savedStudent = studentRepository.save(student);
//        return mapToDTO(savedStudent);
//    }
//
//    public StudentResponseDTO getStudentById(Long id) {
//        return studentRepository.findById(id)
//                .map(this::mapToDTO)
//                .orElse(null);
//    }
//
//    public StudentResponseDTO getStudentByAdmissionNumber(String admissionNumber) {
//        Student student = studentRepository.findByAdmissionNumber(admissionNumber);
//        return student != null ? mapToDTO(student) : null;
//    }
//
//    public List<StudentResponseDTO> getAllStudents() {
//        return studentRepository.findAll().stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }
//
//    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO studentRequestDTO) {
//        return studentRepository.findById(id)
//                .map(existingStudent -> {
//                    existingStudent.setName(studentRequestDTO.getName());
//                    existingStudent.setDateOfBirth(studentRequestDTO.getDateOfBirth());
//                    existingStudent.setEnrollmentDate(studentRequestDTO.getEnrollmentDate());
//                    existingStudent.setAddress(studentRequestDTO.getAddress());
//                    existingStudent.setContactNumber(studentRequestDTO.getContactNumber());
//
//                    ParentDetails parentDetails = new ParentDetails( // Using constructor for ParentDetails entity
//                            studentRequestDTO.getParentDetails().getParentName(),
//                            studentRequestDTO.getParentDetails().getContact(),
//                            studentRequestDTO.getParentDetails().getAddress()
//                    );
//                    existingStudent.setParentDetails(parentDetails);
//                    existingStudent.setClassId(studentRequestDTO.getClassId());
//                    existingStudent.setStreamId(studentRequestDTO.getStreamId());
//
//                    Student updatedStudent = studentRepository.save(existingStudent);
//                    return mapToDTO(updatedStudent);
//                })
//                .orElse(null);
//    }
//
//    public boolean deleteStudent(Long id) {
//        if (studentRepository.existsById(id)) {
//            studentRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
//
//    private StudentResponseDTO mapToDTO(Student student) {
//        ParentDetailsDTO parentDetailsDTO = new ParentDetailsDTO(
//                student.getParentDetails().getParentName(),
//                student.getParentDetails().getContact(),
//                student.getParentDetails().getAddress()
//        );
//
//        // Fetch Class Name from Configuration Service
//        SchoolClassResponseDTO classDto = configurationServiceClient.getClassById(student.getClassId());
//        String className = (classDto != null) ? classDto.getName() : "N/A";
//
//        // Fetch Stream Name from Configuration Service
//        StreamResponseDTO streamDto = configurationServiceClient.getStreamById(student.getStreamId());
//        String streamName = (streamDto != null) ? streamDto.getName() : "N/A";
//
//
//        return new StudentResponseDTO(
//                student.getId(),
//                student.getName(),
//                student.getAdmissionNumber(),
//                student.getDateOfBirth(),
//                student.getEnrollmentDate(),
//                student.getAddress(),
//                student.getContactNumber(),
//                parentDetailsDTO,
//                student.getClassId(),
//                student.getStreamId(),
//                className, // Set className in StudentResponseDTO
//                streamName // Set streamName in StudentResponseDTO
//        );
//    }
//
//
//    private String generateAdmissionNumber() {
//        String prefix = "SMS";
//        String year = String.valueOf(LocalDate.now().getYear());
//        long nextId = studentRepository.count() + 1;
//        String formattedId = String.format("%04d", nextId);
//        return prefix + year + formattedId;
//    }
//}