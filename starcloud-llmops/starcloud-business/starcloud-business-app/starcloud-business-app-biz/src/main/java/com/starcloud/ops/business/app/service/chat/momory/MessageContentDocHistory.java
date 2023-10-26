package com.starcloud.ops.business.app.service.chat.momory;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 上下文文档历史
 */
@Data
public class MessageContentDocHistory {

    private List<MessageContentDocDTO> docs;

    public MessageContentDocHistory(List<MessageContentDocDTO> docs) {
        this.docs = docs;
    }


    /**
     * 增加一条历史
     *
     * @param doc
     */
    public void addDoc(MessageContentDocDTO doc) {
        this.getDocs().add(doc);
    }

    public List<MessageContentDocDTO> limitMessage(long limit) {

        if (limit >= 0) {
            return Optional.ofNullable(this.docs).orElse(new ArrayList<>()).stream().limit(limit).collect(Collectors.toList());
        } else {
            return Optional.ofNullable(this.docs).orElse(new ArrayList<>()).stream().skip(CollectionUtil.size(this.docs) + limit).collect(Collectors.toList());
        }

    }

}
