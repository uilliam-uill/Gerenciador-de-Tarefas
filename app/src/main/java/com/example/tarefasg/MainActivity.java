package com.example.tarefasg;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.tarefasg.calculePriorityStringClass;
import com.example.tarefasg.calculePriorityClass;
import com.example.tarefasg.convertDateClass;

public class MainActivity extends AppCompatActivity {
    calculePriorityStringClass convertString = new calculePriorityStringClass();
    calculePriorityClass convertInt = new calculePriorityClass();
    private SQLiteDatabase bd;
    private EditText taskName;
    private TextView taskDate;
    private Spinner taskPriotiry;
    private Spinner spinnerView;
    private Button taskSave;
    private Button taskDateButton;
    private Button viewButton;
    private DatePickerDialog.OnDateSetListener mDate;
    public ListView listViewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewTask = (ListView) findViewById(R.id.listView);
        taskName = findViewById(R.id.textTask);
        taskPriotiry = findViewById(R.id.spineer);
        taskDate = findViewById(R.id.textDate);
        spinnerView = findViewById(R.id.spinnerView);
        viewButton = findViewById(R.id.buttonView);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        taskSave = findViewById(R.id.buttonSave);
        taskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBd();
                listBd();
            }
        });

        taskDateButton = findViewById(R.id.buttonDate);
        taskDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDate,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

        });

        mDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                String date = dayOfMonth + "/" + month + "/" + year;
                taskDate.setText(date);
            }
        };
        createBd();
        listBd();
    }

    public void createBd() {
        try {
            bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
            bd.execSQL("CREATE TABLE IF NOT EXISTS task( id_task INTEGER PRIMARY KEY AUTOINCREMENT, nome varchar(255), data_task date, priority int)");
            bd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listBd() {
        try {
            bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
            Cursor cursorBd = bd.rawQuery("SELECT id_task, nome, data_task, priority  FROM task", null);
            ArrayList<String> lines = new ArrayList<String>();
            ArrayAdapter myAdapter = new ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    lines
            );
            listViewTask.setAdapter(myAdapter);
            if (cursorBd.moveToFirst()) {
                do {
                    int idTarefa = cursorBd.getInt(0);
                    String nomeTarefa = cursorBd.getString(1);
                    String dataTarefa = cursorBd.getString(2);
                    String prioridadeTarefa = convertString.calculePriorityString(cursorBd.getInt(3));

                    String taskInfo = "id - " + idTarefa + " - " + nomeTarefa + " - Data: " + dataTarefa + " - Importância: " + prioridadeTarefa;
                    lines.add(taskInfo);
                } while (cursorBd.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertBd() {
        int intPriotiry = convertInt.calculePriority(taskPriotiry.getSelectedItem().toString());

        bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
        String sqlInsert = "INSERT INTO task (nome, data_task, priority) VALUES(?, ?, ?)";
        SQLiteStatement stmt = bd.compileStatement(sqlInsert);
        stmt.bindString(1, taskName.getText().toString());
        stmt.bindString(2, convertDateClass.convertDate(taskDate.getText().toString()));
        stmt.bindLong(3, intPriotiry);
        stmt.executeInsert();

        taskName.setText("");
        taskDate.setText("Escolha data");
    }
}