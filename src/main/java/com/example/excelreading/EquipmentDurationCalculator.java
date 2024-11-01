package com.example.excelreading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EquipmentDurationCalculator {

    private final ObjectMapper objectMapper;

    public EquipmentDurationCalculator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String calculateDurationsJson(double distance) {
        Map<String, EquipmentDuration> equipmentDurations = new HashMap<>();
        populateEquipmentDurations(distance, equipmentDurations);
        return convertToJson(equipmentDurations);
    }

    private void populateEquipmentDurations(double distance, Map<String, EquipmentDuration> equipmentDurations) {
        for (Equipment equipment : Equipment.values()) {
            int duration = (int) Math.ceil(distance / equipment.getSpeed() / 60);
            equipmentDurations.put(equipment.getName(), new EquipmentDuration(equipment.getName(), duration));
        }
    }

    private String convertToJson(Map<String, EquipmentDuration> equipmentDurations) {
        try {
            return objectMapper.writeValueAsString(equipmentDurations);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating JSON", e);
        }
    }
}