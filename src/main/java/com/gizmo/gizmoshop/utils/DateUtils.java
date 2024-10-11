package com.gizmo.gizmoshop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    // Phương thức định dạng ngày từ yyyy-MM-dd sang dd/MM/yyyy
    public String formatdate(String dayt) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày ban đầu
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng ngày mới

        try {
            // Chuyển đổi từ chuỗi sang đối tượng Date
            Date date = originalFormat.parse(dayt);
            // Trả về định dạng mới
            return newFormat.format(date);
        } catch (ParseException e) {
            // In ra lỗi nếu định dạng không hợp lệ
            System.err.println("Lỗi chuyển đổi định dạng ngày: " + e.getMessage());
            return "";
        }
    }

    // Phương thức main để kiểm tra

}
