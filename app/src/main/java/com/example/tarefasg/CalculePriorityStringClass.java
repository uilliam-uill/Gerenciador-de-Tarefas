package com.example.tarefasg;

public class CalculePriorityStringClass {
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
