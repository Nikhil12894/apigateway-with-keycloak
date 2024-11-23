package com.nk.apigateway.config;

//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Collections;
//import java.util.Map;
////
////@Configuration
////@ConfigurationProperties(prefix = "custom.security")
////@Setter
////public class SecurityProperties {
////
////    private String whitelistUrl;
////    private Map<String, String> roles; // <client_id, client_secret>
////
////    public Map<String, String> getRoles() {
////        if(roles != null) {
////          return  roles;
////        }
////        return Collections.emptyMap();
////    }
////
////    public String[] getRequestPermitAllPatterns() {
////        return whitelistUrl !=null ? whitelistUrl.split(",") : null;
////    }
////
////}