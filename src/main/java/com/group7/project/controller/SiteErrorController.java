package com.group7.project.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SiteErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpRequest request) {
        try {
            return "error_page";
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "index";
        }
    }
}