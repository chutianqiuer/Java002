package com.mall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mall.payment", "com.mall.common"})
@MapperScan("com.mall.payment.mapper")
public class MallPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPaymentApplication.class, args);
    }
}
