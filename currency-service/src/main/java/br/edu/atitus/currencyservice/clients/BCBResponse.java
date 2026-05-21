package br.edu.atitus.currencyservice.clients;

import java.util.List;

public record BCBResponse(List<BCBCurrencies> value) {
    public record BCBCurrencies(Double cotacaoVenda){

    }

}
