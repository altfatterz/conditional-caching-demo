package com.example;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class ConditionalCachingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConditionalCachingDemoApplication.class, args);
    }

}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

    final CustomerService customerService;

    @GetMapping("/customer")
    public Customer getCustomer() {
        return customerService.findOne();
    }

}

@Component("customKeyGenerator")
@Log4j2
class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("CustomKeyGenerator called with '{}'", username);
        return new SimpleKey(username);
    }
}

@Service
@Log4j2
@CacheConfig(keyGenerator = "customKeyGenerator")
class CustomerService {

    @Cacheable(value = "customers", unless = "@monitoring.isMonitoringUser()")
    public Customer findOne() {
        log.info("CustomerService was called");
        return new Customer("John Doe");
    }
}

@Component(value = "monitoring")
@Log4j2
class Monitoring {

    @Value("${caching.disable.users:#{T(java.util.Collections).emptyList()}}")
    private List<String> users;

    public boolean isMonitoringUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // do not cache
        if (users.contains(name)) return true;

        return false;
    }
}

@Getter
@NoArgsConstructor
class Customer {

    String name;

    public Customer(String name) {
        this.name = name;
    }
}