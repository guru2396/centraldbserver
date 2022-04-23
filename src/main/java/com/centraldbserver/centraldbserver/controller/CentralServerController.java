package com.centraldbserver.centraldbserver.controller;

import com.centraldbserver.centraldbserver.DTO.*;
import com.centraldbserver.centraldbserver.entity.Consent_request;
import com.centraldbserver.centraldbserver.entity.Doctor_info;
import com.centraldbserver.centraldbserver.entity.Hospital_info;
import com.centraldbserver.centraldbserver.entity.Patient_info;
import com.centraldbserver.centraldbserver.service.CentralServerService;
import com.centraldbserver.centraldbserver.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CentralServerController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CentralServerService centralServerService;

    @Value("${patientapp.clientId}")
    private String patientAppClientId;

    @Value("${patientapp.clientSecret}")
    private String patientAppClientSecret;

    @Value("${hospitalapp.clientId}")
    private String hospitalAppClientId;

    @Value("${hospitalapp.clientSecret}")
    private String hospitalAppClientSecret;

    @PostMapping(value="/register-patient")
    public ResponseEntity<?> register(@RequestBody PatientRegistrationDto patientRegistrationDto){
        String id=centralServerService.registerPatient(patientRegistrationDto);

        return ResponseEntity.ok(id);
    }

    @GetMapping(value="/get-consent-notifications/{patientId}")
    public ResponseEntity<?> getConsentRequests(@PathVariable("patientId") String patientId){
        //tring patientId=jwtService.extractID(token);
        System.out.print(patientId);
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

    @PostMapping(value = "/add-doctor/{email}")
    public ResponseEntity<?> addDoctor(@PathVariable("email") String email){
        String doctorid= centralServerService.addDoctor(email);
        return ResponseEntity.ok(doctorid);
    }

    @PostMapping(value = "/register-doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegistrationDto doctorRegistrationDto){
        String msg= centralServerService.registerDoctor(doctorRegistrationDto);
        return ResponseEntity.ok(msg);
    }

    @GetMapping(value="/get-doctor/{doctorId}")
    public ResponseEntity<?> getDoctor(@PathVariable("doctorId") String doctorId){
        Doctor_info doctor_info= centralServerService.getDoctorById(doctorId);
        return ResponseEntity.ok(doctor_info);
    }

    @GetMapping(value="/get-patient/{patientId}")
    public ResponseEntity<?> getPatient(@PathVariable("patientId") String patientId){
        Patient_info patient_info = centralServerService.getPatientById(patientId);
        return ResponseEntity.ok(patient_info);
    }

    @GetMapping(value="/get-ehrId/{patientId}")
    public ResponseEntity<?> getEhrId(@PathVariable("patientId") String patientId){
        String ehrid = centralServerService.getEhrByPatientId(patientId);
        return ResponseEntity.ok(ehrid);
    }

    @GetMapping(value="/get-consentrequest/{consentRequestId}")
    public ResponseEntity<?> getConsentRequestById(@PathVariable("consentRequestId") String id){
        Consent_request consent_request = centralServerService.getConsentRequestById(id);
        return ResponseEntity.ok(consent_request);
    }

    @PostMapping(value = "/update-consentrequest/{consentRequestId}")
    public ResponseEntity<?> updateConsentRequest(@PathVariable("consentRequestId") String id){
        String msg= centralServerService.updateConsentRequest(id);
        return ResponseEntity.ok(msg);
    }

    @GetMapping(value="/get-hospital/{hospitalId}")
    public ResponseEntity<?> getHospital(@PathVariable("hospitalId") String hospitalId){
        Hospital_info hospital_info= centralServerService.getHospitalById(hospitalId);
        return ResponseEntity.ok(hospital_info);
    }

    @PostMapping(value="/patientapp-authenticate")
    public ResponseEntity<?> patientAppAuthenticate(@RequestBody AuthRequestDto authRequestDto){
        if(patientAppClientId.equals(authRequestDto.getUsername()) && patientAppClientSecret.equals(authRequestDto.getPassword())){
            String token= jwtService.createToken(patientAppClientId);
            return ResponseEntity.ok(token);
        }
        else{
            ResponseEntity<String> response=new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            return response;
        }
    }

    @PostMapping(value="/hospitalapp-authenticate")
    public ResponseEntity<?> hospitalAppAuthenticate(@RequestBody AuthRequestDto authRequestDto){
        if(hospitalAppClientId.equals(authRequestDto.getUsername()) && hospitalAppClientSecret.equals(authRequestDto.getPassword())){
            String token= jwtService.createToken(hospitalAppClientId);
            return ResponseEntity.ok(token);
        }
        else{
            ResponseEntity<String> response=new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
            return response;
        }
    }




}
