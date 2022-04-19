package com.centraldbserver.centraldbserver.config;

import com.centraldbserver.centraldbserver.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Value("${patientapp.clientId}")
    private String patientAppClientId;

    @Value("${hospitalapp.clientId}")
    private String hospitalAppClientId;

    //@Autowired
    //private PatientAppService patientAppService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth=request.getHeader("Authorization");
        if(auth!=null && !"".equals(auth) && auth.startsWith("Bearer ")){
            String subject=jwtService.extractID(auth);
            if(subject!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                if(subject.equals(patientAppClientId) || subject.equals(hospitalAppClientId)){
                    UsernamePasswordAuthenticationToken ut=new UsernamePasswordAuthenticationToken(subject,null,null);
                    ut.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(ut);
                }

                    //Patient_info patient=patientAppService.getPatientById(subject)

            }

        }
        filterChain.doFilter(request,response);
    }
}
