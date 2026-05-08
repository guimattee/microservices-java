package br.edu.atitus.productservice.controllers;


import br.edu.atitus.productservice.clients.CurrencyClient;
import br.edu.atitus.productservice.clients.CurrencyResponse;
import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository, CurrencyClient currencyClient) {
        this.repository = repository;
        this.currencyClient = currencyClient;
    }
    @GetMapping("/{idproduct}")

    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long idproduct, @RequestParam String targetCurrency) {
        targetCurrency = targetCurrency.toUpperCase();

        Double convertedPrice = null;
        String environment = "Product-service running on Port: " + port;

        ProductEntity entity = repository.findById(idproduct)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado"));

        if(targetCurrency.equals(entity.getCurrency())) {
            convertedPrice = entity.getPrice();
        } else {
            CurrencyResponse currency = currencyClient.getCurrency(entity.getCurrency(), targetCurrency);
            convertedPrice = entity.getPrice() * currency.conversionRate();
            environment = environment + " - " + currency.environment();
        }


        CurrencyResponse currency = currencyClient.getCurrency(entity.getCurrency(), targetCurrency);




        ProductDTO dto = new ProductDTO(
                entity.getId(),
                entity.getDescription(),
                entity.getBrand(),
                entity.getModel(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getStock(),
                environment,
                convertedPrice,
                targetCurrency
        );

        return ResponseEntity.ok(dto);
    }
}
