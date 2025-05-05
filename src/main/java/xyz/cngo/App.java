package xyz.cngo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"xyz.cngo"})
@EnableAspectJAutoProxy     //开启AOP
@Controller
@MapperScan("xyz.cngo.dao")
public class App 
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projectName", "秒杀商城后端");
        model.addAttribute("version", "1.0-SNAPSHOT");
        model.addAttribute("description", "这是一个高性能的秒杀商城后端系统");
        model.addAttribute("apis", List.of(
                "GET /products - 获取商品列表",
                "POST /order - 创建订单",
                "GET /order/{id} - 获取订单详情"
        ));
        return "index";  // 对应src/main/resources/templates/index.html
    }
}
