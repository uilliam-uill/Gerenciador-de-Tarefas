package com.example.tarefasg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class convertDateClass {
    public static String convertDate(String dateString) {
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
}
