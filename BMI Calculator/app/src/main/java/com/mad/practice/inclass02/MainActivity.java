package com.mad.practice.inclass02;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/*Assignment # InClass02
* FileName : MainActivity
* FullName : Kiran Korey , Prashant Ravindra Desai
* */
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    double age=0;
    double weight =0;
    double heightFt =0;
    double heightInch =0;
    double totalHeigth =0;
    double BMI=0;

    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etAge = findViewById(R.id.etAge);
        final EditText etWeight = findViewById(R.id.etWeight);
        final EditText etHeightFeet = findViewById(R.id.etHeightFeet);
        final EditText etHeightInch = findViewById(R.id.etHeightInch);
        final TextView tvBMI = findViewById(R.id.tvBMI);
        final TextView tvWeightStatus = findViewById(R.id.tvWeightStatus);
        final TextView tvMessage = findViewById(R.id.tvMessage);
        final TextView tvBmiRange = findViewById(R.id.tvBmiRange);

        final LinearLayout llResult= findViewById(R.id.llResult);
        final LinearLayout llStatus= findViewById(R.id.llStatus);
        final LinearLayout llRange= findViewById(R.id.llRange);
        final LinearLayout llMessage= findViewById(R.id.llMessage);


        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(etAge.getText().toString().length()>0){
                        age = Double.parseDouble(etAge.getText().toString());

                        if(age<18){
                            etAge.setError("Age Should be greater than 18");
                            throw(new Exception("Age Should be greater than 18"));
                        }
                    }else{
                        etAge.setError("Required");
                        throw(new Exception("Required Field not Found"));
                    }
                    if(etWeight.getText().toString().length()>0){
                        weight = Double.parseDouble(etWeight.getText().toString());
                    }else{
                        etWeight.setError("Required");
                        throw(new Exception("Required Field not Found"));
                    }
                    if(etHeightFeet.getText().toString().length()>0){
                        heightFt = Double.parseDouble(etHeightFeet.getText().toString());
                    }else{
                        etHeightFeet.setError("Required");
                        throw(new Exception("Required Field not Found"));
                    }
                    if(etHeightInch.getText().toString().length()>0){
                        heightInch = Double.parseDouble(etHeightInch.getText().toString());
                    }else{
                        //etHeightInch.setError("Required");
                        //throw(new Exception("Required Field not Found"));
                    }
                    totalHeigth =getHeight(heightFt,heightInch);
                    BMI=BMICalculator(weight,totalHeigth);

                    tvBMI.setText("BMI = "+df2.format(BMI)+" ");
                    int index =getIndex(BMI);

                    if(index == 0){
                        underWeight(tvWeightStatus,tvMessage);
                    }else if(index ==1){
                        normal(tvWeightStatus,tvMessage);
                    }else if(index ==2){
                        overWeight(tvWeightStatus,tvMessage);
                    }else if(index ==3){
                        obese(tvWeightStatus,tvMessage);
                    }

                    if(index != -1){
                        showAll(llResult,llStatus,llRange,llMessage);
                    }

                    Log.i("BMI",BMI+"");
                }catch(Exception e){
                    hideAll(llResult,llStatus,llRange,llMessage);
                    Log.e("catch","btnCalculate.setOnClickListener "+e.getMessage());
                }
            }
        });



    }

    protected void showAll(LinearLayout l1,LinearLayout l2,LinearLayout l3,LinearLayout l4){
        l1.setVisibility(View.VISIBLE);
        l2.setVisibility(View.VISIBLE);
        l3.setVisibility(View.VISIBLE);
        l4.setVisibility(View.VISIBLE);
    }
    protected void hideAll(LinearLayout l1,LinearLayout l2,LinearLayout l3,LinearLayout l4){
        l1.setVisibility(View.INVISIBLE);
        l2.setVisibility(View.INVISIBLE);
        l3.setVisibility(View.INVISIBLE);
        l4.setVisibility(View.INVISIBLE);
    }

    protected void underWeight(TextView status, TextView msg){
        status.setText(" (UnderWeight) ");
        status.setTextColor(Color.RED);
        msg.setText("You will need to gain 28.9 lbs to\n" +
                "reach a BMI of 18.5");
    }


    protected void normal(TextView status, TextView msg){
        status.setText(" (Normal) ");
        status.setTextColor(Color.GREEN);
        msg.setText("Keep up the good work !!");
    }


    protected void overWeight(TextView status, TextView msg){
        status.setText(" (OverWeight) ");
        status.setTextColor(Color.rgb(255,165,0));
        msg.setText("You will need to lose 25.8 lbs to\n" +
                "reach a BMI of 25 ");
    }

    protected void obese(TextView status, TextView msg){
        status.setText(" (Obese) ");
        status.setAllCaps(true);
        status.setTextColor(Color.BLUE);
        msg.setText("You will die soon!! Time to pull up your Socks ");
        msg.setAllCaps(true);
        msg.setTextColor(Color.RED);
        msg.setTypeface(null,Typeface.BOLD_ITALIC);

    }

    protected double BMICalculator(double mass, double height){

        double bmi = 703 *(mass / (height *height) );
        return bmi;
    }

    protected double getHeight(double feet,double inch){
        return (feet*12)+ inch;
    }

    protected int getIndex(double bmi){

            if(bmi<18.5) return 0;
            if(bmi>=18.5  && bmi <25) return 1;
            if(bmi >=25 && bmi <30) return 2;
            if(bmi>=30) return 3;
            return -1;
    }

}
