package com.starcloud.ops.business.app.service.template;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.StyleRecordRespVO;
import com.starcloud.ops.business.app.dal.databoject.template.StyleRecordDO;
import com.starcloud.ops.business.app.dal.databoject.template.StyleRecordDTO;
import com.starcloud.ops.business.app.dal.mysql.template.StyleRecordMapper;
import com.starcloud.ops.business.app.exception.TemplateErrorData;
import com.starcloud.ops.business.app.exception.TemplateException;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.NOT_TEMPLATE_RESOURCE;

@Slf4j
@Service
public class TemplateRecordServiceImpl implements TemplateRecordService {

    @Resource
    private StyleRecordMapper recordMapper;

    @Resource
    private AdminUserRightsApi rightsApi;

    @Override
    public void addRecord(List<PosterStyleDTO> posterStyleDTOList, String planUid) {
        Set<String> styleUidList = CreativeUtils.getPosterStyleUids(posterStyleDTOList);
        List<StyleRecordDO> recordDOList = recordMapper.selectList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<StyleRecordDO> addStyleRecords = new ArrayList<>();

        List<String> recordCodeList = recordDOList.stream().map(StyleRecordDO::getStyleUid).collect(Collectors.toList());
        for (String styleUid : styleUidList) {
            if (!recordCodeList.contains(styleUid)) {
                StyleRecordDO addRecord = new StyleRecordDO();
                addRecord.setUid(IdUtil.fastSimpleUUID());
                addRecord.setStyleUid(styleUid);
                addRecord.setPlanUid(planUid);
                addStyleRecords.add(addRecord);
            }
        }
        checkRecordNum(posterStyleDTOList);
        recordMapper.insertBatch(addStyleRecords);
    }

    @Override
    public List<StyleRecordRespVO> listRecord() {
        List<StyleRecordDTO> styleRecordDTOList = recordMapper.templateList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<StyleRecordRespVO> result = new ArrayList<>();
        for (StyleRecordDTO styleRecordDTO : styleRecordDTOList) {
            String imageStyleJson = styleRecordDTO.getImageStyleList();
            if (StringUtils.isBlank(imageStyleJson)) {
                continue;
            }
            String styleUid = styleRecordDTO.getStyleUid();
            List<PosterStyleDTO> posterStyleList = JSONUtil.parseArray(imageStyleJson).toList(PosterStyleDTO.class);
            for (PosterStyleDTO posterStyleDTO : posterStyleList) {
                if (Objects.equals(styleUid, posterStyleDTO.getUuid())) {
                    StyleRecordRespVO styleRecordRespVO = new StyleRecordRespVO();
                    styleRecordRespVO.setStyleUid(styleUid);
                    styleRecordRespVO.setPosterStyle(posterStyleDTO);
                    styleRecordRespVO.setUid(styleRecordDTO.getUid());
                    styleRecordRespVO.setCreateTime(styleRecordDTO.getCreateTime());
                    result.add(styleRecordRespVO);
                }
            }
        }

        return result;
    }

    @Override
    public void checkRecordNum(List<PosterStyleDTO> posterStyleDTOList) {
        List<StyleRecordDO> recordDOList = recordMapper.selectList(String.valueOf(WebFrameworkUtils.getLoginUserId()));
        List<String> recordCodeList = recordDOList.stream().map(StyleRecordDO::getStyleUid).collect(Collectors.toList());

        Set<TemplateErrorData> addTemplateDataSet = new HashSet<>();
        for (PosterStyleDTO posterStyleDTO : posterStyleDTOList) {
            if (Objects.isNull(posterStyleDTO.getSaleConfig())
                    || BooleanUtils.isNotTrue(posterStyleDTO.getSaleConfig().getOpenSale())) {
                continue;
            }

            if ((!recordCodeList.contains(posterStyleDTO.getUuid()))) {
                TemplateErrorData templateErrorData = new TemplateErrorData(posterStyleDTO.getUuid(), posterStyleDTO.getSaleConfig().getDemoId());
                addTemplateDataSet.add(templateErrorData);
            }
        }
        long count = addTemplateDataSet.stream().map(TemplateErrorData::getStyleUid).distinct().count();

        Integer originalFixedRightsSums = rightsApi.getOriginalFixedRightsSums(WebFrameworkUtils.getLoginUserId(), AdminUserRightsTypeEnum.TEMPLATE.getType());
        if (count == 0) {
            return;
        }
        int total = recordDOList.size() + (int) count;
        if (Objects.isNull(originalFixedRightsSums) || total > originalFixedRightsSums) {
            throw new TemplateException(NOT_TEMPLATE_RESOURCE, addTemplateDataSet);
        }
    }
}
