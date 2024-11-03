package com.gizmo.gizmoshop.excel;

import com.gizmo.gizmoshop.exception.InvalidInputException;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class GenericExporter<T> {

    public <T> void exportToExcel(List<T> dataList, Class<T> clazz, List<String> excludedFields, OutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(clazz.getSimpleName());
            int rowIdx = 0;

            Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(ExcludeFromExport.class) && !excludedFields.contains(field.getName()))
                    .toArray(Field[]::new);
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
            }

            // Tạo hàng dữ liệu
            for (T data : dataList) {
                Row dataRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Cell cell = dataRow.createCell(i);
                    Object value = fields[i].get(data);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }
            workbook.write(outputStream);
        } catch (IllegalAccessException e) {
            throw new InvalidInputException("Lỗi truy cập vào các trường khi xuất file");
        }
    }
    public List<T> importFromExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new HashMap<>();
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell cell = headerRow.getCell(cellIndex);
                if (cell != null) {
                    columnMap.put(cell.getStringCellValue().trim(), cellIndex);
                }
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                T instance = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Integer cellIndex = columnMap.get(field.getName());
                    if (cellIndex == null) continue;

                    Cell cell = row.getCell(cellIndex);
                    String cellValue = getCellValue(cell);
                    setFieldValue(instance, field, cellValue);
                }
                dataList.add(instance);
            }
        } catch (Exception e) {
            throw new InvalidInputException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return dataList;
    }

    private void setFieldValue(T instance, Field field, String cellValue) throws IllegalAccessException {
        if (cellValue == null || cellValue.isEmpty()) return; // Bỏ qua ô trống

        try {
            if (field.getType().equals(String.class)) {
                field.set(instance, cellValue);
            } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                field.set(instance, Integer.parseInt(cellValue));
            } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                if (cellValue.contains(".")) {
                    field.set(instance, Long.valueOf(Double.valueOf(cellValue).longValue()));
                } else {
                    field.set(instance, Long.parseLong(cellValue));
                }
            } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                field.set(instance, Double.parseDouble(cellValue));
            } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                field.set(instance, cellValue.equalsIgnoreCase("true") || cellValue.equals("1"));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị cho " + field.getName() + " không hợp lệ: " + cellValue);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        String value = null;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = String.valueOf(cell.getDateCellValue());
                } else {
                    value = String.valueOf((long) cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                break;
        }

        return value;
    }




}