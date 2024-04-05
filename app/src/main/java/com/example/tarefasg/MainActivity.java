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

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase bd;
    private EditText taskName;
    private TextView taskDate;
    private Spinner taskPriotiry;
    private Button taskSave;
    private Button taskDateButton;
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

                String date = month + "/" + dayOfMonth + "/" + year;
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
                    String prioridadeTarefa = calculePriorityString(cursorBd.getInt(3));

                    String taskInfo = "id - " + idTarefa + " - " + nomeTarefa + " - Data: " + dataTarefa + " - Importância: " + prioridadeTarefa;
                    lines.add(taskInfo);
                } while (cursorBd.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertBd() {
        int intPriotiry = calculePriority(taskPriotiry.getSelectedItem().toString());

        bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
        String sqlInsert = "INSERT INTO task (nome, data_task, priority) VALUES(?, ?, ?)";
        SQLiteStatement stmt = bd.compileStatement(sqlInsert);
        stmt.bindString(1, taskName.getText().toString());
        stmt.bindString(2, convertDate(taskDate.getText().toString()));
        stmt.bindLong(3, intPriotiry);
        stmt.executeInsert();
    }

    public String convertDate(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = originalFormat.parse(dateString);
            return isoFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Lida com o erro de parse
            return null;
        }
    }

    public int calculePriority(String stringTaskPriority) {
        int intPriority = 0;

        switch (stringTaskPriority) {
            case "Alta":
                intPriority = 3;
                break;

            case "Média":
                intPriority = 2;
                break;

            case "Baixa":
                intPriority = 1;
                break;

            default:
                intPriority = 1;
                break;
        }
        return intPriority;
    }

    public String calculePriorityString(int intTaskPriority) {
        String StringPriority = "";

        switch (intTaskPriority) {
            case 3:
                StringPriority = "Alta";
                break;

            case 2:
                StringPriority = "Média";
                break;

            case 1:
                StringPriority = "Baixa";
                break;

            default:
                StringPriority = "Baixa";
                break;
        }
        return StringPriority;
    }
}