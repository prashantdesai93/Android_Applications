package com.mad.practice.inclass03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    static String STUDENT_KEY ="Student";
    private RadioGroup rgDepartment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");
        final EditText etName = findViewById(R.id.etName);
        final EditText etEmail = findViewById(R.id.etMail);
        rgDepartment = findViewById(R.id.rgDepartment);
        final Button btnSubmit = findViewById(R.id.btnSubmit);
        final SeekBar seekbar = findViewById(R.id.seekBar);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student student =  new Student();
                try{
                    if(etName.getText().toString().length()>0){
                        student.name =etName.getText().toString();
                    }else{
                        etName.setError("Required");
                        throw(new Exception("Required Field not Found"));
                    }

                    if(etEmail.getText().toString().length()>0){
                        String email = etEmail.getText().toString();

                        if(isValidEmail(email)) {
                            student.email =email;
                        }else{
                            etEmail.setError("Valid Email Required");
                            throw(new Exception("Valid Email not Found"));
                        }
                    }else{
                        etEmail.setError("Required");
                        throw(new Exception("Required Field not Found"));
                    }

                    if(seekbar.getProgress()>0){
                        student.mood = seekbar.getProgress()+" % Positive";
                    } else if(seekbar.getProgress()==0){
                        student.mood = "0 % Positive";
                    }

                    student.dept=getDepartment(rgDepartment);

                    Intent intent = new Intent(MainActivity.this,DisplayActivity.class);
                    intent.putExtra(STUDENT_KEY,student);
                    startActivity(intent);

                }catch(Exception e){
                    Log.d("Catch",e.getMessage());
                }


            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
