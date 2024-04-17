package com.example.tarefasg;

public class ViewTask {

    public String returnSql(String choice){
        switch (choice) {
            case "Prioridade":
                choice = "SELECT id_task, nome, data_task, priority, description_task FROM task ORDER BY priority DESC " +
                        " WHERE completed = false";
                break;

            case "Concluidas":
                choice = "SELECT id_task, nome, data_task, priority, description_task FROM task WHERE completed = true";
                break;

            case "Não concluidas":
                choice = "SELECT id_task, nome, data_task, priority, description_task FROM task WHERE completed = false";
                break;

            case "Data proxima":
                choice = "SELECT id_task, nome, data_task, priority, description_task FROM task WHERE DATE(data_task) >= DATE('now') AND completed = 0 ORDER BY DATE(data_task) ASC";
                break;

            case "Data distante":
                choice = "SELECT id_task, nome, data_task, priority, description_task FROM task WHERE completed = 0 ORDER BY DATE(data_task) DESC";
                break;

        }
        return choice;
    }
}