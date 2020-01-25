package conuhackv.back.controller;

import conuhackv.back.CommonReturnType;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Jingchao Zhang
 * @createDate: 2019/10/20
 **/
@RestController
@RequestMapping("/hello")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class HelloController {

    @GetMapping(value = "/hello")
    public CommonReturnType Hello() {
        return CommonReturnType.create("hello");
    }
}
