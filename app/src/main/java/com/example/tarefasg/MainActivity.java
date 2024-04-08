package com.example.tarefasg;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    CalculePriorityStringClass convertString = new CalculePriorityStringClass();
    CalculePriorityClass convertInt = new CalculePriorityClass();
    ViewTask viewTask = new ViewTask();
    private SQLiteDatabase bd;
    private EditText taskName;
    private TextView taskDate;
    private Spinner taskPriotiry;
    private Spinner spinnerView;
    private Button taskSave;
    private Button taskDateButton;
    private Button viewButton;
    private DatePickerDialog.OnDateSetListener mDate;
    private  EditText id_task;
    public ListView listViewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewTask = (ListView) findViewById(R.id.listView);
        listViewTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemClicked = (String) parent.getItemAtPosition(position);
                String primeiroCaractere = itemClicked.substring(0, 1);
                dialogMesage(itemClicked, Integer.parseInt(primeiroCaractere));
            }
        });

        taskName = findViewById(R.id.textTask);
        taskPriotiry = findViewById(R.id.spineer);
        taskDate = findViewById(R.id.textDate);
        id_task = findViewById(R.id.id_task);
        spinnerView = findViewById(R.id.spinnerView);
        viewButton = findViewById(R.id.buttonView);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String sql = viewTask.returnSql(spinnerView.getSelectedItem().toString());
                    bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
                    Cursor cursorBd = bd.rawQuery(sql, null);
                    ArrayList<String> linesConsult = new ArrayList<String>();
                    ArrayAdapter<String> adapterConsult = new ArrayAdapter<>(
                            v.getContext(),
                            android.R.layout.simple_list_item_1,
                            linesConsult
                    );
                    listViewTask.setAdapter(adapterConsult);
                    if (cursorBd.moveToFirst()) {
                        do {
                            int idTarefa = cursorBd.getInt(0);
                            String nomeTarefa = cursorBd.getString(1);
                            String dataTarefa = cursorBd.getString(2);
                            String prioridadeTarefa = convertString.calculePriorityString(cursorBd.getInt(3));

                            String taskInfo = "id - " + idTarefa + " - " + nomeTarefa + " - Data: " + dataTarefa + " - Importância: " + prioridadeTarefa;
                            linesConsult.add(taskInfo);
                        } while (cursorBd.moveToNext());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
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
            bd.execSQL("CREATE TABLE IF NOT EXISTS task( id_task INTEGER PRIMARY KEY AUTOINCREMENT,completed boolean, nome varchar(255), data_task date, priority int)");
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

                    String taskInfo = idTarefa + " - " + nomeTarefa + " - Data: " + dataTarefa + " - Importância: " + prioridadeTarefa;
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
        String sqlInsert = "INSERT INTO task (nome, data_task, priority, completed) VALUES(?, ?, ?, false)";
        SQLiteStatement stmt = bd.compileStatement(sqlInsert);
        stmt.bindString(1, taskName.getText().toString());
        stmt.bindString(2, ConvertDateClass.convertDate(taskDate.getText().toString()));
        stmt.bindLong(3, intPriotiry);
        stmt.executeInsert();

        taskName.setText("");
        taskDate.setText("Escolha data");
    }

    public void dialogMesage(String taksName, int id_task){
        AlertDialog.Builder msgDialog = new AlertDialog.Builder(this);
        msgDialog.setTitle(taksName);
        msgDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
                String sqlDelete = "DELETE FROM task WHERE id_task = ?";
                SQLiteStatement stmt = bd.compileStatement(sqlDelete);
                stmt.bindLong(1, id_task);
                stmt.executeUpdateDelete();
            }
        });
        msgDialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
                String sqlUpdate = "UPDATE task SET completed = true WHERE id_task = ?";
                SQLiteStatement stmt = bd.compileStatement(sqlUpdate);
                stmt.bindLong(1, id_task);
                stmt.executeUpdateDelete();
            }
        });
        msgDialog.setPositiveButton("Concluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
                String sqlSelect = "SELECT id_task, nome, data_task, priority  FROM task WHERE id_task = ?";
                Cursor cursorBd = bd.rawQuery(sqlSelect + id_task, null);
                SQLiteStatement stmt = bd.compileStatement(sqlSelect);
                stmt.bindLong(1, id_task);
                stmt.executeUpdateDelete();
                if (cursorBd.moveToFirst()) {
                    do {
                        int idTarefa = cursorBd.getInt(0);
                        String nomeTarefa = cursorBd.getString(1);
                        String dataTarefa = cursorBd.getString(2);
                        String prioridadeTarefa = convertString.calculePriorityString(cursorBd.getInt(3));

                        taskName.setText(nomeTarefa);
                        taskDate.setText(dataTarefa);
                    } while (cursorBd.moveToNext());
                }
            }
        });
        msgDialog.show();
    }
}