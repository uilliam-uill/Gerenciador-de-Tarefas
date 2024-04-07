package com.example.tarefasg;

public class calculePriorityClass {
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
}
