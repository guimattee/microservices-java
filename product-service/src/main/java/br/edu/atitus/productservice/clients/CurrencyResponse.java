package br.edu.atitus.productservice.clients;

public record CurrencyResponse(String sourceCurrency, String targetCurrency, Double convertionRate, String environment) {
}
