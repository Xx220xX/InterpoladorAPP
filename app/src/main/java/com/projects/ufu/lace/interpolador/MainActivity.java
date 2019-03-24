package com.projects.ufu.lace.interpolador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.ufu.lace.interpolador.interpolacao.Equacao;
import com.projects.ufu.lace.interpolador.interpolacao.Tabela;

public class MainActivity extends Activity {
    GridView table;
    AdpterCustumer<Double> table_adapter;
    Button edit;
    Button interpolar;
    RelativeLayout telona;
    Button help;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table = findViewById(R.id.main_tabela);
        edit = findViewById(R.id.main_edit);
        interpolar = findViewById(R.id.main_interpolar);
        telona = findViewById(R.id.main_tela);
        help = findViewById(R.id.main_help);
        //gerarPontos();
        configuracoes_de_button();
        configurarButtonHelp();
        configuracoes_de_gridView();
        atualizar();
//        startActivity(new Intent(getApplicationContext(),TelaAjuda.class));
//        finish();
    }

    private void configurarButtonHelp() {
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TelaAjuda.class));
                finish();
            }
        });
    }

    private void gerarPontos() {
        int points = 50;
        double[] x = new double[points];
        double[] y = new double[points];
        for (int i = 0; i < x.length; i++) {
            x[i] = i;
            y[i] =i;
        }
        Dados.getInstance().setTabela(new Tabela(x, y));
    }

    private void configuracoes_de_button() {

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selecionarEdicao();

            }
        });
        interpolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder escolherOrdem = new AlertDialog.Builder(getContext());
                escolherOrdem.setTitle(getString(R.string.interpolar_na_forma_de));

                final EditText ordem = new EditText(getApplicationContext());

                ordem.setTextColor(Color.BLACK);

                ordem.setText(Dados.getInstance().getOrdem() + "");
                ordem.setInputType(InputType.TYPE_CLASS_NUMBER);
                escolherOrdem.setView(ordem);
                escolherOrdem.setMessage("insira o grau do polinomio a ser gerado");

                escolherOrdem.setNegativeButton(R.string.lagrange, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                escolherOrdem.setNeutralButton(getString(R.string.newton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                escolherOrdem.setPositiveButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                final AlertDialog dialog = escolherOrdem.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        abrirTeclado(ordem);

                    }
                });

                dialog.show();
                Button btNewton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                btNewton.setOnClickListener(new DialogButtonClickWrapper(dialog) {
                    @Override
                    protected boolean onClicked() {
                        try {
                            fecharTeclado();
                            int grau = Integer.valueOf(ordem.getText().toString());
                            if (grau == 0) {
                                throw new Equacao.OrdemRequestException(getString(R.string.explicacao_para_ordem_zero));
                            } else if (grau >= Dados.getInstance().getNumberPoits()) {
                                throw new Equacao.OrdemRequestException("\n" + getString(R.string.explicacao_ordem_maior_igual_numero_pontos) + "\n" + getString(R.string.total_de_pontos) + Dados.getInstance().getNumberPoits() + getString(R.string.ordem_requerida) + " " + grau);

                            }
                            Dados.getInstance().setOrdem(grau);
                            startActivity(new Intent(getApplicationContext(), TelaNewton.class));
                            finish();
                            return true;
                        } catch (NumberFormatException e) {
                            msgToast(R.string.valor_invalido);
                        } catch (Equacao.OrdemRequestException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });
                Button btLagrange = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                btLagrange.setOnClickListener(new DialogButtonClickWrapper(dialog) {
                    @Override
                    protected boolean onClicked() {
                        try {
                            fecharTeclado();
                            int grau = Integer.valueOf(ordem.getText().toString());
                            if (grau == 0) {
                                throw new Equacao.OrdemRequestException(getString(R.string.explicacao_para_ordem_zero));
                            } else if (grau >= Dados.getInstance().getNumberPoits()) {
                                throw new Equacao.OrdemRequestException("\n" + getString(R.string.explicacao_ordem_maior_igual_numero_pontos) + "\n" + getString(R.string.total_de_pontos) + Dados.getInstance().getNumberPoits() + getString(R.string.ordem_requerida) + " " + grau);

                            }
                            Dados.getInstance().setOrdem(grau);
                            startActivity(new Intent(getApplicationContext(), TelaLagrange.class));
                            finish();
                            return true;
                        } catch (NumberFormatException e) {
                            msgToast(R.string.valor_invalido);
                        } catch (Equacao.OrdemRequestException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                });

            }

        });

    }

    private void abrirTeclado(EditText view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void fecharTeclado() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void selecionarEdicao() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Context context = getApplicationContext();
        Button add = new Button(context);
        Button clearAll = new Button(context);
        Button opcoes = new Button(context);
        LinearLayout layout = new LinearLayout(context);

        add.setText(R.string.adicionar_um_novo_ponto);
        clearAll.setText(R.string.apagar_todos_pontos);
        opcoes.setText(R.string.opcoes);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(add);
        if (Dados.getInstance().getNumberPoits() > 0)
            layout.addView(clearAll);
        layout.addView(opcoes);

        builder.setView(layout);
        builder.setNegativeButton(R.string.voltar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                adiciocionarPonto();
            }
        });
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                apagarTd();
            }
        });
        opcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                config();
            }
        });

        alertDialog.show();
    }

    private void config() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText casasDecimas = new EditText(getApplicationContext());
        final Switch afetaCalculo = new Switch(getApplicationContext());
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        casasDecimas.setInputType(InputType.TYPE_CLASS_NUMBER);
        casasDecimas.setText(Dados.getInstance().getCasasDecimas() + "");
        casasDecimas.setTextColor(Color.BLACK);

        afetaCalculo.setChecked(Dados.getInstance().getDecimalAfetaCalculo());
        afetaCalculo.setText(R.string.arredondar_durante_calculos);
        afetaCalculo.setTextColor(Color.BLACK);
        afetaCalculo.setGravity(Gravity.RIGHT);
        afetaCalculo.setMinHeight((int) getResources().getDimension(R.dimen.dimension_50_dp));

        layout.addView(casasDecimas);
        layout.addView(afetaCalculo);

        builder.setView(layout);
        builder.setTitle(R.string.casa_apos_virgula);
        builder.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int dec = Dados.getInstance().getCasasDecimas();
                boolean afet = afetaCalculo.isChecked();
                try {
                    dec = Integer.valueOf(casasDecimas.getText().toString());
                } catch (NumberFormatException e) {
                    msgToast(R.string.valor_invalido);
                }
                Dados.getInstance().setCasaDecimais(dec, afet);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fecharTeclado();
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void apagarTd() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.apagar_todos_os_pontos));
        builder.setMessage(getString(R.string.apagar_));
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dados.getInstance().clearAll();
                atualizar();
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void adiciocionarPonto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LinearLayout telinha = new LinearLayout(getApplicationContext());
        telinha.setOrientation(LinearLayout.VERTICAL);

        TextView textView_x = new TextView(getApplicationContext());
        TextView textView_y = new TextView(getApplicationContext());
        textView_x.setText(R.string.valor_de_x);
        textView_y.setText(R.string.valor_de_y);
        textView_x.setTextColor(Color.BLACK);
        textView_y.setTextColor(Color.BLACK);
        textView_x.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView_y.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


        final EditText editText_x = new EditText(getApplicationContext());
        final EditText editText_y = new EditText(getApplicationContext());
        editText_x.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText_y.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText_x.setTextColor(Color.BLACK);
        editText_y.setTextColor(Color.BLACK);

        telinha.addView(textView_x);
        telinha.addView(editText_x);
        telinha.addView(textView_y);
        telinha.addView(editText_y);
        builder.setView(telinha);


        builder.setPositiveButton(R.string.adicionar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    double x = Double.valueOf(editText_x.getText().toString()),
                            y = Double.valueOf(editText_y.getText().toString());
                    Array_add(x, y);
                    dialog.cancel();
                } catch (NumberFormatException e) {
                    msgToast(getString(R.string.valor_invalido));
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText_x, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();

    }

    private void Array_add(final double x, final double y) {
        Dados.getInstance().addNewPoint(x, y);
        table_adapter.add(new Double(x));
        table_adapter.add(new Double(y));
    }

    private Context getContext() {
        return this;
    }

    boolean gridviewIslongClick = false;

    private void configuracoes_de_gridView() {
        //Dados.getInstance().setTabela(new Equacao.Tabela(new double[]{1, 2, 3, 8, 6, 4, 5, 6, 2, 6, 8}, new double[]{1, 4, 9, 8, 6, 4, 5, 6, 2, 6, 8}));
        table_adapter = new AdpterCustumer<>(getApplicationContext(), R.layout.txt);
        table.setAdapter(table_adapter);
        table.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (gridviewIslongClick) {
                    gridviewIslongClick = false;
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final EditText valor = new EditText(getApplicationContext());
                valor.setText(((TextView) view).getText());
                valor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valor.setMinHeight((int) getResources().getDimension(R.dimen.dimension_50_dp));
                valor.setTextColor(Color.BLACK);
                valor.setGravity(Gravity.BOTTOM);

                builder.setView(valor);
                builder.setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Double value = Double.valueOf(valor.getText().toString());
                            Dados.getInstance().setPoint(value, position);
                            table_adapter.setItem(value, position);
                            dialog.cancel();
                        } catch (NumberFormatException e) {
                            msgToast(getString(R.string.valor_invalido));
                        } finally {
                            fecharTeclado();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fecharTeclado();
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(valor, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                dialog.show();
            }
        });
        table.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                gridviewIslongClick = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.apagar_par_de_pontos);
                builder.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dados.getInstance().removePoint(position);
                        table_adapter.removeItem(position);
                        msgToast(getString(R.string.ponto_apagado));
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });

    }

    private void msgToast(int resId) {
        msgToast(getString(resId));
    }

    private void msgToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    void atualizar() {
        table_adapter.clear();
        if (Dados.getInstance().getNumberPoits() > 0) {
            for (Double a : Dados.getInstance().getPointers()) {
                table_adapter.add(a);
            }
        }
    }


}
