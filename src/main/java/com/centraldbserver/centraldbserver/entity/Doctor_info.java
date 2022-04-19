package com.centraldbserver.centraldbserver.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="doctor_info")
@Data
public class Doctor_info {

    @Id
    private String doctor_id;

    private String doctor_name;

    private String doctor_email;

    private String doctor_contact;

    private String doctor_speciality;

}
