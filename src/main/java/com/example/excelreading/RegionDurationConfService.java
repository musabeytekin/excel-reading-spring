package com.example.excelreading;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionDurationConfService {

    private final RegionDurationConfRepository repository;
    private final EquipmentDurationCalculator equipmentDurationCalculator;

    @Transactional
    public void processExcelFile(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        processWorkbook(workbook);
        workbook.close();
    }

    private void processWorkbook(Workbook workbook) {
        int sheets = workbook.getNumberOfSheets();
        for (int i = 0; i < sheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            processSheet(sheet);
        }
    }

    private void processSheet(Sheet sheet) {
        List<RegionDurationConf> records = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            processRow(new RowData(headerRow, row), records);
        }
        repository.saveAll(records);
    }

    private void processRow(RowData rowData, List<RegionDurationConf> records) {
        String fromRegion = getCellValue(rowData.row().getCell(0));
        for (int j = 1; j < rowData.row().getPhysicalNumberOfCells(); j++) {
            String toRegion = getCellValue(rowData.headerRow().getCell(j));
            double distance;
            try {
                distance = getNumericCellValue(rowData.row().getCell(j));
            } catch (IllegalArgumentException e) {
                continue;
            }
            String equipmentConfJson = equipmentDurationCalculator.calculateDurationsJson(distance);
            records.add(createRegionDurationConf(fromRegion, toRegion, equipmentConfJson));
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() : String.valueOf((int) cell.getNumericCellValue()).trim();
    }

    private double getNumericCellValue(Cell cell) throws IllegalArgumentException {
        if (cell == null) throw new IllegalArgumentException("Cell cannot be null");
        try {
            return cell.getNumericCellValue();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    private RegionDurationConf createRegionDurationConf(String from, String to, String equipmentConf) {
        RegionDurationConf conf = new RegionDurationConf();
        conf.setFromRegion(from);
        conf.setToRegion(to);
        conf.setEquipmentConf(equipmentConf);
        return conf;
    }
}