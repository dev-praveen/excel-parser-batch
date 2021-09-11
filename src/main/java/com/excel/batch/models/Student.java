package com.excel.batch.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "STUDENT")
public class Student {

    @Id
    @Column(name = "STUDENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "STUDENT_NAME")
    private String name;

    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;
}
