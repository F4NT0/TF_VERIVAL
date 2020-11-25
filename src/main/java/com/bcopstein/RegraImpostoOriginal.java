package com.bcopstein;

import java.util.List;

public class RegraImpostoOriginal implements RegraImposto {
    @Override
    public double calcular(List<ItemVenda> itens) {
        double soma = itens.stream().mapToDouble(it->it.getValorVendido()).sum();
        //todo: possivel erro (nao deveria ser 0.1?)
        return soma * 0.01;
    }
}
