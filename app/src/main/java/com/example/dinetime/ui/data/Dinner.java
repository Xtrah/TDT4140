package com.example.dinetime.ui.data;

public class Dinner {

    private String dato;
    private String deleUtgifter;
    private String gjester;
    private String klokkeslett;
    private String sted;
    private String typeRett;
    private Boolean vegetar;

    public Dinner() {

    }

    public Dinner(String dato, String deleUtgifter, String gjester, String klokkeslett, String
            sted, String typeRett, Boolean vegetar) {
        this.dato = dato;
        this.deleUtgifter = deleUtgifter;
        this.gjester = gjester;
        this.klokkeslett = klokkeslett;
        this.sted = sted;
        this.typeRett = typeRett;
        this.vegetar = vegetar;
    }

    public Boolean getVegetar() {
        return vegetar;
    }

    public String getDato() {
        return dato;
    }

    public String getDeleUtgifter() {
        return deleUtgifter;
    }

    public String getGjester() {
        return gjester;
    }

    public String getKlokkeslett() {
        return klokkeslett;
    }

    public String getSted() {
        return sted;
    }

    public String getTypeRett() {
        return typeRett;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public void setDeleUtgifter(String deleUtgifter) {
        this.deleUtgifter = deleUtgifter;
    }

    public void setGjester(String gjester) {
        this.gjester = gjester;
    }

    public void setKlokkeslett(String klokkeslett) {
        this.klokkeslett = klokkeslett;
    }

    public void setSted(String sted) {
        this.sted = sted;
    }

    public void setTypeRett(String typeRett) {
        this.typeRett = typeRett;
    }

    public void setVegetar(Boolean vegetar) {
        this.vegetar = vegetar;
    }
}
