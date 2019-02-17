package com.mad.practice.homework01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg;
    Double billValue = 0.0;
    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.tip_icons);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        final SeekBar sb = findViewById(R.id.seekBar2);
        sb.setEnabled(false);
        final EditText et = findViewById(R.id.etBillTotal);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    Log.d("demo", s.toString());
                    calculateTip(et, rg.getCheckedRadioButtonId());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rg = findViewById(R.id.rGrp);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                calculateTip(et, checkedId);
            }
        });

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void calculateTip(EditText et, int checkedId){
        //final EditText et = findViewById(R.id.etBillTotal);
        final TextView tipValue = findViewById(R.id.tvTipValue);
        final TextView totalBill = findViewById(R.id.tvTotalTipValue);
        final SeekBar sb = findViewById(R.id.seekBar2);
        sb.setEnabled(false);
        if (et.getText().length() > 0) {
            billValue = Double.parseDouble(et.getText().toString());
            RadioButton rb = findViewById(checkedId);

            if (rb == findViewById(R.id.rb1)) {
                sb.setEnabled(false);
                Double tipAmount = billValue * 0.1;
                tipValue.setText(df2.format(tipAmount)+"");
                totalBill.setText(df2.format(billValue+tipAmount) +"");
            } else if (rb == findViewById(R.id.rb2)) {
                sb.setEnabled(false);
                Double tipAmount = billValue * 0.15;
                tipValue.setText(df2.format(tipAmount)+"");
                totalBill.setText(df2.format(billValue+tipAmount) +"");
            } else if (rb == findViewById(R.id.rb3)) {
                sb.setEnabled(false);
                Double tipAmount = billValue * 0.18;
                tipValue.setText(df2.format(tipAmount)+"");
                totalBill.setText(df2.format(billValue+tipAmount) +"");
            } else if (rb == findViewById(R.id.rb4)) {
                sb.setEnabled(true);
                sb.setMax(50);
                final TextView seekVal = findViewById(R.id.tvSeekValue);
                Double tipAmount = billValue * (sb.getProgress()*0.01);
                tipValue.setText(df2.format(tipAmount)+"");
                totalBill.setText(df2.format(billValue+tipAmount) +"");
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekVal.setText(progress + "%");
                        Double tipAmount = billValue * (progress*0.01);
                        tipValue.setText(df2.format(tipAmount)+"");
                        totalBill.setText(df2.format(billValue+tipAmount) +"");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });

            }else{
                Log.d("demo", "null");
            }
        } else {
            et.setError("Enter Bill Total");
            tipValue.setText("0.00");
            totalBill.setText("0.00");
        }
    }
}
