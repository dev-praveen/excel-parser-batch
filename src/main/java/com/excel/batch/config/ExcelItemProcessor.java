package com.excel.batch.config;

import com.excel.batch.models.Student;
import com.excel.batch.models.StudentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ExcelItemProcessor implements ItemProcessor<StudentDTO, Student> {

    @Override
    public Student process(final StudentDTO studentDTO) {

        log.info("=======student dto object============ "+studentDTO);
        Student student = new Student();
        student.setEmailAddress(studentDTO.getEmailAddress());
        student.setName(studentDTO.getName());

        log.info("=======student object============ "+student);
        return student;
    }
}
