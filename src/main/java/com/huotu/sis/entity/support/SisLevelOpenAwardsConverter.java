package com.huotu.sis.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/25.
 */
@Converter(autoApply = true)
public class SisLevelOpenAwardsConverter implements AttributeConverter<SisLevelAwards,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(SisLevelAwards sisLevelOpenAwards) {
        if (sisLevelOpenAwards == null)
            return null;
        try {
            SisLevelOpenAward[] award = sisLevelOpenAwards.values().toArray(new SisLevelOpenAward[sisLevelOpenAwards.size()]);
            return objectMapper.writeValueAsString(award);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public SisLevelAwards convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        if (s.equalsIgnoreCase("null"))
            return null;
        try {
            SisLevelAwards sisLevelOpenAwards = new SisLevelAwards();
            JsonNode node = objectMapper.readTree(s);
            for (JsonNode n:node){
                SisLevelOpenAward sisLevelOpenAward  = objectMapper.treeToValue(n,SisLevelOpenAward.class);
                sisLevelOpenAwards.put(sisLevelOpenAward.getBuySisLvId(),sisLevelOpenAward);
            }
//            JsonParser jsonParser = objectMapper.treeAsTokens(objectMapper.readTree(dbData));
//            objectMapper.readValues(jsonParser,LevelPrice.class).forEachRemaining(levelPrice
//                    -> levelPrices.put(levelPrice.getLevel(),levelPrice));
            return sisLevelOpenAwards;
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
