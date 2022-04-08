package com.centraldbserver.centraldbserver.controller;

import com.centraldbserver.centraldbserver.DTO.ConsentNotificationResponse;
import com.centraldbserver.centraldbserver.DTO.ConsentRequestDTO;
import com.centraldbserver.centraldbserver.DTO.PatientRegistrationDto;
import com.centraldbserver.centraldbserver.service.CentralServerService;
import com.centraldbserver.centraldbserver.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CentralServerController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CentralServerService centralServerService;

    @PostMapping(value="/register-patient")
    public ResponseEntity<?> register(@RequestBody PatientRegistrationDto patientRegistrationDto){
        String id=centralServerService.registerPatient(patientRegistrationDto);

        return ResponseEntity.ok(id);
    }

    @GetMapping(value="/get-consent-notifications/{patientId}")
    public ResponseEntity<?> getConsentRequests(@PathVariable("patientId") String patientId){
        //tring patientId=jwtService.extractID(token);
       // System.out.print(token);
        List<ConsentNotificationResponse> consentrequests= centralServerService.getConsentRequests(patientId);
        return ResponseEntity.ok(consentrequests);
    }

    @PostMapping(value="/create-consent-request/{doctorId}/{hospitalId}")
    public ResponseEntity<?> updateConsentRequest(@PathVariable("doctorId") String doctorId, @PathVariable("hospitalId") String hospitalId,@RequestBody ConsentRequestDTO consentRequestDTO){
        //tring patientId=jwtService.extractID(token);
        // System.out.print(token);
        String id= centralServerService.createConsentRequest(doctorId,hospitalId,consentRequestDTO);
        return ResponseEntity.ok(id);
    }

    @GetMapping(value="/get-hospitals/{patientId}")
    public ResponseEntity<?> getHospitalsByPatientID(@PathVariable("patientId") String patientId){
        //tring patientId=jwtService.extractID(token);
        // System.out.print(token);
        List<String> hospitals= centralServerService.getHospitals(patientId);
        return ResponseEntity.ok(hospitals);
    }

    @PostMapping(value="/update-mapping/{patientId}/{hospitalId}")
    public ResponseEntity<?> updateMapping(@PathVariable("patientId") String patientId,@PathVariable("hospitalId") String hospitalId){
        //tring patientId=jwtService.extractID(token);
        // System.out.print(token);
        String status= centralServerService.getHospitalAndPatient(patientId,hospitalId);
        return ResponseEntity.ok(status);
    }

}
