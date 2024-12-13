package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.AiIdentifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginListReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.convert.plugin.PluginDefinitionConvert;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryAppBindMapper;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryMapper;
import com.starcloud.ops.business.app.dal.mysql.plugin.PluginDefinitionMapper;
import com.starcloud.ops.business.app.enums.plugin.*;
import com.starcloud.ops.business.app.feign.CozePublicClient;
import com.starcloud.ops.business.app.feign.dto.coze.BotListInfo;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceListInfo;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.job.api.BusinessJobApi;
import com.starcloud.ops.business.job.dto.JobDetailDTO;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;

@Slf4j
@Service
public class PluginsDefinitionServiceImpl implements PluginsDefinitionService {

    @Resource
    private PluginDefinitionMapper pluginDefinitionMapper;

    @Resource
    private CozePublicClient cozePublicClient;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private PluginConfigService configService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BusinessJobApi businessJobApi;

    @Resource
    private MaterialLibraryAppBindMapper materialLibraryAppBindMapper;

    @Resource
    private MaterialLibraryMapper materialLibraryMapper;

    @Resource
    private DeptPermissionApi deptPermissionApi;

    @Resource
    private AppMarketService appMarketService;


    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("platform", PlatformEnum.options());
        metadata.put("scene", PluginSceneEnum.options());
        metadata.put("outputType", OutputTypeEnum.options());
        metadata.put("processManner", ProcessMannerEnum.options());
        return metadata;
    }


    /**
     * 更新插件执行时间
     */
    @Override
    public void updateTime(Long time, String pluginUid) {
        RLock lock = redissonClient.getLock(pluginUid);
        try {
            if (lock.tryLock(5, 5, TimeUnit.SECONDS)) {
                PluginDefinitionDO pluginDefinitionDO = getByUid(pluginUid);
                pluginDefinitionDO.setTotalTime((pluginDefinitionDO.getTotalTime() == null ? 0 : pluginDefinitionDO.getTotalTime()) + time);
                pluginDefinitionDO.setCount((pluginDefinitionDO.getCount() == null ? 0 : pluginDefinitionDO.getCount()) + 1);
                pluginDefinitionDO.setExecuteTimeAvg(time);
                pluginDefinitionMapper.updateById(pluginDefinitionDO);
            }
        } catch (Exception e) {
            log.warn("update time error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public SpaceListInfo spaceList(String accessTokenId, Integer pageSize, Integer pageIndex) {
        CozeResponse<SpaceListInfo> spaceList = cozePublicClient.spaceList(bearer(accessTokenId), pageSize, pageIndex);
        if (spaceList.getCode() != 0) {
            throw exception(COZE_ERROR, spaceList.getMsg());
        }
        return spaceList.getData();
    }

    @Override
    public PluginRespVO create(PluginDefinitionVO pluginVO) {
        PluginDefinitionDO pluginConfig = pluginDefinitionMapper.selectByName(pluginVO.getPluginName());
        if (Objects.nonNull(pluginConfig)) {
            throw exception(NAME_DUPLICATE, pluginVO.getPluginName());
        }

        // 校验coze token 的部门
//        SocialUserDO socialUser = socialUserService.getNewSocialUser(Long.valueOf(pluginVO.getCozeTokenId()));
//        deptPermissionApi.adminEditPermission(socialUser.getDeptId());

        PluginDefinitionDO pluginConfigDO = PluginDefinitionConvert.INSTANCE.convert(pluginVO);
        if (PlatformEnum.coze.getCode().equalsIgnoreCase(pluginConfigDO.getType())) {
            CozeBotInfo cozeBotInfo = botInfo(pluginConfigDO.getEntityUid(), pluginConfigDO.getCozeTokenId());
            pluginConfigDO.setEntityName(cozeBotInfo.getName());
        }

        pluginConfigDO.setUid(IdUtil.fastSimpleUUID());
        pluginDefinitionMapper.insert(pluginConfigDO);
        return PluginDefinitionConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    @DataPermission(enable = false)
    public List<PluginRespVO> publishedList() {
        List<PluginDefinitionDO> pluginDOList = pluginDefinitionMapper.publishedList();
        return PluginDefinitionConvert.INSTANCE.convert(pluginDOList);
    }

    @Override
    public List<PluginRespVO> ownerList() {
        List<PluginDefinitionDO> pluginDOList = pluginDefinitionMapper.selectOwnerPlugin();
        if (CollectionUtil.isEmpty(pluginDOList)) {
            return Collections.emptyList();
        }

        List<String> socialList = pluginDOList.stream().map(PluginDefinitionDO::getCozeTokenId).collect(Collectors.toList());
        Map<String, SocialUserDO> socialUser = socialUserService.getSocialUser(socialList);

        List<PluginRespVO> result = new ArrayList<>(pluginDOList.size());
        for (PluginDefinitionDO pluginDefinitionDO : pluginDOList) {
            PluginRespVO pluginRespVO = PluginDefinitionConvert.INSTANCE.convert(pluginDefinitionDO);
            SocialUserDO socialUserDO = socialUser.get(pluginDefinitionDO.getCozeTokenId());
            if (Objects.nonNull(socialUserDO)) {
                pluginRespVO.setAccountName(socialUserDO.getNickname());
            }
            result.add(pluginRespVO);
        }
        return result;
    }

    @Override
    public List<PluginRespVO> list(PluginListReqVO reqVO) {
        List<PluginConfigRespVO> configList = configService.configList(reqVO.getLibraryUid());
        if (CollectionUtil.isEmpty(configList)) {
            return Collections.emptyList();
        }
        Map<String, PluginConfigRespVO> map = configList.stream().collect(Collectors.toMap(PluginConfigVO::getPluginUid, Function.identity(), (a, b) -> a));
        List<PluginDefinitionDO> pluginDefinitionDOList = pluginDefinitionMapper.selectByUid(configList.stream().map(PluginConfigVO::getPluginUid).collect(Collectors.toList()));
        List<PluginRespVO> result = PluginDefinitionConvert.INSTANCE.convert(pluginDefinitionDOList);
        List<Long> creatorList = pluginDefinitionDOList.stream().map(item -> Long.valueOf(item.getCreator())).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);
        List<JobDetailDTO> jobDetailList = businessJobApi.queryJob(configList.stream().map(PluginConfigRespVO::getUid).collect(Collectors.toList()));
        Map<String, JobDetailDTO> jobMap = jobDetailList.stream().collect(Collectors.toMap(JobDetailDTO::getForeignKey, Function.identity(), (a, b) -> a));
        result.forEach(plugin -> {
            if ((plugin.getCreator() != null)) {
                plugin.setCreator(creatorMap.get(Long.valueOf(plugin.getCreator())));
            }
            PluginConfigRespVO configRespVO = map.get(plugin.getUid());
            if (Objects.nonNull(configRespVO)) {
                plugin.setConfigUid(configRespVO.getUid());
                plugin.setSystemPlugin(PluginBindTypeEnum.isSys(configRespVO.getType()));
                plugin.setBindName(configRespVO.getBindName());
            } else {
                plugin.setBindName(plugin.getPluginName());
            }
            Boolean enable = Optional.ofNullable(jobMap.get(plugin.getConfigUid())).map(JobDetailDTO::getEnable).orElse(Boolean.FALSE);
            plugin.setJobEnable(BooleanUtil.isTrue(enable));
        });
        return result;
    }

    /**
     * 查询应用下的插件列表, 我的应用。
     *
     * @param appUid 应用uid
     * @return 插件列表
     */
    @Override
    public List<PluginRespVO> list(String appUid) {
        // 查询应用下的素材库绑定关系
        LambdaQueryWrapper<MaterialLibraryAppBindDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryAppBindDO.class);
        wrapper.eq(MaterialLibraryAppBindDO::getAppUid, appUid);
        wrapper.eq(MaterialLibraryAppBindDO::getDeleted, false);
        List<MaterialLibraryAppBindDO> libraryAppBindList = materialLibraryAppBindMapper.selectList(wrapper);
        if (CollUtil.isEmpty(libraryAppBindList) || libraryAppBindList.size() > 1) {
            return Collections.emptyList();
        }
        // 根据素材库绑定关系，查询插件查询素材库
        MaterialLibraryAppBindDO libraryAppBind = libraryAppBindList.get(0);
        MaterialLibraryDO materialLibrary = materialLibraryMapper.selectById(libraryAppBind.getLibraryId());
        if (materialLibrary == null) {
            return Collections.emptyList();
        }
        PluginListReqVO request = new PluginListReqVO();
        request.setLibraryUid(materialLibrary.getUid());
        return list(request);
    }

    @Override
    public void publish(String uid) {
        if (UserUtils.isNotAdmin()) {
            throw exception(NO_PERMISSIONS);
        }
        PluginDefinitionDO pluginDefinitionDO = getByUid(uid);
        deptPermissionApi.adminEditPermission(pluginDefinitionDO.getDeptId());
        pluginDefinitionDO.setPublished(true);
        pluginDefinitionMapper.updateById(pluginDefinitionDO);
    }

    @Override
    public PluginRespVO modifyPlugin(PluginConfigModifyReqVO reqVO) {
        PluginDefinitionDO pluginDefinitionDO = getByUid(reqVO.getUid());
//        SocialUserDO socialUser = socialUserService.getNewSocialUser(Long.valueOf(reqVO.getCozeTokenId()));

//        deptPermissionApi.adminEditPermission(pluginDefinitionDO.getDeptId(), socialUser.getDeptId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.plugin_edit, Long.valueOf(pluginDefinitionDO.getCreator()));

        PluginDefinitionDO updatePlugin = PluginDefinitionConvert.INSTANCE.convert(reqVO);
        if (PlatformEnum.coze.getCode().equalsIgnoreCase(pluginDefinitionDO.getType())) {
            CozeBotInfo cozeBotInfo = botInfo(pluginDefinitionDO.getEntityUid(), reqVO.getCozeTokenId());
            pluginDefinitionDO.setEntityName(cozeBotInfo.getName());
        }
        updatePlugin.setId(pluginDefinitionDO.getId());
        pluginDefinitionMapper.updateById(updatePlugin);
        return PluginDefinitionConvert.INSTANCE.convert(updatePlugin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        PluginDefinitionDO pluginDefinitionDO = getByUid(uid);
        deptPermissionApi.adminEditPermission(pluginDefinitionDO.getDeptId());
        deptPermissionApi.checkPermission(DeptPermissionEnum.plugin_delete, Long.valueOf(pluginDefinitionDO.getCreator()));
        pluginDefinitionMapper.deleteById(pluginDefinitionDO.getId());
        configService.deleteByPluginUid(pluginDefinitionDO.getUid());
    }

    @Override
    @DataPermission(enable = false)
    public PluginRespVO detail(String uid) {
        PluginDefinitionDO pluginConfigDO = getByUid(uid);
        return PluginDefinitionConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    public BotListInfo spaceBot(String spaceId, String accessTokenId, Integer pageSize, Integer pageIndex) {
        String accessToken = bearer(accessTokenId);
        CozeResponse<BotListInfo> listCozeResponse = cozePublicClient.spaceBots(spaceId, accessToken, pageSize, pageIndex);
        if (listCozeResponse.getCode() != 0) {
            throw exception(COZE_ERROR, listCozeResponse.getMsg());
        }
        return listCozeResponse.getData();
    }

    @Override
    public CozeBotInfo botInfo(String botId, String accessTokenId) {
        String accessToken = bearer(accessTokenId);
        // 测试连接
        CozeResponse<CozeBotInfo> cozeResponse = cozePublicClient.botInfo(botId, accessToken);
        if (cozeResponse.getCode() != 0) {
            throw exception(COZE_ERROR, cozeResponse.getMsg());
        }
        return cozeResponse.getData();
    }

    @Override
    public String bearer(String accessTokenId) {
        SocialUserDO socialUser = socialUserService.getNewSocialUser(Long.valueOf(accessTokenId));
        if (Objects.isNull(socialUser) || StringUtils.isBlank(socialUser.getToken())) {
            throw exception(TOKEN_ERROR, accessTokenId);
        }
        return "Bearer " + socialUser.getToken();
    }

    @Override
    public String getPrompt(AiIdentifyReqVO reqVO) {
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Collections.singletonList("PLUGIN_INPUT_GENERATE"));
        List<AppMarketRespVO> list = appMarketService.list(query);
        if (CollectionUtils.isEmpty(list)) {
            throw exception(PLUGIN_NOT_EXIST);
        }
        String userPrompt = list.get(0).getWorkflowConfig().getStepByHandler("OpenAIChatActionHandler").getVariableToString("USER_PROMPT");

        userPrompt = userPrompt.replaceAll("\\{STEP.生成文本.PLUGIN_DESC\\}", reqVO.getDescription());
        userPrompt = userPrompt.replaceAll("\\{STEP.生成文本.RESULT_FORMAT\\}", reqVO.getInputFormart());
        userPrompt = userPrompt.replaceAll("\\{STEP.生成文本.PLUGIN_NAME\\}", reqVO.getPluginName());
        userPrompt = userPrompt.replaceAll("\\{STEP.生成文本.USER_INPUT\\}", reqVO.getUserInput());

        return userPrompt;
    }

    private PluginDefinitionDO getByUid(String uid) {
        PluginDefinitionDO pluginConfigDO = pluginDefinitionMapper.selectByUid(uid);
        if (Objects.isNull(pluginConfigDO)) {
            throw exception(PLUGIN_NOT_EXIST, uid);
        }
        return pluginConfigDO;
    }
}
