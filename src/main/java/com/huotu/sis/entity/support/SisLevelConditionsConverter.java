package com.huotu.sis.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
@Converter(autoApply = true)
public class SisLevelConditionsConverter implements AttributeConverter<List<SisLevelCondition>,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(List<SisLevelCondition> sisLevelConditions) {
        if (sisLevelConditions == null)
            return null;
        try {
            SisLevelCondition[] conditions = sisLevelConditions.toArray(new SisLevelCondition[sisLevelConditions.size()]);
            return objectMapper.writeValueAsString(conditions);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public List<SisLevelCondition> convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        if (s.equalsIgnoreCase("null"))
            return null;
        try {
//            SisLevelConditions sisLevelConditions = new SisLevelConditions();
            List<SisLevelCondition> sisLevelConditions=new ArrayList<>();
            JsonNode node = objectMapper.readTree(s);
            for (JsonNode n:node){
                SisLevelCondition sisLevelCondition  = objectMapper.treeToValue(n,SisLevelCondition.class);
                sisLevelConditions.add(sisLevelCondition);
//                sisLevelConditions.put(sisLevelCondition.getSisLvId(),sisLevelCondition);
            }
            return sisLevelConditions;
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
