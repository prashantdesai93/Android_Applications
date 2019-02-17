package com.mad.practice.inclass03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    static int EDIT_NAME =1;
    static int EDIT_EMAIL =2;
    static int EDIT_DEP =3;
    static int EDIT_MOOD =4;

    static String NAME_KEY = "Name";
    static String EMAIL_KEY = "Email";
    static String DEPT_KEY = "Dept";
    static String MOOD_KEY = "Mood";

    static String WHICH = "Name1";

    Student student=null;
    private TextView tvNameValue ;
    private TextView tvEmailValue ;
    private TextView tvDepartmentValue ;
    private TextView tvMoodValue  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setTitle("Display Activity");
         tvNameValue = findViewById(R.id.tvNameValue);
         tvEmailValue = findViewById(R.id.tvEmailValue);
         tvDepartmentValue = findViewById(R.id.tvDepartmentValue);
         tvMoodValue = findViewById(R.id.tvMoodValue);

        final ImageView imageView1 = findViewById(R.id.imageView1);
        final ImageView imageView2 = findViewById(R.id.imageView2);
        final ImageView imageView3 = findViewById(R.id.imageView3);
        final ImageView imageView4 = findViewById(R.id.imageView4);



        if(getIntent() != null && getIntent().getExtras() != null ){
            student = (Student) getIntent().getExtras().getSerializable(MainActivity.STUDENT_KEY);
            tvNameValue.setText(student.name);
            tvNameValue.setRight(View.TEXT_ALIGNMENT_TEXT_START);
            tvEmailValue.setText(student.email);
            tvEmailValue.setRight(View.TEXT_ALIGNMENT_TEXT_START);
            tvDepartmentValue.setText(student.dept);
            tvDepartmentValue.setRight(View.TEXT_ALIGNMENT_TEXT_START);
            tvMoodValue.setText(student.mood);
            tvMoodValue.setRight(View.TEXT_ALIGNMENT_TEXT_START);

        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.mad.practice.inclass03.intent.action.EDIT");
                i.putExtra(DisplayActivity.NAME_KEY,tvNameValue.getText().toString());
                i.putExtra(DisplayActivity.WHICH,DisplayActivity.NAME_KEY);
                startActivityForResult(i,EDIT_NAME);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.mad.practice.inclass03.intent.action.EDIT");
                i.putExtra(DisplayActivity.EMAIL_KEY,tvEmailValue.getText().toString());
                i.putExtra(DisplayActivity.WHICH,DisplayActivity.EMAIL_KEY);
                startActivityForResult(i,EDIT_EMAIL);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.mad.practice.inclass03.intent.action.EDIT");
                i.putExtra(DisplayActivity.DEPT_KEY,tvDepartmentValue.getText().toString());
                i.putExtra(DisplayActivity.WHICH,DisplayActivity.DEPT_KEY);
                startActivityForResult(i,EDIT_DEP);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.mad.practice.inclass03.intent.action.EDIT");
                i.putExtra(DisplayActivity.MOOD_KEY,tvMoodValue.getText().toString());
                i.putExtra(DisplayActivity.WHICH,DisplayActivity.MOOD_KEY);
                startActivityForResult(i,EDIT_MOOD);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==EDIT_NAME){
            if(resultCode==RESULT_OK){
                String value = data.getExtras().getString(NAME_KEY);
                student.name=value;
                tvNameValue.setText(student.name);
                Log.i("Result",value);
            }else if(resultCode == RESULT_CANCELED){
                Log.i("Result","No Value Recieved");
            }
        }
        if(requestCode==EDIT_EMAIL){
            if(resultCode==RESULT_OK){
                String value = data.getExtras().getString(EMAIL_KEY);
                student.email=value;
                tvEmailValue.setText(student.email);
                Log.i("Result",value);
            }else if(resultCode == RESULT_CANCELED){
                Log.i("Result","No Value Recieved");
            }
        }
        if(requestCode==EDIT_DEP){
            if(resultCode==RESULT_OK){
                String value = data.getExtras().getString(DEPT_KEY);
                student.dept=value;
                tvDepartmentValue.setText(student.dept);
                Log.i("Result",value);
            }else if(resultCode == RESULT_CANCELED){
                Log.i("Result","No Value Recieved");
            }
        }
        if(requestCode==EDIT_MOOD){
            if(resultCode==RESULT_OK){
                String value = data.getExtras().getString(MOOD_KEY);
                student.mood=value;
                tvMoodValue.setText(student.mood);
                Log.i("Result",value);
            }else if(resultCode == RESULT_CANCELED){
                Log.i("Result","No Value Recieved");
            }
        }
    }
}
