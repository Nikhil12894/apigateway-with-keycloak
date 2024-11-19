package com.nk.user_service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.nk.base","com.nk.common","com.nk.user_service" })
public class LibrariesConfiguration {

}