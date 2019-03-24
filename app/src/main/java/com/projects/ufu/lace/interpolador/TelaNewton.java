package com.projects.ufu.lace.interpolador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.ufu.lace.interpolador.interpolacao.Equacao;
import com.projects.ufu.lace.interpolador.interpolacao.InterruptedForceException;
import com.projects.ufu.lace.interpolador.interpolacao.Newton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelaNewton extends Activity {
    Newton newton;

    RelativeLayout progress_layout;
    TextView progress_int;
    ProgressBar progress_bar;

    ListView difdivs;
    ArrayAdapter<SpannableString> difDivs_adapter;

    RadioGroup group;
    RadioButton b1, b2, b3;

    TextView result;
    TextView result_Completo;
    TextView msgStatus;
    ScrollView scrollViewResult;
    ScrollView scrollViewResult_complet;

    String polinomio_simplificado = "";
    SpannableString polinomio_completo;

    Button voltar;


    final Thread calcular = new Thread() {
        @Override
        public void run() {
            try {
                newton.gerarPn();
            } catch (InterruptedForceException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_newton);

        progress_int = findViewById(R.id.newton_progres_int);
        progress_bar = findViewById(R.id.newton_progres_bar);
        progress_layout = findViewById(R.id.newton_progres_layout);

        result = findViewById(R.id.newton_results);
        result_Completo = findViewById(R.id.newton_results_completo);
        msgStatus = findViewById(R.id.newton_status);
        scrollViewResult = findViewById(R.id.newton_scrol_result);
        scrollViewResult_complet = findViewById(R.id.newton_scrol_result_complet);

        voltar = findViewById(R.id.newton_voltar);


        difdivs = findViewById(R.id.newton_lay_difdiv);
        difDivs_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_layout_two_line);
        difdivs.setAdapter(difDivs_adapter);

        b1 = findViewById(R.id.newton_b1);
        b2 = findViewById(R.id.newton_b2);
        b3 = findViewById(R.id.newton_b3);
        b1.setChecked(true);
        group = findViewById(R.id.newton_radio_group);
        group.setVisibility(View.INVISIBLE);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                opcaoDeVisualizacaoMudada();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        instanciar();
        calcular.start();
    }

    private void opcaoDeVisualizacaoMudada() {
        if (b1.isChecked()) {
            difdivs.setVisibility(View.INVISIBLE);
            scrollViewResult_complet.setVisibility(View.INVISIBLE);
            scrollViewResult.setVisibility(View.VISIBLE);

        } else if (b2.isChecked()) {
            scrollViewResult_complet.setVisibility(View.INVISIBLE);
            scrollViewResult.setVisibility(View.INVISIBLE);
            difdivs.setVisibility(View.VISIBLE);
        } else if (b3.isChecked()) {
            difdivs.setVisibility(View.INVISIBLE);
            scrollViewResult_complet.setVisibility(View.VISIBLE);
            scrollViewResult.setVisibility(View.INVISIBLE);
        }
    }

    private void mudarTxtColorir(String regex, String str) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(str);
        polinomio_completo = new SpannableString(str);
        polinomio_completo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, polinomio_completo.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        int start, end;
        while (mt.find()) {
            start = mt.start();
            end = mt.end() - 1;
            polinomio_completo.setSpan(new AbsoluteSizeSpan(22, true), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            polinomio_completo.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        }
    }

    private void instanciar() {

        newton = new Newton(Dados.getInstance().getTabela(), Dados.getInstance().getOrdem()) {
            @Override
            public void onFinish(String polinomio, final String polinomioCompleto) {
                msgStatus("PROCESSANDO...\nAguarde...", -1);
                switch (resultIsConfiable()) {
                    case CONFIAVEL:
                        break;
                    case NAN:
                        polinomio += getString(R.string.divisao_zero_zero);
                        break;
                    case INF:
                        polinomio += getString(R.string.divisao_por_zero);
                        break;
                    case TOTALMENTE_INCONFIAVEL:
                        polinomio += getString(R.string.divisao_zer_zer_e_div_por_zer);
                        break;

                }

                polinomio_simplificado = polinomio;
                mudarTxtColorir(" [+] [(]", polinomioCompleto);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        group.setVisibility(View.VISIBLE);
                        progress_layout.setVisibility(View.INVISIBLE);
                        msgStatus.setVisibility(View.INVISIBLE);
                        b1.setChecked(true);
                        result_Completo.setText(polinomio_completo);
                        result_Completo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        result.setText(polinomio_simplificado);
                        result.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        opcaoDeVisualizacaoMudada();

                    }
                });
            }

            @Override
            protected void setMsgDifdiv(final String str, final double d) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        difDivs_adapter.add(new SpannableString(str + d));
                    }
                });
            }

            @Override
            public void msgStatus(String msg, int porcentagem) {
                String pc = porcentagem + "%";
                if (porcentagem == -1) {
                    pc = "";
                }
                final String msg_ = msg;
                final String finalPc = pc;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgStatus.setText(msg_);
                        progress_int.setText(finalPc);

                    }
                });
            }

        };
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