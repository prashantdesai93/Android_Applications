package com.mad.practice.homework02;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class CreateTaskActivity extends AppCompatActivity {

    static String TASK_KEY="Task";
    String titleValue, priorityValue;
    Date dateValue;
    Time timeValue;
    static EditText etTitle = null;
    static EditText etDate = null;
    static EditText etTime =null;
    Tasks newTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle("Create Task");

        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        RadioGroup rg = findViewById(R.id.radioBtnGroup);
        priorityValue="High";

        /*etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                } else {
                }
            }
        });*/

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
/*
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "DatePicker");
                } else {
                }
            }
        });*/

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                Log.d("demo", rb.getText().toString());
                priorityValue = rb.getText().toString();
            }
        });


        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etTitle.getText() != null && etTitle.getText().length() > 0) {
                        titleValue = etTitle.getText().toString();
                    } else {
                        etTitle.setError("Required");
                        throw new Exception("Required");
                    }

                    if (etDate.getText() != null && etDate.getText().length() > 0) {
                        dateValue =Tasks.dtFormat.parse(etDate.getText().toString());
                        Log.i("Date value",dateValue.toString());
                    } else {
                        etDate.setError("Required");
                        throw new Exception("Required");
                    }

                    if (etTime.getText() != null && etTime.getText().length() > 0) {
                        timeValue = new Time(Tasks.timeFormat.parse(etTime.getText().toString()).getTime());
                        Log.i("Time value",timeValue.toString());
                    } else {
                        etTime.setError("Required");
                        throw new Exception("Required");
                    }
                    newTask = new Tasks(titleValue, priorityValue, dateValue, timeValue);
                    Log.i("Task",newTask.toString());
                    Intent i = new Intent();
                    i.putExtra(TASK_KEY, newTask);
                    setResult(RESULT_OK, i);
                    finish();
                }catch(Exception e){
                    setResult(RESULT_CANCELED);
                    Log.i("catch",e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String amPM = (hourOfDay>12)?"PM":"AM";
            hourOfDay = hourOfDay % 12;
            etTime.setText(hourOfDay+ ":"+minute+" "+amPM);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            etDate.setText(month+1+"/"+day+"/"+year);
        }
    }
}



