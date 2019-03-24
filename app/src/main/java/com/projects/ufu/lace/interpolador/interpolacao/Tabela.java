package com.projects.ufu.lace.interpolador.interpolacao;

public  class Tabela {
    private int pontos;
    private double[] x;
    private double[] y;

    public Tabela() {
        this.x = new double[0];
        this.y = new double[0];
        this.pontos = 0;
        atualizar();
    }

    public Tabela(double[] x, double[] y) {
        this.x = x;
        this.y = y;
        atualizar();
    }

    public synchronized void atualizar() {
        this.pontos = this.x.length > this.y.length ? this.y.length : this.x.length;
    }

    public synchronized void delet(int position) {
        position /= 2;
        double[] aux = new double[(this.x.length - 1)];
        int j = 0;
        int j2 = 0;
        for (int i = 0; i < this.x.length; i++) {
            if (i != position) {
                aux[j2] = this.x[i];
                j2++;
            }
        }
        this.x = aux;
        aux = new double[(this.y.length - 1)];
        for (j2 = 0; j2 < this.y.length; j2++) {
            if (j2 != position) {
                aux[j] = this.y[j2];
                j++;
            }
        }
        this.y = aux;
        atualizar();
    }

    public synchronized void deletTabela() {
        this.x = new double[0];
        this.y = new double[0];
    }

    public synchronized int getNumberPontos() {
        atualizar();
        return this.pontos;
    }

    public synchronized Double[] getPontos() {
           /* if (this.x.length == 0) {
                this.x = new double[]{1,2,3};
                this.y = new double[]{9,8,5};
            }*/
        Double[] aux = new Double[(this.x.length * 2)];
        for (int i = 0; i < this.x.length * 2; i++) {
            if (i % 2 == 0) {
                aux[i] = Double.valueOf(this.x[i / 2]);
            } else {
                aux[i] = Double.valueOf(this.y[i / 2]);
            }
        }
        return aux;
    }

    public synchronized double getX(int i) {
        if (this.x[i] == -0.0d) this.x[i] = 0.0d;
        return this.x[i];
    }

    public synchronized double getY(int i) {
        if (this.y[i] == -0.0d) this.y[i] = 0.0d;

        return this.y[i];
    }

    public synchronized void newPoint(double x, double y) {
        double[] aux = new double[(this.x.length + 1)];
        int i;
        for (i = 0; i < this.x.length; i++) {
            aux[i] = this.x[i];
        }
        aux[i] = x;
        this.x = aux;
        aux = new double[(this.x.length + 1)];
        for (i = 0; i < this.y.length; i++) {
            aux[i] = this.y[i];
        }
        aux[i] = y;
        this.y = aux;
        atualizar();
    }

    public synchronized boolean possuiPontos(int n) {
        return n >= 0 && n < this.pontos;
    }

    public synchronized void setX(int position, double x) {
        this.x[position] = x;
    }

    public synchronized void setX(int position, String s) {
        Double valor;
        try {
            valor = Double.valueOf(s);
        } catch (Exception e) {
            valor = Double.valueOf(0.0d);
        }
        this.x[position] = valor.doubleValue();
    }

    public synchronized void setY(int position, double y) {
        this.y[position] = y;
    }

    public synchronized void setY(int position, String s) {
        Double valor;
        try {
            valor = Double.valueOf(s);
        } catch (Exception e) {
            valor = Double.valueOf(0.0d);
        }
        this.y[position] = valor.doubleValue();
    }

}
