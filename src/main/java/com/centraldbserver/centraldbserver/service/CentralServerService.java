package com.centraldbserver.centraldbserver.service;

import com.centraldbserver.centraldbserver.DTO.*;
import com.centraldbserver.centraldbserver.entity.*;
import com.centraldbserver.centraldbserver.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CentralServerService {

    @Autowired
    private Consent_request_repo consent_request_repo;
    @Autowired
    private Doctor_info_repo doctor_info_repo;

    @Autowired
    private Hospital_info_repo hospital_info_repo;

    @Autowired
    private Patient_info_repo patient_info_repo;

    @Autowired
    private Patient_Hospital_mapping_repo patient_hospital_mapping_repo;

    @Autowired
    private Environment environment;

    @Autowired
    private Ehr_info_repo ehr_info_repo;

    @Autowired
    private JwtService jwtService;

    @Value("${consentManager.url}")
    private String consentManagerBaseUrl;

    @Value("${consentmanager.client.id}")
    private String consentManagerClientId;

    @Value("${consentmanager.client.secret}")
    private String consentManagerClientSecret;

    private String consentToken;

    private Map<String,String> tokenMap=new HashMap<>();

    //private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public String registerPatient(PatientRegistrationDto patientRegistrationDto){
        Patient_info patient=patient_info_repo.getPatientByEmail(patientRegistrationDto.getPatient_email());
        if(patient==null){
            Patient_info patient_info=new Patient_info();
            patient_info.setPatient_name(patientRegistrationDto.getPatient_name());
            patient_info.setPatient_contact(patientRegistrationDto.getPatient_contact());
            long id=generateID();
            String patientId="PAT_" + id;
            patient_info.setPatient_id(patientId); // String id="PAT_"+UUID.randomUUID().toString();

            patient_info.setPatient_email(patientRegistrationDto.getPatient_email());
            patient_info.setPatient_dob(patientRegistrationDto.getPatient_dob());
            patient_info.setPatient_address(patientRegistrationDto.getPatient_address());
            patient_info.setPatient_gender(patientRegistrationDto.getPatient_gender());
            patient_info.setPatient_emergency_contact(patientRegistrationDto.getPatient_emergency_contact());
            patient_info.setPatient_emergency_contact_name(patientRegistrationDto.getPatient_emergency_contact_name());
            patient_info.setPatient_govtid_type(patientRegistrationDto.getPatient_govtid_type());
            patient_info.setPatient_govtid(patientRegistrationDto.getPatient_govtid());

           // String hash_password = passwordEncoder.encode(patientRegistrationDto.getPatient_password());
            patient_info.setPatient_password(patientRegistrationDto.getPatient_password()); //saving hashed password in database;

            //patient_info.setPatient_password(patientRegistrationDto.getPatient_password());

            //boolean matched = passwordEncoder.matches(plaintextpassword in string, hashedpassword from database);
            try{
                patient_info_repo.save(patient_info);
            }catch(Exception e){
                return null;
            }
            return patient_info.getPatient_id(); //returns this id to PatientAppController
        }
        else{
            return null;
        }

    }

    public String createConsentRequest(String doctorId,String hospitalId,ConsentRequestDTO consentRequestDTO){
        Consent_request consent_request=new Consent_request();
        consent_request.setRequest_info(consentRequestDTO.getRequest_info());
        consent_request.setAccess_purpose(consentRequestDTO.getAccess_purpose());
        consent_request.setHospital_id(hospitalId);
        long id=generateID();
        String consentRequestId="REQ_"+id;
        consent_request.setConsent_request_id(consentRequestId);
        consent_request.setPatient_id(consentRequestDTO.getPatient_id());
        consent_request.setDoctor_id(doctorId);
        consent_request.setRequest_status("Pending");
        consent_request.setCreated_dt(new Date());
        consent_request_repo.save(consent_request);
        return consentRequestId;
    }

    public long generateID(){
        long id=(long) Math.floor(Math.random()*9_000_000_000L)+1_000_000_000L;
        return id;
    }

    public List<ConsentNotificationResponse> getConsentRequests(String patientId){
        List<Consent_request> consentReqList=consent_request_repo.getConsentRequestsForPatient(patientId);
        List<ConsentNotificationResponse> consentRepList=new ArrayList<>();
        if(consentReqList!=null){
            for(Consent_request consent_request:consentReqList){
                ConsentNotificationResponse consentNotificationResponse=new ConsentNotificationResponse();
                consentNotificationResponse.setConsent_request_id(consent_request.getConsent_request_id());
                Doctor_info doctor =doctor_info_repo.getDoctorById(consent_request.getDoctor_id());
                Hospital_info hospital=hospital_info_repo.getHospitalById(consent_request.getHospital_id());
                consentNotificationResponse.setAccess_purpose(consent_request.getAccess_purpose());
                consentNotificationResponse.setRequest_info(consent_request.getRequest_info());
                consentNotificationResponse.setDoctor_name(doctor.getDoctor_name());
                consentNotificationResponse.setHospital_name(hospital.getHospital_name());
                consentRepList.add(consentNotificationResponse);
            }
        }
        return consentRepList;
    }

    public List<String> getHospitals(String patientId){
        List<String> hospitals=patient_hospital_mapping_repo.fetchHospitalIdsByPatientId(patientId);
        return hospitals;
    }

    public String getHospitalAndPatient(String patientId,String hospitalId){
        String hospital=patient_hospital_mapping_repo.getPatientHospitalPair(patientId,hospitalId);
        if(hospital==null){
            Patient_Hospital_mapping patient_hospital_mapping=new Patient_Hospital_mapping();
            patient_hospital_mapping.setPatient_id(patientId);
            patient_hospital_mapping.setHospital_id(hospitalId);
            long id=generateID();
            String mappingId="MP_"+id;
            patient_hospital_mapping.setMapping_id(mappingId);
            patient_hospital_mapping_repo.save(patient_hospital_mapping);
            String msg="First time registration done with this hospital";
            return msg;
        }
        else{
            String msg="Already registered with this hospital";
            return msg;
        }
    }


}
