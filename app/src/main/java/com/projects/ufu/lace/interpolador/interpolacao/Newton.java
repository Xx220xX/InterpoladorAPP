package com.projects.ufu.lace.interpolador.interpolacao;

import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.doubleToString;
import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.multiplicaCoef;
import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.porcentagem;
import static com.projects.ufu.lace.interpolador.interpolacao.Equacao.somaCoef;

public  abstract class Newton  implements ConfiancaChecavel {

    private StringBuilder polinomio;
    private double diferencasDivididas[];
    private Tabela pontos;
    int ordem;

    public Newton(Tabela pontos, int ordem) {
        this.pontos = pontos;
        this.ordem = ordem;

    }

    protected synchronized void calculaDifDiv() {
        diferencasDivididas = new double[ordem + 1];
        diferencasDivididas[0] = pontos.getY(0);
        class Dif {
            double v[] = new double[5];
        }
        class Tabelinha {
            public Tabelinha(Dif[] n) {
                this.n = n;
            }

            Dif n[];
        }
        Tabelinha t[] = new Tabelinha[ordem + 1];
        setMsgDifdiv(gerarDif(0), diferencasDivididas[0]);
        for (int i = 1; i <= ordem; i++) {
            t[i] = new Tabelinha(new Dif[ordem + 1 - i]);
            msgStatus("calculando diferença dividida de ordem " + i, porcentagem(ordem + 1, i));
            for (int j = 0; j < ordem + 1 - i; j++) {
                t[i].n[j] = new Dif();
                if (i == 1) {
                    t[i].n[j].v[0] = pontos.getY(i + j);
                    t[i].n[j].v[1] = pontos.getY(i + j - 1);
                    t[i].n[j].v[2] = pontos.getX(i + j);
                    t[i].n[j].v[3] = pontos.getX(i + j - 1);
                } else {
                    t[i].n[j].v[0] = t[i - 1].n[j + 1].v[4];
                    t[i].n[j].v[1] = t[i - 1].n[j].v[4];
                    t[i].n[j].v[2] = t[i - 1].n[j + 1].v[2];
                    t[i].n[j].v[3] = t[i - 1].n[j].v[3];
                }
                t[i].n[j].v[4] = (t[i].n[j].v[0] - t[i].n[j].v[1]) / (t[i].n[j].v[2] - t[i].n[j].v[3]);
            }
            diferencasDivididas[i] = t[i].n[0].v[4];
            setMsgDifdiv(gerarDif(i), diferencasDivididas[i]);

        }
    }

    public synchronized int resultIsConfiable() {
        int result = CONFIAVEL;
        if (Equacao.isJaFoiNan() && Equacao.isJaFoiInf()) {
            result = TOTALMENTE_INCONFIAVEL;
        } else if (Equacao.isJaFoiInf()) {
            result = INF;
        } else if (Equacao.isJaFoiNan()) {
            result = NAN;
        }
        return result;
    }

    public abstract void onFinish(String polinomio, String polinomioCompleto);

    protected abstract void setMsgDifdiv(String str, double d);

    public abstract void msgStatus(String msg, int Porcentagem);


    /***
     recursive form
     private  double difDiv( int ordemRequerida, int inicial) {
     return ordemRequerida == 0 ? pontos.getY(inicial) : (difDiv( ordemRequerida - 1, inicial + 1) - difDiv( ordemRequerida - 1, inicial)) / (pontos.getX(inicial + ordemRequerida) - pontos.getX(inicial));
     }
     */
    private String gerarDif(int ordem) {
        StringBuilder s = new StringBuilder("F[");
        for (int i = 0; i <= ordem; i++) {
            s.append("x");
            s.append(i);
            if (i < ordem) {
                s.append(",");
            }
        }
        s.append("] = ");
        return s.toString();
    }

    public synchronized void gerarPn() throws InterruptedForceException {

        Equacao.setConfiable();

        if (!pontos.possuiPontos(ordem)) {
            onFinish("ERRO!!!::Pontos insuficientes", "");
            return;
        }
        calculaDifDiv();
        polinomio = new StringBuilder();
        Equacao fim = new Equacao(0d, 0d);
        for (int i = 0; i <= ordem; i++) {
            msgStatus("calculando termo " + i, porcentagem(ordem + 1, i));
            fim = somaCoef(fim, termo(i, pontos, ordem));
            if (i < ordem) {
                polinomio.append(" + ");
            }
        }

        onFinish(fim.toString(), polinomio.toString());
        polinomio = null;
    }

    private Equacao termo(int i, Tabela pontos, int ordem) throws InterruptedForceException {

        double difdiv = difDivOtm(i);//calcula diferenca aki
        Equacao terminho = new Equacao(1d, 0d);

        for (int j = 0; j < i; j++) {
            polinomio.append("(x ");
            polinomio.append((pontos.getX(j) >= 0.0d) ? ("- " + doubleToString(pontos.getX(j))) : ("+ " + (doubleToString(-pontos.getX(j)))));
            polinomio.append(")*");
            terminho = multiplicaCoef(terminho, -pontos.getX(j), 1);
        }
        polinomio.append((difdiv < 0 ? ("(" + doubleToString(difdiv) + ")") : (doubleToString(difdiv))));

        return multiplicaCoef(terminho, difdiv, 0);

    }

    private double difDivOtm(int i) {
        return diferencasDivididas[i];
    }


}