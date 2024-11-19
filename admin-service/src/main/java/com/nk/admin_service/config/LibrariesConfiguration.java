package com.nk.admin_service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.nk.base","com.nk.common","com.nk.admin_service" })
public class LibrariesConfiguration {

}