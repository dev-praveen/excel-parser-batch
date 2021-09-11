package com.excel.batch.config;

import com.excel.batch.models.Student;
import com.excel.batch.models.StudentDTO;
import com.excel.batch.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class ExcelBatchConfiguration {

    //@Autowired
    private final JobBuilderFactory jobBuilderFactory;

    //@Autowired
    private final StepBuilderFactory stepBuilderFactory;

    //@Autowired
    private final StudentRepository studentRepository;

    //@Autowired
    private final PlatformTransactionManager platformTransactionManager;

    //@Autowired
    private final DataSource dataSource;

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
    public RepositoryItemWriter<Student> dbItemWriter() {

        return new RepositoryItemWriterBuilder<Student>()
                .methodName("save")
                .repository(this.studentRepository)
                .build();
    }

    @Bean
    public Job excelParserJob(JobCompletionNotificationListener listener) {
        return this.jobBuilderFactory.get("ExcelParserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("ExcelReadWriteStep")
                .<StudentDTO, Student>chunk(1)
                .reader(excelStudentReader())
                .processor(excelItemProcessor())
                .writer(dbItemWriter())
                .transactionManager(this.platformTransactionManager)
                .build();
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }


    private RowMapper<StudentDTO> excelRowMapper() {
        final BeanWrapperRowMapper<StudentDTO> rowMapper = new BeanWrapperRowMapper<>();
        rowMapper.setTargetType(StudentDTO.class);
        return rowMapper;
    }
}
