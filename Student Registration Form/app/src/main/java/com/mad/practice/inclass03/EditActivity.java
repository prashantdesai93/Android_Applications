package com.mad.practice.inclass03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    private RadioGroup rgDepartment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Edit Activity");

        final EditText etName = findViewById(R.id.etName);
        final EditText etEmail = findViewById(R.id.etMail);
        rgDepartment = findViewById(R.id.rgDepartment);
        final SeekBar seekbar = findViewById(R.id.seekBar);
        TextView tvDept = findViewById(R.id.tvDept);
        TextView tvMoodText = findViewById(R.id.tvMoodText);

        etName.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        seekbar.setVisibility(View.INVISIBLE);
        rgDepartment.setVisibility(View.INVISIBLE);
        tvDept.setVisibility(View.INVISIBLE);
        tvMoodText.setVisibility(View.INVISIBLE);

        final Button btnSave = findViewById(R.id.btnSave);

        if(getIntent() != null && getIntent().getExtras() != null ){
            String which =getIntent().getExtras().getString(DisplayActivity.WHICH);

            switch(which){
                case ("Name"):
                    String name =getIntent().getExtras().getString(DisplayActivity.NAME_KEY);
                    etName.setText(name);
                    etName.setVisibility(View.VISIBLE);
                    break;
                case ("Email"):
                    String email =getIntent().getExtras().getString(DisplayActivity.EMAIL_KEY);
                    etEmail.setText(email);
                    etEmail.setVisibility(View.VISIBLE);
                    break;
                case ("Dept"):
                    String dept =getIntent().getExtras().getString(DisplayActivity.DEPT_KEY);
                    switch (dept){
                        case ("SIS"):
                            rgDepartment.check(R.id.rbSIS);
                            break;
                        case ("CS"):
                            rgDepartment.check(R.id.rbCS);
                            break;
                        case ("BIO"):
                            rgDepartment.check(R.id.rbBio);
                            break;
                        case ("OTHERS"):
                            rgDepartment.check(R.id.rbOthers);
                            break;
                    }
                    tvDept.setVisibility(View.VISIBLE);
                    rgDepartment.setVisibility(View.VISIBLE);
                    break;

                case ("Mood"):
                    String progress =getIntent().getExtras().getString(DisplayActivity.MOOD_KEY);
                    progress=progress.replace(" % Positive","");
                    seekbar.setProgress(Integer.parseInt(progress));
                    tvMoodText.setVisibility(View.VISIBLE);
                    seekbar.setVisibility(View.VISIBLE);
                    break;

            }

            btnSave.setOnClickListener(new View.OnClickListener() {
                Intent i = new Intent();
                @Override
                public void onClick(View v) {
                    boolean finish =false;
                    String which =getIntent().getExtras().getString(DisplayActivity.WHICH);
                    switch(which){
                        case ("Name"):
                            String value=etName.getText().toString();
                            if(value!=null && value.length()>0){
                                Intent i = new Intent();
                                i.putExtra(DisplayActivity.NAME_KEY,etName.getText().toString());
                                setResult(RESULT_OK,i);
                                finish=true;
                            }else{
                                etName.setError("Required");
                                setResult(RESULT_CANCELED);
                            }
                            break;
                        case ("Email"):
                            String email=etEmail.getText().toString();
                            if(email!=null && email.length()>0){
                                if(MainActivity.isValidEmail(email)) {
                                    Intent i = new Intent();
                                    i.putExtra(DisplayActivity.EMAIL_KEY,etEmail.getText().toString());
                                    setResult(RESULT_OK,i);
                                    finish=true;
                                }else{
                                    etEmail.setError("Valid Email Required");
                                }
                            }else{
                                etEmail.setError("Required");
                                setResult(RESULT_CANCELED);
                            }
                            break;
                        case ("Dept"):
                            String dept=getDepartment(rgDepartment);
                            if(dept!=null && dept.length()>0){
                                Intent i = new Intent();
                                i.putExtra(DisplayActivity.DEPT_KEY,dept);
                                setResult(RESULT_OK,i);
                                finish=true;
                            }else{
                                setResult(RESULT_CANCELED);
                            }
                            break;
                        case ("Mood"):
                            String mood=seekbar.getProgress()+" % Positive";
                            if(mood!=null && mood.length()>0){
                                Intent i = new Intent();
                                i.putExtra(DisplayActivity.MOOD_KEY,mood);
                                setResult(RESULT_OK,i);
                                finish=true;
                            }else{
                                setResult(RESULT_CANCELED);
                            }
                            break;
                    }
                    if(finish){
                        finish();
                    }
                }
            });
        }
    }

    String getDepartment(RadioGroup rgDepartment){
        String selectedDept="";
        switch(rgDepartment.getCheckedRadioButtonId()){
            case (R.id.rbSIS):
                Log.d("getDepartment","Checked the SIS Radio Button");
                selectedDept = "SIS";
                break;
            case (R.id.rbCS):
                Log.d("getDepartment","Checked the CS Radio Button");
                selectedDept = "CS";
                break;
            case (R.id.rbBio):
                Log.d("getDepartment","Checked the BIO Radio Button");
                selectedDept = "BIO";
                break;
            case (R.id.rbOthers):
                Log.d("getDepartment","Checked the Others Radio Button");
                selectedDept = "OTHERS";
                break;
            case (-1):
                Log.d("getDepartment","Error in radio button");
                selectedDept = "";
                break;
        }
        return selectedDept;
    }
}
