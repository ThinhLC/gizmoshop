package com.gizmo.gizmoshop.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
@Component
public class GenericExporter<T> {

    public ByteArrayInputStream exportToExcel(List<T> dataList, Class<T> clazz) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(clazz.getSimpleName());
            int rowIdx = 0;

            // Create Header Row
            Row headerRow = sheet.createRow(rowIdx++);
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
            }

            // Fill Data Rows
            for (T data : dataList) {
                Row dataRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Cell cell = dataRow.createCell(i);
                    Object value = fields[i].get(data);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing fields for export", e);
        }
    }
}