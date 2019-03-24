package com.projects.ufu.lace.interpolador.interpolacao;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Equacao {

    private double[] coef;
    private static int casasDecimais = 4;
    private static boolean casasDecimaisNosCalculos = true;

    private static boolean jaFoiInf = false;
    private static boolean jaFoiNan = false;
    private static Cancelable cancelable = new Cancelable() {
        @Override
        public boolean canRun() {
            return !Thread.currentThread().isInterrupted();
        }
    };

    public synchronized static int getCasasDecimais() {
        return casasDecimais;
    }

    public synchronized static void setCasasDecimais(int casasDecimais, boolean inclusiveNosCalculos) {
        Equacao.casasDecimais = casasDecimais;
        casasDecimaisNosCalculos = inclusiveNosCalculos;
    }

    protected synchronized static boolean isJaFoiInf() {
        return jaFoiInf;
    }

    protected synchronized static void set_JaFoiInf() {
        Equacao.jaFoiInf = true;
    }

    protected synchronized static boolean isJaFoiNan() {
        return jaFoiNan;
    }

    protected synchronized static void set_JaFoiNan() {
        Equacao.jaFoiNan = true;
    }

    protected Equacao(double constante, double variavel) throws InterruptedForceException {
        this(new double[]{constante, variavel});
    }

    protected synchronized static int porcentagem(int total, int atual) {
        return (int) (((double) atual * 100d) / (double) total);
    }

    protected static void setConfiable() {
        jaFoiInf = false;
        jaFoiNan = false;
        setCasasDecimais(getCasasDecimais(), casasDecimaisNosCalculos);
    }

    public synchronized static boolean decimalAfetaCalculo() {
        return casasDecimaisNosCalculos;
    }



    protected Equacao(double[] coef) throws InterruptedForceException {
        canRun();
        this.coef = coef;
    }

    protected static String doubleToString(double v) throws InterruptedForceException {
        canRun();
        long b = (long) v;
        if (v - (double) b != 0.0d) {
            String formato = "%." + casasDecimais + "f";
            return String.format(Locale.US, formato, v);
        } else {
            return b + "";
        }
    }

    //4x^4 +6x^3+7x^2+4x+5
    //5,4,7,6,4
    protected synchronized static Equacao multiplicaCoef(Equacao a, double coef, double x) throws InterruptedForceException {
        return multiplicaCoef(a, new Equacao(new double[]{coef, x}));
    }

    private static Equacao multiplicaCoef(Equacao a, Equacao b) throws InterruptedForceException {
        a.simplificar();
        b.simplificar();
        double[] aux = new double[(a.length() + b.length())];
        for (int i = 0; i < b.length(); i++) {
            for (int j = 0; j < a.length(); j++) {
                int i2 = i + j;
                aux[i2] = aux[i2] + (a.getCoef(j) * b.getCoef(i));
            }
        }
        return new Equacao(aux);
    }


    private void simplificaoArredondada() {
        double aux[] = null;
        boolean isValid = false;
        int i = coef.length - 1;
        while (i >= 0) {
            coef[i] = arredondar(coef[i]);
            if (coef[i] == -0.0d) {
                coef[i] = 0.0d;
            } else {
                if (Double.isNaN(coef[i])) {
                    set_JaFoiNan();
                } else if (Double.isInfinite(coef[i])) {
                    set_JaFoiInf();
                }
            }
            if (!isValid && Double.isNaN(coef[i])) {
                coef[i] = 0.0d;
            }
            if (!(coef[i] == 0.0d || isValid)) {
                aux = new double[(i + 1)];
                isValid = true;
            }
            if (isValid) {
                aux[i] = coef[i];
            }
            i--;
        }
        if (aux == null) {
            aux = new double[]{0.0d};
        }
        this.coef = aux;
    }

    private double arredondar(double v) {
        if (Double.isInfinite(v) || Double.isNaN(v)) return v;
        BigDecimal decimal = new BigDecimal(v).setScale(getCasasDecimais(), RoundingMode.HALF_EVEN);
        v = decimal.doubleValue();
        return v;
    }

    private void simplificar() throws InterruptedForceException {
        canRun();
        if (casasDecimaisNosCalculos) {
            simplificaoArredondada();
            return;
        }
        double aux[] = null;
        boolean isValid = false;
        int i = coef.length - 1;
        if (coef[i] != 0.0d && coef[i] != -0.0d && !Double.isNaN(coef[i])) {
            return;
        }
        while (i >= 0) {

            if (coef[i] == -0.0d) {
                coef[i] = 0.0d;
            } else {
                if (Double.isNaN(coef[i])) {
                    set_JaFoiNan();
                } else if (Double.isInfinite(coef[i])) {
                    set_JaFoiInf();
                }
            }
            if (!isValid && Double.isNaN(coef[i])) {
                coef[i] = 0.0d;
            }
            if (!(coef[i] == 0.0d || isValid)) {
                aux = new double[(i + 1)];
                isValid = true;
            }
            if (isValid) {
                aux[i] = coef[i];
            }
            i--;
        }
        if (aux == null) {
            aux = new double[]{0.0d};
        }
        this.coef = aux;
    }

    protected synchronized static Equacao somaCoef(Equacao a, Equacao b) throws InterruptedForceException {
        a.simplificar();
        b.simplificar();
        int ordem = b.length() > a.length() ? b.length() : a.length();
        double[] aux = new double[ordem];
        for (int i = 0; i < ordem; i++) {
            aux[i] = 0.0d;
            if (i < b.length()) {
                aux[i] = aux[i] + b.getCoef(i);
            }
            if (i < a.length()) {
                aux[i] = aux[i] + a.getCoef(i);
            }
        }
        return new Equacao(aux);
    }

    protected synchronized double getCoef(int i) {
        return this.coef[i];
    }

    protected synchronized int length() {
        return this.coef.length;
    }

    @Override
    public synchronized String toString() {
        try {
            simplificar();
            StringBuilder str = new StringBuilder();
            boolean mostrei = false;
            int i = length() - 1;
            double co;
            while (i >= 0) {
                co = coef[i];
                if (co != 0.0d) {
                    if (co < 0) {
                        str.append(" - ");
                        co = -co;
                    } else if (mostrei) {
                        str.append(" + ");
                    }
                    if (co != 1 || i == 0) {
                        str.append(doubleToString(co));
                    }
                    if (i > 0) {
                        str.append("x");
                    }
                    if (i > 1) {
                        str.append("^" + i);
                    }
                    mostrei = true;
                }
                i--;
            }
            return str.toString();
        } catch (InterruptedForceException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static class OrdemRequestException extends Exception {
        public OrdemRequestException(String s) {
            super(s);
        }
    }

    private static void canRun() throws InterruptedForceException {
        if (cancelable != null) {
            if (!cancelable.canRun()) {
                throw new InterruptedForceException();
            }
        }
    }


}



