package com.starcloud.ops.business.app.util.qlOperator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 多字段支持 docs.print('author','bookName')
 */
@Slf4j
public class PrintOperator extends OperatorBase {

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {

        OperateData docs = list.get(0);

        int length = list.length;

        if (length > 1) {
            OperateData lastData = list.get(length - 1);


        }

        List<String> fieldList = new ArrayList<>(length - 1);
        for (int i = 1; i < length; i++) {
            fieldList.add((String) list.get(i).getObject(parent));


        }

        log.info("list: {}", list);

        Object ddocs = docs.getObject(parent);

        List<Object> result = new ArrayList<>();
        if (ddocs instanceof List) {
            List<Object> fdocs = (List<Object>) ddocs;

            for (int i = 0; i < fdocs.size(); i++) {
                Object doc = fdocs.get(i);
                StringJoiner sj = new StringJoiner("\r\n");
                for (String field : fieldList) {
                    Object fieldValue = BeanUtil.getFieldValue(doc, field);
                    if (Objects.nonNull(fieldValue)) {
                        sj.add(fieldValue.toString());
                    }
                }
                result.add(sj);
            }
            return new OperateData(StrUtil.join("\r\n\r\n", result), String.class);
        }

        return null;
    }
}
