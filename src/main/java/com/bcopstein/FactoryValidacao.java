package com.bcopstein;

import java.time.LocalTime;

public class FactoryValidacao {
    private LocalTime agora;

    // Deve receber o horário corrente (agora) como parâmetro
    public FactoryValidacao(LocalTime agora){
        this.agora = agora;
    }

    public RegraValidacao getRegraValidacao(){
        if (LocalTime.parse("08:00").compareTo(agora) <= 0 &&
            LocalTime.parse("18:00").compareTo(agora) >= 0){
                return new ValidacaoHorarioComercial();
        }else{
            return new ValidacaoForaHorarioComercial();
        }
    } 
}
