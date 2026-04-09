package br.edu.atitus.greetingservice.controllers;

import br.edu.atitus.greetingservice.Dtos.GreetingDto;
import br.edu.atitus.greetingservice.configs.GreetingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

//    @Value("${greeting-service.greeting}")
//    private String greeting;
//
//    @Value("{greeting-service.default-name}")
//    private String defaultName;

    private final GreetingConfig config;
    //injecao de dependencia
    public GreetingController(GreetingConfig config) {
        this.config = config;
    }

    @GetMapping({"","/", "{name}"})
    public String getGreeting(@RequestParam (required = false) String name,
                              @PathVariable (required = false, name="name") String namePath) {

        String finalName = name != null ? name : namePath;

        String greetingReturn = String.format("%s %s!!!", config.getGreeting(),finalName = finalName != null ? finalName : config.getDefaultName());
        return greetingReturn;

    }

    @PostMapping
    public String postGreeting(@RequestBody GreetingDto greetingDto) {

        String name = greetingDto.name();
        if (name.isBlank())
            name = config.getDefaultName();

        String greetingReturn = String.format("%s %s!!!", config.getGreeting(),name);
        return greetingReturn;
    }
}
