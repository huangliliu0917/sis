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
public class OpenSisAwardsConverter implements AttributeConverter<OpenSisAwards,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(OpenSisAwards openSisAwards) {
        if (openSisAwards == null)
            return null;
        try {
            OpenSisAward[] awards = openSisAwards.values().toArray(new OpenSisAward[openSisAwards.size()]);
            return objectMapper.writeValueAsString(awards);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public OpenSisAwards convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        if (s.equalsIgnoreCase("null"))
            return null;
        try {
            OpenSisAwards openSisAwards = new OpenSisAwards();
            JsonNode node = objectMapper.readTree(s);
            for (JsonNode n:node){
                OpenSisAward openSisAward  = objectMapper.treeToValue(n,OpenSisAward.class);
                openSisAwards.put(openSisAward.getIdx(),openSisAward);
            }
//            JsonParser jsonParser = objectMapper.treeAsTokens(objectMapper.readTree(dbData));
//            objectMapper.readValues(jsonParser,LevelPrice.class).forEachRemaining(levelPrice
//                    -> levelPrices.put(levelPrice.getLevel(),levelPrice));
            return openSisAwards;
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
