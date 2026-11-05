package notkirill.workshopmanager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {
    @GetMapping({"/", "/{path:^(?!api$|assets$)[^\\.]*}", "/{path:^(?!api$|assets$)[^\\.]*}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
