package com.projects.ufu.lace.interpolador;

import com.projects.ufu.lace.interpolador.interpolacao.Equacao;
import com.projects.ufu.lace.interpolador.interpolacao.Tabela;

public class Dados {

    int ordem = -1;

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    private Tabela tabela;

    void apagaTd() {
        tabela.deletTabela();
    }

    void addNewPoint(double x, double y) {
        tabela.newPoint(x, y);
    }

    void removePoint(int position) {
        tabela.delet(position);
    }

    Double[] getPointers() {
        return tabela.getPontos();
    }

    void setTabela(Tabela tabela) {
        this.tabela = tabela;
    }


    private static Dados instance;

    public static Dados getInstance() {
        if (instance == null) {
            instance = new Dados(new Tabela(new double[0], new double[0]));
        }
        return instance;
    }

    private Dados(Tabela tabela) {
        this.tabela = tabela;
    }

    public void setPoint(Double value, int position) {
        if (position % 2 == 0) {
            tabela.setX(position / 2, value);
        } else {
            tabela.setY(position / 2, value);
        }
    }

    public void setCasaDecimais(int dec,boolean afetarNosCalculos) {
        Equacao.setCasasDecimais(dec,afetarNosCalculos);
    }

    public int getNumberPoits() {
        return tabela.getNumberPontos();
    }

    public void clearAll() {
        tabela.deletTabela();
    }

    public Tabela getTabela() {
        return tabela;
    }

    public int getOrdem() {
        ordem = ordem < 0 ? getNumberPoits() - 1 : ordem;
        return ordem;
    }

    public int getCasasDecimas() {
        return Equacao.getCasasDecimais();
    }

    public boolean getDecimalAfetaCalculo() {
        return Equacao.decimalAfetaCalculo();
    }
}
