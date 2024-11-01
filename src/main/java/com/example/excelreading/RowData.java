package com.example.excelreading;

import org.apache.poi.ss.usermodel.Row;

public record RowData(Row headerRow, Row row) {
}