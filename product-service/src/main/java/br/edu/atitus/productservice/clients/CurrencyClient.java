package br.edu.atitus.productservice.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencty-service", url = "http://localhost:8100")
public interface CurrencyClient {

    @GetMapping("/currency/convert")
    CurrencyResponse getCurrency(@RequestParam String source,@RequestParam String target);

}


