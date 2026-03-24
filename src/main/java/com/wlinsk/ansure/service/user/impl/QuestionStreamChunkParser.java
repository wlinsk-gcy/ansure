package com.wlinsk.ansure.service.user.impl;

import com.alibaba.fastjson2.JSON;
import com.wlinsk.ansure.model.dto.question.QuestionContentDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class QuestionStreamChunkParser {

    private final StringBuilder buffer = new StringBuilder();
    private int braceDepth = 0;

    List<QuestionContentDTO> parse(String text) {
        List<QuestionContentDTO> questions = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return questions;
        }
        for (char c : text.toCharArray()) {
            if (c == '{') {
                braceDepth++;
            }
            if (braceDepth > 0) {
                buffer.append(c);
            }
            if (c == '}') {
                braceDepth--;
                if (braceDepth == 0) {
                    questions.add(JSON.parseObject(buffer.toString(), QuestionContentDTO.class));
                    buffer.setLength(0);
                }
            }
        }
        return questions;
    }
}
