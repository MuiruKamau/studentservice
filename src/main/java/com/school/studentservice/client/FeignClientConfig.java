package com.school.studentservice.client;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientConfig.class); // Logger

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            logger.debug("FeignClientInterceptor: Intercepting Feign request..."); // Log interceptor execution

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                logger.debug("FeignClientInterceptor: RequestAttributes found."); // Log attributes found

                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null) {
                    logger.debug("FeignClientInterceptor: Authorization header found in original request: {}", authorizationHeader); // Log header found

                    requestTemplate.header("Authorization", authorizationHeader);
                    logger.debug("FeignClientInterceptor: Authorization header added to Feign request."); // Log header added
                } else {
                    logger.debug("FeignClientInterceptor: Authorization header NOT found in original request."); // Log header not found
                }
            } else {
                logger.debug("FeignClientInterceptor: RequestAttributes NOT found (RequestContextHolder.getRequestAttributes() is null)."); // Log attributes not found
            }
        };
    }
}

//package com.school.studentservice.client;
//
//import feign.RequestInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Configuration
//public class FeignClientConfig {
//
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attributes != null) {
//                HttpServletRequest request = attributes.getRequest();
//                String authorizationHeader = request.getHeader("Authorization");
//                if (authorizationHeader != null) {
//                    requestTemplate.header("Authorization", authorizationHeader);
//                }
//            }
//        };
//    }
//}
