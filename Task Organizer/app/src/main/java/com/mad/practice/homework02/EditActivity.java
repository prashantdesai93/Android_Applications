package com.mad.practice.homework02;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditActivity extends AppCompatActivity {

    String titleValueEdit, priorityValueEdit;
    Date dateValueEdit;
    Time timeValueEdit;

    static String TASK_KEY_EDIT = "taskEdit";

    static EditText etTitleEdit = null;
    static EditText etDateEdit = null;
    static EditText etTimeEdit =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setTitle("Edit Task");

        etTitleEdit = findViewById(R.id.etTitleEdit);
        etDateEdit = findViewById(R.id.etDateEdit);
        etTimeEdit = findViewById(R.id.etTimeEdit);
        RadioGroup rg = findViewById(R.id.radioBtnGroupEdit);

        if(getIntent() != null && getIntent().getExtras() != null ){
            Tasks task = (Tasks) getIntent().getExtras().get("task");
            etTitleEdit.setText(task.getTitle());
            etDateEdit.setText(Tasks.dtFormat.format(task.getDate()));
            etTimeEdit.setText(Tasks.timeFormat.format(task.getTime()));
            //tvTaskPriority.setText(curr.getPriority().toString());

            switch (task.getPriority()){
                case ("High"):
                    rg.check(R.id.rbHighEdit);
                    break;
                case ("Medium"):
                    rg.check(R.id.rbMediumEdit);
                    break;
                case ("Low"):
                    rg.check(R.id.rbLowEdit);
                    break;
            }
            priorityValueEdit=task.getPriority();
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                Log.d("demo", rb.getText().toString());
                priorityValueEdit = rb.getText().toString();
            }
        });

        etTimeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "TimePicker");
                } else {
                }
            }
        });

        etDateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "DatePicker");
                } else {
                }
            }
        });

        findViewById(R.id.btnSaveEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (etTitleEdit.getText() != null && etTitleEdit.getText().length() > 0) {
                        titleValueEdit = etTitleEdit.getText().toString();
                    } else {
                        etTitleEdit.setError("Required");
                        throw new Exception("Required");
                    }

                    if (etDateEdit.getText() != null && etDateEdit.getText().length() > 0) {
                        dateValueEdit =Tasks.dtFormat.parse(etDateEdit.getText().toString());
                        Log.i("Date value",dateValueEdit.toString());
                    } else {
                        etDateEdit.setError("Required");
                        throw new Exception("Required");
                    }

                    if (etTimeEdit.getText() != null && etTimeEdit.getText().length() > 0) {
                        timeValueEdit = new Time(Tasks.timeFormat.parse(etTimeEdit.getText().toString()).getTime());
                        Log.i("Time value",timeValueEdit.toString());
                    } else {
                        etTimeEdit.setError("Required");
                        throw new Exception("Required");
                    }
                   Tasks newTask = new Tasks(titleValueEdit, priorityValueEdit, dateValueEdit, timeValueEdit);
                    Log.i("Task",newTask.toString());
                    Intent i = new Intent();
                    i.putExtra(TASK_KEY_EDIT, newTask);
                    i.putExtra("taskIndexEdit", (int) getIntent().getExtras().get("taskIndex"));
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
            etTimeEdit.setText(hourOfDay+ ":"+minute+" "+amPM);
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
            etDateEdit.setText(month+1+"/"+day+"/"+year);
        }
    }
}
