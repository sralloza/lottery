package es.sralloza.lottery.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DocsController {

    @RequestMapping("/docs")
    public String index() {
        return "docs.html";
    }
}
