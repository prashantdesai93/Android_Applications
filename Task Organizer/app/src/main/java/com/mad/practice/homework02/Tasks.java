package com.mad.practice.homework02;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by hp on 2/2/2018.
 */

public class Tasks implements Parcelable{

    static SimpleDateFormat dtFormat = new SimpleDateFormat("MM/dd/yyyy");
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");
    //static DateFormat dtFormat= DateFormat.getDateInstance();
    //static DateFormat timeFormat= DateFormat.getTimeInstance();
    private String title;
    private String priority;
    private Date date;
    private Time time;

    public Tasks(String title, String priority, Date date, Time time) {
        this.title = title;
        this.priority = priority;
        this.date = date;
        this.time = time;
    }

    protected Tasks(Parcel in) {
        title = in.readString();
        priority = in.readString();
        try {
            //date =formatter.parse(in.readString());
            //date = new Date(in.readString());
            date = dtFormat.parse(in.readString());
            Log.i("in Constructor ",date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            //time=new Time(format.parse(in.readString()).getTime());
            //time =new Time(timeFormat.format(in.readString()));
            time = new Time(timeFormat.parse(in.readString()).getTime());
            Log.i("in Constructor ",time.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Tasks> CREATOR = new Creator<Tasks>() {
        @Override
        public Tasks createFromParcel(Parcel in) {

            return new Tasks(in);
        }

        @Override
        public Tasks[] newArray(int size) {
            return new Tasks[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(priority);
        dest.writeString(dtFormat.format(date));
        dest.writeString(timeFormat.format(time));
    }
}
