package br.edu.atitus.currencyservice.controllers;

import br.edu.atitus.currencyservice.dtos.CurrencyDTO;
import br.edu.atitus.currencyservice.entities.CurrencyEntity;
import br.edu.atitus.currencyservice.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import java.util.Currency;

@RestController
@RequestMapping("currency")
public class CurrencyController {
    private final CurrencyRepository repository;

    @Value("${server.port}")
    private String port;

    public CurrencyController(CurrencyRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/convert")
    public ResponseEntity<CurrencyDTO> getConvert(
            @RequestParam String source,
            @RequestParam String target) throws Exception {
        source = source.toUpperCase();
        target = target.toUpperCase();
        CurrencyEntity currency = repository.findBySourceCurrencyAndTargetCurrency(source, target)
                .orElseThrow(() -> new Exception("Currency not found"));

        String environment = "Currency Service running on port: " + port;

        CurrencyDTO dto = new CurrencyDTO(
                currency.getSourceCurrency(),
                currency.getTargetCurrency(),
                currency.getConversionRate(),
                environment);


        return ResponseEntity.ok(dto);
    }
}