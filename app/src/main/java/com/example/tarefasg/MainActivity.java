package com.example.tarefasg;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import android.widget.Toast;

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
    private Button readTask;
    private DatePickerDialog.OnDateSetListener mDate;
    private EditText id_task;
    public ListView listViewTask;
    private TextView textIdTask;
    private TextView textViewIdTask;
    private Button updateButton;
    TextToSpeech speakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewTask = (ListView) findViewById(R.id.listView);
        listViewTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemClicked = (String) parent.getItemAtPosition(position);
                try {
                    int idTask = extractTaskId(itemClicked);
                    dialogMesage(itemClicked, idTask);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        textIdTask = findViewById(R.id.textIdTask);
        textViewIdTask = findViewById(R.id.id_task);
        textIdTask.setVisibility(View.INVISIBLE);
        textViewIdTask.setVisibility(View.INVISIBLE);
        updateButton = findViewById(R.id.buttonUpdate);
        updateButton.setVisibility(View.INVISIBLE);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                textIdTask.setVisibility(View.INVISIBLE);
                textViewIdTask.setVisibility(View.INVISIBLE);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        taskSave = findViewById(R.id.buttonSave);
        taskSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBd();
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

        readTask = findViewById(R.id.buttonProx);
        readTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadActivity();
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
            Cursor cursorBd = bd.rawQuery("SELECT id_task, nome, data_task, priority  FROM task WHERE completed = 0", null);
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

                    String taskInfo = idTarefa + " - " + nomeTarefa + " - " + dataTarefa + " - Nivel: " + prioridadeTarefa;
                    lines.add(taskInfo);
                } while (cursorBd.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ShowToast")
    public void insertBd() {
        int intPriotiry = convertInt.calculePriority(taskPriotiry.getSelectedItem().toString());

        String sqlInsert = "INSERT INTO task (nome, data_task, priority, completed) VALUES(?, ?, ?, false)";
        SQLiteStatement stmt = bd.compileStatement(sqlInsert);
        stmt.bindString(1, taskName.getText().toString());
        stmt.bindString(2, ConvertDateClass.convertDate(taskDate.getText().toString()));
        stmt.bindLong(3, intPriotiry);
        stmt.executeInsert();

        taskName.setText("");
        taskDate.setText("Selecione");
        Toast.makeText(this, "Inserido com Sucesso", Toast.LENGTH_SHORT).show();
        listBd();
    }

    public void update() {
        String sqlUpdate = "UPDATE task SET nome = ?, data_task = ?, priority = ? WHERE id_task = ?";
        int intPriotiry = convertInt.calculePriority(taskPriotiry.getSelectedItem().toString());
        SQLiteStatement stmt = bd.compileStatement(sqlUpdate);
        stmt.bindString(1, taskName.getText().toString());
        stmt.bindString(2, ConvertDateClass.convertDate(taskDate.getText().toString()));
        stmt.bindLong(3, intPriotiry);
        stmt.bindLong(4, Integer.parseInt(textIdTask.getText().toString()));
        stmt.executeUpdateDelete();
        stmt.close();
        updateButton.setVisibility(View.INVISIBLE);
        taskSave.setVisibility(View.VISIBLE);
        taskName.setText("");
        taskDate.setText("Selecione");
        Toast.makeText(this, "Atualizado com Sucesso", Toast.LENGTH_SHORT).show();
        listBd();
    }

    public void delete(int id_task) {
        String sqlDelete = "DELETE FROM task WHERE id_task = ?";
        SQLiteStatement stmtDelete = bd.compileStatement(sqlDelete);
        stmtDelete.bindLong(1, id_task);
        stmtDelete.executeUpdateDelete();
        stmtDelete.close();
        Toast.makeText(this, "Excluido com Sucesso", Toast.LENGTH_SHORT).show();
        listBd();
    }

    private int extractTaskId(String itemClicked) throws NumberFormatException {
        String[] parts = itemClicked.split(" - ");
        if (parts.length > 0) {
            return Integer.parseInt(parts[0]);
        } else {
            throw new NumberFormatException("Unable to extract task ID from: " + itemClicked);
        }
    }

    public void dialogMesage(String taksName, int id_task) {
        AlertDialog.Builder msgDialog = new AlertDialog.Builder(this);
        msgDialog.setTitle(taksName);
        msgDialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(id_task);
            }
        });
        msgDialog.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sqlUpdate = "SELECT id_task, nome, data_task, priority FROM task WHERE id_task = ?";
                Cursor cursorBd = null;
                try {
                    cursorBd = bd.rawQuery(sqlUpdate, new String[]{String.valueOf(id_task)});
                    if (cursorBd != null && cursorBd.moveToFirst()) {
                        do {
                            int idTarefa = cursorBd.getInt(0);
                            String nomeTarefa = cursorBd.getString(1);
                            String dataTarefa = cursorBd.getString(2);
                            String prioridadeTarefa = convertString.calculePriorityString(cursorBd.getInt(3));
                            textIdTask.setVisibility(View.VISIBLE);
                            textViewIdTask.setVisibility(View.VISIBLE);
                            updateButton.setVisibility(View.VISIBLE);
                            taskSave.setVisibility(View.INVISIBLE);
                            textIdTask.setText(String.valueOf(idTarefa));
                            taskName.setText(nomeTarefa);
                            taskDate.setText(dataTarefa);
                        } while (cursorBd.moveToNext());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (cursorBd != null) {
                        cursorBd.close();
                    }
                }
            }
        });


        msgDialog.setNeutralButton("Concluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sqlUpdateCompleted = "UPDATE task SET completed = 1 WHERE id_task = ?";
                SQLiteStatement stmtCm = bd.compileStatement(sqlUpdateCompleted);
                stmtCm.bindLong(1, id_task);
                stmtCm.executeUpdateDelete();
                stmtCm.close();
                listBd();
            }
        });
        msgDialog.show();
    }

    public void ReadActivity() {
        bd = openOrCreateDatabase("taskg", MODE_PRIVATE, null);
        String sqlQuery = "SELECT nome, data_task, priority FROM task WHERE data_task >= date('now')" +
                " ORDER BY data_task ASC LIMIT 1";
        Cursor cursor = bd.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            String nameTask = cursor.getString(0);
            String dateTask = cursor.getString(1);
            String priorityTask = convertString.calculePriorityString(cursor.getInt(2));
            String textSpeak = "A atividade mais prossima é " + nameTask + " no dia " + dateTask
                    + " e sua prioridade é " + priorityTask;
            //escrevi proxima com "ss" pra a voz falar a palavra correta 
            speakText = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    speakText.speak(textSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }
}

