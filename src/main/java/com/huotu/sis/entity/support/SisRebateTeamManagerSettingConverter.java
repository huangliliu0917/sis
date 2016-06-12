package com.huotu.sis.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/25.
 */
@Converter(autoApply = true)
public class SisRebateTeamManagerSettingConverter implements AttributeConverter<SisRebateTeamManagerSetting,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SisRebateTeamManagerSetting attribute) {
        if(attribute==null){
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public SisRebateTeamManagerSetting convertToEntityAttribute(String dbData) {
        if(dbData==null){
            return null;
        }
        try {
            return objectMapper.readValue(dbData, SisRebateTeamManagerSetting.class);
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
