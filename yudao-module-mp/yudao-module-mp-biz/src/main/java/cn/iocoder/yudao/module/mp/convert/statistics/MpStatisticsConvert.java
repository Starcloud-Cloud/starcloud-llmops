package cn.iocoder.yudao.module.mp.convert.statistics;

import cn.iocoder.yudao.module.mp.controller.admin.statistics.vo.MpStatisticsInterfaceSummaryRespVO;
import cn.iocoder.yudao.module.mp.controller.admin.statistics.vo.MpStatisticsUpstreamMessageRespVO;
import cn.iocoder.yudao.module.mp.controller.admin.statistics.vo.MpStatisticsUserCumulateRespVO;
import cn.iocoder.yudao.module.mp.controller.admin.statistics.vo.MpStatisticsUserSummaryRespVO;
import me.chanjar.weixin.mp.bean.datacube.WxDataCubeInterfaceResult;
import me.chanjar.weixin.mp.bean.datacube.WxDataCubeMsgResult;
import me.chanjar.weixin.mp.bean.datacube.WxDataCubeUserCumulate;
import me.chanjar.weixin.mp.bean.datacube.WxDataCubeUserSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper
public interface MpStatisticsConvert {

    MpStatisticsConvert INSTANCE = Mappers.getMapper(MpStatisticsConvert.class);

    List<MpStatisticsUserSummaryRespVO> convertList01(List<WxDataCubeUserSummary> list);

    List<MpStatisticsUserCumulateRespVO> convertList02(List<WxDataCubeUserCumulate> list);

    List<MpStatisticsUpstreamMessageRespVO> convertList03(List<WxDataCubeMsgResult> list);

    @Mappings({
            @Mapping(source = "refDate", target = "refDate", qualifiedByName = "parseDate"),
            @Mapping(source = "msgUser", target = "messageUser"),
            @Mapping(source = "msgCount", target = "messageCount"),
    })
    MpStatisticsUpstreamMessageRespVO convert(WxDataCubeMsgResult bean);

    List<MpStatisticsInterfaceSummaryRespVO> convertList04(List<WxDataCubeInterfaceResult> list);

    @Mapping(source = "refDate", target = "refDate", qualifiedByName = "parseDate")
    MpStatisticsInterfaceSummaryRespVO convert(WxDataCubeInterfaceResult bean);

    @Named("parseDate")
    default LocalDateTime parseDate(String string) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(string, dateTimeFormatter).atStartOfDay();
    }
}
