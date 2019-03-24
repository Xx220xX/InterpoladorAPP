package com.projects.ufu.lace.interpolador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.ufu.lace.interpolador.interpolacao.Equacao;
import com.projects.ufu.lace.interpolador.interpolacao.InterruptedForceException;
import com.projects.ufu.lace.interpolador.interpolacao.Lagrange;

public class TelaLagrange extends Activity {
    Lagrange lagrange;
    RadioGroup group;
    RadioButton b1, b2;

    TextView result;
    ScrollView result_scr;

    ListView ls_list;
    HorizontalScrollView ls_layout;
    ArrayAdapter<SpannableString> arrayAdapter;

    TextView status_txt, status_int;
    RelativeLayout progressLayout;

    Button voltar;

    long time = -1;

    private Thread calcular = new Thread() {
        @Override
        public void run() {
            try {
                lagrange.gerarPn();
            } catch (InterruptedForceException e) {
                e.printStackTrace();
            }
        }
    };


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_lagrange);
        instanciarRadioGroup();
        instanciarResult();
        instanciarLs();
        instanciarStatus();
        instanciarButton();
        instanciar();
        calcular.start();
    }

    private void instanciarButton() {
        voltar = findViewById(R.id.lagrange_voltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void instanciarStatus() {
        status_txt = findViewById(R.id.lagrange_status);
        progressLayout = findViewById(R.id.lagrange_progres_layout);
        status_int = findViewById(R.id.lagrange_progres_int);

    }

    private void instanciarResult() {
        result = findViewById(R.id.lagrange_results);
        result_scr = findViewById(R.id.lagrange_scrol_result);
        result_scr.setVisibility(View.INVISIBLE);

    }

    private void instanciarLs() {
        ls_layout = findViewById(R.id.lagrange_ls_horizontal_scrollscroll);
        ls_list = findViewById(R.id.lagrange_ls_list);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_layout_two_line);
        ls_list.setAdapter(arrayAdapter);

    }

    private void instanciarRadioGroup() {
        group = findViewById(R.id.lagrange_radio_group);
        b1 = findViewById(R.id.lagrange_b1);
        b2 = findViewById(R.id.lagrange_b2);
        b1.setChecked(true);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selecionarVisuaizacao();
            }
        });
        group.setVisibility(View.INVISIBLE);
    }

    private void selecionarVisuaizacao() {

        if (b1.isChecked()) {
            result_scr.setVisibility(View.VISIBLE);
            ls_layout.setVisibility(View.INVISIBLE);
        } else if (b2.isChecked()) {
            result_scr.setVisibility(View.INVISIBLE);
            ls_layout.setVisibility(View.VISIBLE);
        }
    }

    private void instanciar() {
        lagrange = new Lagrange(Dados.getInstance().getTabela(), Dados.getInstance().getOrdem()) {

            @Override
            public void onFinish(final String polinomio) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),polinomio,Toast.LENGTH_LONG).show();
                        group.setVisibility(View.VISIBLE);
                        result.setText(polinomio);
                        selecionarVisuaizacao();
                        progressLayout.setVisibility(View.INVISIBLE);
                        status_txt.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onFormedLi(final String numerador, final String denominador, final int LI) {
                new Thread() {
                    @Override
                    public void run() {
                        String aux = "L" + LI + "   ";
                        String chao = "\n";
                        int lenth = aux.length();
                        for (int i = 0; i < lenth; i++) {
                            chao += " ";
                        }
                        String str = aux + numerador + chao + denominador;
                        final SpannableString str_fim = new SpannableString(str);
                        str_fim.setSpan(new ForegroundColorSpan(Color.RED), 0, aux.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                       ls_list.post(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.add(str_fim);
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void msgStatus(String msg, final int progress) {
                String fim;
                if (time > 0) {
                    long temp = System.currentTimeMillis() - time;
                    temp = temp * (100 - progress);
                    double tempo = (temp / 10L);
                    tempo /= 100d;
                    fim = calculaTempo(tempo);
                } else {
                    fim = getString(R.string.calculando_tempo);
                }
                time = System.currentTimeMillis();
                final String msgfinal = msg + "\n" + fim;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status_txt.setText(msgfinal);
                        status_int.setText(progress + "%");
                    }
                });
            }
        };
    }

    private String calculaTempo(double tempo) {

        String s = "";

        int minuto = (int) tempo / 60;
        int seg = (int) tempo % 60;
        if (minuto > 0) {
            s += minuto + " " + getString(R.string.minutos);
        }
        if (seg > 0) {
            s += " " + seg + getString(R.string.segundos);
        }
        s += " " + getString(R.string.tempo_restantes);

        return s;
    }

    @Override
    public void onBackPressed() {
        if (calcular.isAlive()) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle(R.string.cancelar_interpolacao);
            dialog.setMessage(getString(R.string.cancelar_interpolacao_interromper));
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.confirmar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    calcular.interrupt();
                    if (calcular.isInterrupted())
                        Toast.makeText(getApplicationContext(), "interrompido", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    dialog.cancel();
                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
            return;
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }

}
