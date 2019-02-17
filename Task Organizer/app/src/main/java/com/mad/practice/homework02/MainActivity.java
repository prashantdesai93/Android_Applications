package com.mad.practice.homework02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    static int ACTIVITY_KEY =100;

    static int EDIT_ACTIVITY_KEY =200;
    LinkedList<Tasks> tasks = new LinkedList<Tasks>();
    TextView tvTaskTitle;
    TextView tvTaskDate ;
    TextView tvTaskTime ;
    TextView tvTaskPriority ;
    TextView tvCurrentTask ;
    TextView tvTotalTask ;
    TextView tvTaskLabel ;
    TextView tvOfLabel ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("View Tasks");

        findViewById(R.id.addTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CreateTaskActivity.class);
                startActivityForResult(i,ACTIVITY_KEY);
            }
        });

        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDate = findViewById(R.id.tvTaskDate);
        tvTaskTime = findViewById(R.id.tvTaskTime);
        tvTaskPriority = findViewById(R.id.tvTaskPriority);
        tvCurrentTask = findViewById(R.id.tvCurrentTask);
        tvTotalTask = findViewById(R.id.tvTotalTask);
        tvTaskLabel = findViewById(R.id.tvTaskLabel);
        tvOfLabel = findViewById(R.id.tvOfLabel);

        findViewById(R.id.btnFirst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tasks.size()>0){
                    refresh(0);
                }
            }
        });

        findViewById(R.id.btnPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvCurrentTask.getText()!=null && tvCurrentTask.getText().length()>0){
                    int curIndex= Integer.parseInt(tvCurrentTask.getText().toString());
                    curIndex--;
                    int prevIndex =curIndex-1;
                    if(prevIndex>-1 && prevIndex<tasks.size()){
                        refresh(prevIndex);
                    }
                }
            }
        });

        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                if(tvCurrentTask.getText()!=null && tvCurrentTask.getText().length()>0) {
                    int curIndex = Integer.parseInt(tvCurrentTask.getText().toString());
                    if(curIndex>0){
                        curIndex--;
                        i.putExtra("task",tasks.get(curIndex));
                        i.putExtra("taskIndex", curIndex);
                        startActivityForResult(i,EDIT_ACTIVITY_KEY);
                    }
                }
            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvCurrentTask.getText()!=null && tvCurrentTask.getText().length()>0){
                    int curIndex= Integer.parseInt(tvCurrentTask.getText().toString());
                    curIndex--;
                    Log.i("full out if",curIndex+"");
                    if(curIndex>-1 && curIndex<tasks.size()){
                        tasks.remove(curIndex);
                        Log.i("out if",curIndex+"");
                        if(tasks.size()==1){
                            refresh(0);
                        }else if(curIndex >-1 && tasks.size()>0&& curIndex-1<=tasks.size()-1){
                            refresh(curIndex-1);
                            Log.i("if",curIndex -1+"");
                        }else if(curIndex +1 <=tasks.size()){
                            refresh(curIndex+1);
                            Log.i("else if",curIndex +1+"");
                        }else if(tasks.size()==0){
                            tvTaskTitle.setText("Task Title");
                            tvTaskDate.setText("Task Date");
                            tvTaskTime.setText("Task Time");
                            tvTaskPriority.setText("Task Priority");
                            tvCurrentTask.setText("0");
                            tvTotalTask.setText("0");
                        }
                    }
                }
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvCurrentTask.getText()!=null && tvCurrentTask.getText().length()>0){
                    int curIndex= Integer.parseInt(tvCurrentTask.getText().toString());
                    curIndex--;
                    int nextIndex =curIndex+1;
                    if(nextIndex>0 && nextIndex<tasks.size()){
                        refresh(nextIndex);
                    }
                }
            }
        });

        findViewById(R.id.btnLast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (tasks.size() > 1 ) {
                        refresh(tasks.size() - 1);
                    }
            }
        });
    }
    protected void refresh(int which){
        order(tasks);
        Tasks curr = tasks.get(which);
        tvTaskTitle.setText(curr.getTitle().toString());
        tvTaskDate.setText(Tasks.dtFormat.format(curr.getDate()));
        tvTaskTime.setText(Tasks.timeFormat.format(curr.getTime()));
        tvTaskPriority.setText(curr.getPriority().toString());
        tvCurrentTask.setText(which +1+"");
        tvTotalTask.setText(tasks.size()+"");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == ACTIVITY_KEY) {
                if (resultCode == RESULT_OK) {
                    Tasks task = (Tasks) data.getExtras().getParcelable(CreateTaskActivity.TASK_KEY);
                    Log.i("Result", task.toString());
                    tasks.add(task);
                    refresh(0);

                } else if (resultCode == RESULT_CANCELED) {
                    Log.i("Result", "No Value Recieved");
                }
            } else if (requestCode == EDIT_ACTIVITY_KEY) {
                if (resultCode == RESULT_OK) {
                    Tasks task = (Tasks) data.getExtras().getParcelable(EditActivity.TASK_KEY_EDIT);
                    Log.d("demo", task.toString());
                    int modifiedTaskIndex = (int) data.getExtras().get("taskIndexEdit");
                    tasks.get(modifiedTaskIndex).setTitle(task.getTitle());
                    tasks.get(modifiedTaskIndex).setDate(task.getDate());
                    tasks.get(modifiedTaskIndex).setTime(task.getTime());
                    tasks.get(modifiedTaskIndex).setPriority(task.getPriority());

                    refresh(modifiedTaskIndex);
                    Log.i("Result", task.toString());
                } else if (resultCode == RESULT_CANCELED) {
                    Log.i("Result", "No Value Recieved");
                }
            }
        }catch (Exception e){
            Log.i("catch", e.getMessage());
            e.printStackTrace();
        }
    }

    private static void order(LinkedList<Tasks> tasks) {

        Collections.sort(tasks, new Comparator() {

            public int compare(Object o1, Object o2) {

                Date d1 = ((Tasks) o1).getDate();
                Date d2 = ((Tasks) o2).getDate();
                int dComp = d1.compareTo(d2);

                if (dComp != 0) {
                    return dComp;
                } else {
                    Time t1 = ((Tasks) o1).getTime();
                    Time t2 = ((Tasks) o2).getTime();
                    return t1.compareTo(t2);
                }
            }});
    }
}


