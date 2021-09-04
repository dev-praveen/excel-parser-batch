package com.excel.batch.config;

import com.excel.batch.models.Student;
import com.excel.batch.models.StudentDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class ExcelBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<StudentDTO> excelStudentReader() {
        PoiItemReader<StudentDTO> reader = new PoiItemReader<>();
        reader.setName("XLSItemReader");
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource("/home/praveen/Desktop/students_list.xlsx"));
        reader.setRowMapper(excelRowMapper());
        return reader;
    }

    @Bean
    public ExcelItemProcessor excelItemProcessor() {
        return new ExcelItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Student> csvItemWriter() {

        return new FlatFileItemWriterBuilder<Student>()
                .name("CSVItemWriter")
                .resource(new FileSystemResource("/home/praveen/Desktop/students_output.txt"))
                .delimited()
                .delimiter("|")
                .names(new String[]{"name", "emailAddress"})
                .build();
    }

    @Bean
    public Job excelParserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("ExcelParserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("ExcelReadWriteStep")
                .<StudentDTO, Student> chunk(1)
                .reader(excelStudentReader())
                .processor(excelItemProcessor())
                .writer(csvItemWriter())
                .build();
    }

    private RowMapper<StudentDTO> excelRowMapper() {
        BeanWrapperRowMapper<StudentDTO> rowMapper = new BeanWrapperRowMapper<>();
        rowMapper.setTargetType(StudentDTO.class);
        return rowMapper;
    }

}
