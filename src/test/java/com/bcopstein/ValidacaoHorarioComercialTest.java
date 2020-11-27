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
    @Test
    public void validaTresProdutosExistentes() {
        Produtos produtos = mock(Produtos.class);
        when(produtos.recupera(10)).thenReturn(new Produto(10,"Prod10",1000.0));
        when(produtos.recupera(30)).thenReturn(new Produto(30,"Prod30",2000.0));
        when(produtos.recupera(50)).thenReturn(new Produto(50,"Prod15",1500.0));

        Estoque estoque = mock(Estoque.class);
        when(estoque.recupera(10)).thenReturn(new ItemEstoque(10,5));
        when(estoque.recupera(30)).thenReturn(new ItemEstoque(30,3));
        when(estoque.recupera(50)).thenReturn(new ItemEstoque(50,15));

        List<ItemVenda> itens = new ArrayList<>(3);
        itens.add(new ItemVenda(1,10,2,1000));
        itens.add(new ItemVenda(2,30,3,2000));
        itens.add(new ItemVenda(3,50,1,1500));

        RegraValidacao regra = new ValidacaoHorarioComercial();
        assertDoesNotThrow(()->regra.valida(produtos,estoque,itens));
    }
    @ParameterizedTest
    @CsvSource({
        "08:00:00:00",
        "12:00:00:00",
        "19:00:00:00"
    })
    public void getRegraValidacaoHorarioComercialTest(String hora){
        int[] horaInt = new int[4];
        String[] vet = hora.split(":");
        for(int i = 0; i < vet.length; i++){
            horaInt[i] = Integer.parseInt(vet[i]);
        }
        FactoryValidacao fac = new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3]));
        Assertions.assertTrue(fac.getRegraValidacao() instanceof ValidacaoHorarioComercial);
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
        Assertions.assertTrue(fac.getRegraValidacao() instanceof ValidacaoForaHorarioComercial);
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
            "147"
    })
    public void calcularComprasGrandeTest(int resultado){
        RegraImpostoComprasGrandes ricg = new RegraImpostoComprasGrandes();
        ArrayList<ItemVenda> lista = lista_um();
        double valor = ricg.calcular(lista);
        Assertions.assertEquals(resultado,valor);
    }

    @ParameterizedTest
    @CsvSource({
            "112"
    })
    public void calcularComprasOriginalTest(int resultado){
        RegraImpostoOriginal rio = new RegraImpostoOriginal();
        ArrayList<ItemVenda> lista = lista_dois();
        double valor = rio.calcular(lista);
        Assertions.assertEquals(resultado,valor);
    }

    @ParameterizedTest
    @CsvSource({
            "1232,1,00:00",
            "1233,2,00:00"
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
            "1120-112-1232,1,00:00",
            "1120-112-1232,2,00:00"
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
        }else{
            ri = new RegraImpostoOriginal();
        }
        Produtos produtos = mock(Produtos.class);
        Estoque estoque = mock(Estoque.class);
        ServicoDeVendas sv = new ServicoDeVendas(produtos,estoque,ri,new FactoryValidacao(LocalTime.of(horaInt[0],horaInt[1],horaInt[2],horaInt[3])));
        Assertions.assertEquals(resultado.split("-")[0],sv.todosValores(lista_dois())[0]);
    }

    public ArrayList<ItemVenda> lista_um(){
        ItemVenda a = new ItemVenda(1, 1, 2, 120);
        ItemVenda b = new ItemVenda(2, 2, 1, 1000);
        ItemVenda c = new ItemVenda(3, 3, 2, 300);
        ItemVenda d = new ItemVenda(4, 4, 1, 100);
        ArrayList<ItemVenda> lista = new ArrayList<ItemVenda>();
        lista.add(a);
        lista.add(b);
        lista.add(c);
        lista.add(d);
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
