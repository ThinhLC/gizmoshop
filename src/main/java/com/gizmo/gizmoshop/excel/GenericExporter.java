package com.gizmo.gizmoshop.excel;

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

            // Tạo hàng tiêu đề
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

            // Ghi workbook vào outputStream
            workbook.write(outputStream); // Ghi vào outputStream, không cần tạo ByteArrayOutputStream mới
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Lỗi truy cập vào các trường khi xuất file", e);
        }
    }

    public List<T> importFromExcel(MultipartFile file, Class<T> clazz) throws IOException {
        List<T> dataList = new ArrayList<>();
        Long maxId = 0L; // Biến lưu trữ ID lớn nhất

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = new HashMap<>();

            // Đọc tiêu đề cột và ánh xạ tên cột tới chỉ số
            for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
                Cell cell = headerRow.getCell(cellIndex);
                if (cell != null) {
                    columnMap.put(cell.getStringCellValue().trim(), cellIndex);
                }
            }

            // Đọc dữ liệu từ các hàng
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                T instance = clazz.getDeclaredConstructor().newInstance();
                boolean idProvided = false; // Kiểm tra xem ID đã được cung cấp hay chưa
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Integer cellIndex = columnMap.get(field.getName());
                    if (cellIndex == null) continue;

                    Cell cell = row.getCell(cellIndex);
                    String cellValue = getCellValue(cell);

                    // Kiểm tra nếu trường là ID
                    if (field.getName().equals("id")) {
                        if (cellValue == null || cellValue.isEmpty()) {
                            // Tự động điền ID nếu không có
                            maxId++;
                            field.set(instance, maxId);
                        } else {
                            idProvided = true;
                            try {
                                Double idValue = Double.parseDouble(cellValue);
                                if (idValue % 1 != 0) {
                                    throw new IllegalArgumentException("Giá trị ID không hợp lệ trong hàng số " + (rowIndex + 1) + ": " + cellValue + ". ID phải là số nguyên.");
                                }
                                Long id = idValue.longValue();
                                field.set(instance, id);
                                // Cập nhật maxId nếu ID lớn hơn
                                maxId = Math.max(maxId, id);
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Giá trị ID không hợp lệ trong hàng số " + (rowIndex + 1) + ": " + cellValue);
                            }
                        }
                    } else {
                        setFieldValue(instance, field, cellValue);
                    }
                }
                dataList.add(instance);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }

        return dataList;
    }

    private void setFieldValue(T instance, Field field, String cellValue) throws IllegalAccessException {
        if (cellValue == null || cellValue.isEmpty()) return; // Bỏ qua ô trống

        if (field.getType().equals(String.class)) {
            field.set(instance, cellValue);
        } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
            try {
                field.set(instance, Integer.parseInt(cellValue));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá trị cho " + field.getName() + " không hợp lệ: " + cellValue);
            }
        } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
            try {
                field.set(instance, Double.parseDouble(cellValue));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá trị cho " + field.getName() + " không hợp lệ: " + cellValue);
            }
        } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            field.set(instance, cellValue.equalsIgnoreCase("true") || cellValue.equals("1"));
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
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                // Xử lý nếu ô là công thức
                value = cell.getCellFormula();
                break;
            default:
        }

        return value;
    }

}