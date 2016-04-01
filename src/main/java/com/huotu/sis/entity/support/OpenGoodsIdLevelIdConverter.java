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
public class OpenGoodsIdLevelIdConverter implements AttributeConverter<OpenGoodsIdLevelIds,String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(OpenGoodsIdLevelIds openGoodsIdLevelIds) {
        if (openGoodsIdLevelIds == null)
            return null;
        try {
            OpenGoodsIdLevelId[] goodsIdLevelIds =
                    openGoodsIdLevelIds.values().toArray(new OpenGoodsIdLevelId[openGoodsIdLevelIds.size()]);
            return objectMapper.writeValueAsString(goodsIdLevelIds);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }

    @Override
    public OpenGoodsIdLevelIds convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        if (s.equalsIgnoreCase("null"))
            return null;
        try {
            OpenGoodsIdLevelIds openGoodsIdLevelIds = new OpenGoodsIdLevelIds();
            JsonNode node = objectMapper.readTree(s);
            for (JsonNode n:node){
                OpenGoodsIdLevelId openGoodsIdLevelId  = objectMapper.treeToValue(n,OpenGoodsIdLevelId.class);
                openGoodsIdLevelIds.put(openGoodsIdLevelId.getLevelid(),openGoodsIdLevelId);
            }
//            JsonParser jsonParser = objectMapper.treeAsTokens(objectMapper.readTree(dbData));
//            objectMapper.readValues(jsonParser,LevelPrice.class).forEachRemaining(levelPrice
//                    -> levelPrices.put(levelPrice.getLevel(),levelPrice));
            return openGoodsIdLevelIds;
        } catch (IOException e) {
            throw new IllegalStateException("Broken JSON", e);
        }
    }
}
