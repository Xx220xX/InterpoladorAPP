package com.projects.ufu.lace.interpolador.interpolacao;

import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.doubleToString;
import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.multiplicaCoef;
import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.porcentagem;

public   abstract class Lagrange implements ConfiancaChecavel {
    int ordem;
    Tabela pontos;

    public Lagrange(Tabela tabela, int grauDopolinomio) {
        this.ordem = grauDopolinomio;
        pontos = tabela;
    }


    public abstract void onFinish(String L_polinomio);

    public abstract void onFormedLi(String numerador, String denominador, int LI);

    public abstract void msgStatus(String msg, int progress);

    private String getFormatDouble(double v) throws InterruptedForceException {
        String str = "";
        if (v < 0) {
            str += "- ";
            v = -v;
        } else {
            str += "+ ";
        }
        str += doubleToString(v);
        return str;
    }

    private Equacao gerarLi(int i, int ordem, Tabela pontos) throws InterruptedForceException {//gera o termo Li
        Equacao numerador = new Equacao(new double[]{1});
        double denominador = 1;

        StringBuilder str = new StringBuilder();
        StringBuilder strden = new StringBuilder();

        for (int j = 0; j <= ordem; j++) {
            if (j == i) continue;
//                (x - x1)(x - x2)(x - x4)/
//                (x3 - x1)(x3 - x2)(x3 - x4)
            str.append("(x " + getFormatDouble(-pontos.getX(j)) + ")");
            strden.append("(" + doubleToString(pontos.getX(i)) + " " + getFormatDouble(-pontos.getX(j)) + ")");
            if (j < ordem && !(i == ordem && j + 1 == ordem)) {
                str.append(" * ");
                strden.append(" * ");
            }
            numerador = multiplicaCoef(numerador, -pontos.getX(j), 1);
            denominador *= pontos.getX(i) - pontos.getX(j);
        }
        onFormedLi(str.toString(), strden.toString(), i);
        return multiplicaCoef(numerador, pontos.getY(i) / denominador, 0);
    }


    public synchronized void gerarPn() throws InterruptedForceException {
        Equacao.setConfiable();
        if (pontos.possuiPontos(ordem)) {
            Equacao polinomioDelaGrange = new Equacao(new double[]{0});
            for (int i = 0; i <= ordem; i++) {
                msgStatus("gerando " + " L" + i, porcentagem(ordem + 1, i));
                polinomioDelaGrange = Equacao.somaCoef(polinomioDelaGrange, gerarLi(i, ordem, pontos));
            }
            onFinish(polinomioDelaGrange.toString());
        } else {
            onFinish("ERRO!!!");
        }
    }

}
