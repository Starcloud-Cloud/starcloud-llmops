package com.starcloud.ops.business.app.util.qlOperator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 多字段支持 docs.print('author','bookName',false)
 */
@Slf4j
public class PrintOperator extends OperatorBase {
    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {

        OperateData docs = list.get(0);

        int length = list.length;
        List<String> fieldList = new ArrayList<>(length - 1);
        boolean addPrefix = false;
        for (int i = 1; i < length; i++) {
            Object data = list.get(i).getObject(parent);
            if ((i == length - 1) && "true".equalsIgnoreCase(data.toString())) {
                addPrefix = true;
            } else if ("false".equalsIgnoreCase(data.toString())) {
                addPrefix = false;
            } else {
                fieldList.add((String) data);
            }
        }

        log.info("list: {}", list);

        Object ddocs = docs.getObject(parent);

        List<Object> result = new ArrayList<>();
        if (ddocs instanceof List) {
            List<Object> fdocs = (List<Object>) ddocs;

            for (int i = 0; i < fdocs.size(); i++) {
                Object doc = fdocs.get(i);
                StringJoiner sj = new StringJoiner("\n");
                for (String fieldName : fieldList) {
                    Object fieldValue = BeanUtil.getFieldValue(doc, fieldName);
                    String prefix = addPrefix ? prefix(doc, fieldName) : StringUtils.EMPTY;
                    if (Objects.nonNull(fieldValue)) {
                        sj.add(prefix + fieldValue.toString());
                    }
                }
                result.add(sj);
            }
            return new OperateData(StrUtil.join("\n", result), String.class);
        }

        return null;
    }


    private String prefix(Object doc, String fieldName) {
        try {
            Field field = doc.getClass().getDeclaredField(fieldName);
            FieldDefine fieldDefine = field.getDeclaredAnnotation(FieldDefine.class);
            if (Objects.isNull(fieldDefine)) {
                return StringUtils.EMPTY;
            }
            return fieldDefine.desc() + ":";
        } catch (NoSuchFieldException e) {
            return StringUtils.EMPTY;
        }
    }
}
