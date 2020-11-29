package com.bcopstein;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ValidacaoHorarioComercialTest {

    @ParameterizedTest
    @CsvSource({
        "08:00:00:00",
        "12:00:00:00"
    })
    public void getRegraValidacaoHorarioComercialTest(String hora){
        int[] horaInt = new int[4];
        String[] vet = hora.split(":");
        for(int i = 0; i < vet.length; i++){
            horaInt[i] = Integer.parseInt(vet[i]);
        }
        FactoryValidacao fac = new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3]));
        Assertions.assertTrue(fac.getRegraValidacao().getClass() == ValidacaoHorarioComercial.class);
    }

    @ParameterizedTest
    @CsvSource({
        "00:00:00:00"
    })
    public void getRegraValidacaoForaHorarioComercialTest(String hora){
        int[] horaInt = new int[4];
        String[] vet = hora.split(":");
        for(int i = 0; i < vet.length; i++){
            horaInt[i] = Integer.parseInt(vet[i]);
        }
        FactoryValidacao fac = new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3]));
        Assertions.assertTrue(fac.getRegraValidacao().getClass() == ValidacaoForaHorarioComercial.class);
    }

    @ParameterizedTest
    @CsvSource({
        "10"
    })
    public void entradaTest(int quantidade){
        ItemEstoque ie = new ItemEstoque(123, 1);
        int resultado = ie.getQuantidade() + quantidade;
        ie.entrada(quantidade);
        Assertions.assertEquals(resultado,ie.getQuantidade());
    }

    @ParameterizedTest
    @CsvSource({
        "-1"
    })
    public void entradaExceptionTest(int quantidade){
        ItemEstoque ie = new ItemEstoque(123, 1);
        Assertions.assertThrows(new SistVendasException(SistVendasException.Causa.QUANTIDADE_INVALIDA).getClass(),()->{ie.entrada(quantidade);});
    }

    @ParameterizedTest
    @CsvSource({
        "5"
    })
    public void saidaTest(int quantidade){
        ItemEstoque ie = new ItemEstoque(123, 6);
        int resultado = ie.getQuantidade() - quantidade;
        ie.saida(quantidade);
        Assertions.assertEquals(resultado,ie.getQuantidade());
    }

    @ParameterizedTest
    @CsvSource({
        "-1"
    })
    public void saidaQuantInvTest(int quantidade){
        ItemEstoque ie = new ItemEstoque(123, 1);
        Assertions.assertThrows(new SistVendasException(SistVendasException.Causa.QUANTIDADE_INVALIDA).getClass(),()->{ie.saida(quantidade);});
    }

    @ParameterizedTest
    @CsvSource({
        "10"
    })
    public void saidaQuantInsTest(int quantidade){
        ItemEstoque ie = new ItemEstoque(123, 1);
        Assertions.assertThrows(new SistVendasException(SistVendasException.Causa.QUANTIDADE_INSUFICIENTE).getClass(),()->{ie.saida(quantidade);});
    }

    @ParameterizedTest
    @CsvSource({
            "327"
    })
    public void calcularComprasGrandeTest(int resultado){
        RegraImpostoComprasGrandes ricg = new RegraImpostoComprasGrandes();
        ArrayList<ItemVenda> lista = lista_um();
        double valor = ricg.calcular(lista);
        Assertions.assertEquals(resultado,valor);
    }

    @ParameterizedTest
    @CsvSource({
            "124"
    })
    public void calcularComprasOriginalTest(int resultado){
        RegraImpostoOriginal rio = new RegraImpostoOriginal();
        ArrayList<ItemVenda> lista = lista_dois();
        double valor = rio.calcular(lista);
        Assertions.assertEquals(resultado,valor);
    }

    @ParameterizedTest
    @CsvSource({
            "1232,1,09:00:00:00",
            "1232,2,09:00:00:00",
            "1232,1,19:00:00:00",
            "1232,2,19:00:00:00"
    })
    public void calculaPrecoFinalTest(int resultado,int imposto,String hora){
        int[] horaInt = new int[4];
        String[] vet = hora.split(":");
        for(int i = 0; i < vet.length; i++){
            horaInt[i] = Integer.parseInt(vet[i]);
        }
        RegraImposto ri;
        if(imposto == 1){
           ri = new RegraImpostoComprasGrandes();
        }else{
            ri = new RegraImpostoOriginal();
        }

        Produtos produtos = mock(Produtos.class);
        Estoque estoque = mock(Estoque.class);
        ServicoDeVendas sv = new ServicoDeVendas(produtos,estoque,ri,new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3])));
        Assertions.assertEquals(resultado,sv.calculaPrecoFinal(lista_dois()));
    }
    @ParameterizedTest
    @CsvSource({
            "1120-112-1232,1,09:00:00:00",
            "1120-112-1232,2,09:00:00:00",
            "1120-112-1232,1,19:00:00:00",
            "1120-112-1232,2,19:00:00:00"
    })
    public void todosValoresTest(String resultado,int imposto,String hora){
        int[] horaInt = new int[4];
        String[] vet = hora.split(":");
        for(int i = 0; i < vet.length; i++){
            horaInt[i] = Integer.parseInt(vet[i]);
        }
        RegraImposto ri;
        if(imposto == 1){
            ri = new RegraImpostoComprasGrandes();
        }else if(imposto == 2){
            ri = new RegraImpostoOriginal();
        }else{
            ri = mock(RegraImposto.class);
        }

        Produtos produtos = mock(Produtos.class);
        Estoque estoque = mock(Estoque.class);
        ServicoDeVendas sv = new ServicoDeVendas(produtos,estoque,ri,new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3])));
        Integer[] resultadoInt = new Integer[3];
        resultadoInt[0] = Integer.parseInt(resultado.split("-")[0]);
        resultadoInt[1] = Integer.parseInt(resultado.split("-")[1]);
        resultadoInt[2] = Integer.parseInt(resultado.split("-")[2]);
        Assertions.assertArrayEquals(resultadoInt,sv.todosValores(lista_dois()));
    }

    public ArrayList<ItemVenda> lista_um(){
        ItemVenda a = new ItemVenda(1, 1, 2, 120);
        ItemVenda b = new ItemVenda(2, 2, 1, 1000);
        ItemVenda c = new ItemVenda(3, 3, 2, 300);
        ItemVenda d = new ItemVenda(4, 4, 1, 100);
        ItemVenda e = new ItemVenda(2, 2, 3, 500);
        ArrayList<ItemVenda> lista = new ArrayList<ItemVenda>();
        lista.add(a);
        lista.add(b);
        lista.add(c);
        lista.add(d);
        lista.add(e);
        return lista;
    }

    public ArrayList<ItemVenda> lista_dois(){
        ItemVenda a = new ItemVenda(1, 1, 2, 120);
        ItemVenda b = new ItemVenda(2, 2, 1, 1000);
        ArrayList<ItemVenda> lista = new ArrayList<ItemVenda>();
        lista.add(a);
        lista.add(b);
        return lista;
    }
    
}
