package com.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                //정적인 리소스를 처리하기 위해 사용되는 핸들러
                //특정 요청에 대한 리소스를 컨트롤하기 위해선 리소스 핸들러를 정의해서 config에 등록해야한다.
                //아래 코드를 통해 어느 경로로 들어왔을 떄 매핑시킬건지 정의한다.
                .addResourceHandler("/images/**")  // URL 경로
                //실제 파일이 있는 경로를 설정하는 메서드(로컬)
                //System.getProperty("user.dir")는 현재 프로젝트의 루트 디렉토리 절대 경로
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/fileImage/");
    }
}