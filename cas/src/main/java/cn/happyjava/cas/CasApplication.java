package cn.happyjava.cas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.happyjava.cas.mapper")
public class CasApplication {

    public static void main(String[] args)  {
        SpringApplication.run(CasApplication.class, args);
    }

}
