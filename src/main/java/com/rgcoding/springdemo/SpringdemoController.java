package com.rgcoding.springdemo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpringdemoController {
    static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Autowired
    private SpringdemoRepository springdemoRepository;

    @GetMapping({ "/index", "/" })
    public String index(Model model) {
        ArrayList<Medication> medications = new ArrayList<>(springdemoRepository.readAllItems());
        medications.sort(Comparator.comparing(Medication::getGenericName));
        model.addAttribute("medications", medications);
        return "index";
    }
}
