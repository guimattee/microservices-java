package br.edu.atitus.productservice.controllers;


import br.edu.atitus.productservice.clients.CurrencyClient;
import br.edu.atitus.productservice.clients.CurrencyResponse;
import br.edu.atitus.productservice.dtos.ProductDTO;
import br.edu.atitus.productservice.entities.ProductEntity;
import br.edu.atitus.productservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductRepository repository;
    private final CurrencyClient currencyClient;
    private final CacheManager cacheManager;

    @Value("${server.port}")
    private String port;

    public ProductController(ProductRepository repository, CurrencyClient currencyClient, CacheManager cacheManager) {
        this.repository = repository;
        this.currencyClient = currencyClient;
        this.cacheManager = cacheManager;
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
            String nameCache="ConvertedValue";
            String keyCache= entity.getCurrency() + "-" + targetCurrency;
            Double convertedValue = cacheManager.getCache(nameCache).get(keyCache, Double.class);
            if (convertedValue == null) {
                CurrencyResponse currency = currencyClient.getCurrency(entity.getCurrency(), targetCurrency);
                    if (currency != null) {
                        convertedPrice = entity.getPrice() * currency.convertionRate();
                        environment = environment + " - " + currency.environment();
                        cacheManager.getCache(nameCache).put(keyCache, currency.convertionRate());
                    } else {
                        convertedPrice = - 1.0;
                        environment = environment + " - Currency Fallback";
                    }
            } else {
                convertedPrice = convertedValue * entity.getPrice();
                environment = environment + " - currency in cache";
            }

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
