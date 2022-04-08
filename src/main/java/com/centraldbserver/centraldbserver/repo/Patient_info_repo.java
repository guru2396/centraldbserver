package com.centraldbserver.centraldbserver.repo;

import com.centraldbserver.centraldbserver.entity.Patient_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Patient_info_repo extends JpaRepository<Patient_info,String> {

    @Query(value = "SELECT * FROM patient_info WHERE patient_id=?1",nativeQuery = true)
    Patient_info getPatientById(String id);

    @Query(value = "SELECT * FROM patient_info WHERE patient_email=?1",nativeQuery = true)
    Patient_info getPatientByEmail(String email);
}
