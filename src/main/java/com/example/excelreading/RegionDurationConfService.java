package com.example.excelreading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegionDurationConfService {

    private final RegionDurationConfRepository repository;

    public RegionDurationConfService(RegionDurationConfRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processExcelFile(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        processSheet(sheet);
        workbook.close();

    }

    private void processSheet(Sheet sheet) {

        List<RegionDurationConf> records = new ArrayList<>();

        Row headerRow = sheet.getRow(0);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell fromRegionCell = row.getCell(0);
            if (fromRegionCell == null) continue;

            String fromRegion = "";
            if (fromRegionCell.getCellType() == CellType.STRING) {
                fromRegion = fromRegionCell.getStringCellValue().trim();
            } else if (fromRegionCell.getCellType() == CellType.NUMERIC) {
                fromRegion = String.valueOf((int) fromRegionCell.getNumericCellValue()).trim();
            }

            for (int j = 1; j < row.getPhysicalNumberOfCells(); j++) {

                Cell cell = row.getCell(j);
                if (cell == null) continue;
                String toRegion = "";

                if (headerRow.getCell(j).getCellType() == CellType.STRING) {
                    toRegion = headerRow.getCell(j).getStringCellValue().trim();
                } else if (headerRow.getCell(j).getCellType() == CellType.NUMERIC) {
                    toRegion = String.valueOf((int) headerRow.getCell(j).getNumericCellValue()).trim();
                }

                double distance = 0;
                try {
                    distance = row.getCell(j).getNumericCellValue();
                } catch (IllegalStateException e) {
                    distance = 0;
                }
                String equipmentConfJson = calculateDurationsJson(distance);
                records.add(createRegionDurationConf(fromRegion, toRegion, equipmentConfJson));
            }
        }
        repository.saveAll(records);
    }

    private RegionDurationConf createRegionDurationConf(String from, String to, String equipmentConf) {
        RegionDurationConf conf = new RegionDurationConf();
        conf.setFromRegion(from);
        conf.setToRegion(to);
        conf.setEquipmentConf(equipmentConf);
        conf.setDirection("DEPARTURE");
        return conf;
    }

    private String calculateDurationsJson(double distance) {
        Map<String, EquipmentDuration> equipmentDurations = new HashMap<>();

        for (Equipment equipment : Equipment.values()) {
            int duration =(int) Math.ceil(distance / equipment.getSpeed() / 60);
            equipmentDurations.put(equipment.getName(), new EquipmentDuration(equipment.getName(), duration));
        }

        try {
            return new ObjectMapper().writeValueAsString(equipmentDurations);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating JSON", e);
        }
    }
}