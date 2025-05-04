package xyz.cngo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"xyz.cngo"})
@EnableAspectJAutoProxy     //开启AOP
@RestController
@MapperScan("xyz.cngo.dao")
public class App 
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping("/")
    public String home(){
        return "Hello World!";
    }

//    @RequestMapping("/error")
//    public String error(){
//        return "404";
//    }
}
