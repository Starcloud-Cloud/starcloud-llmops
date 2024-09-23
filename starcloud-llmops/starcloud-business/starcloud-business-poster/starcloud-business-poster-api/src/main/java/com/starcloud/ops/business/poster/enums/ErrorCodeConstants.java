package com.starcloud.ops.business.poster.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode TEMPLATETYPE_NOT_EXISTS = new ErrorCode(600003001, "海报模板类型不存在");

    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(600003002, "海报模板不存在");

    ErrorCode ELEMENTTYPE_NOT_EXISTS = new ErrorCode(600003003, "海报元素类型不存在");

    ErrorCode ELEMENT_NOT_EXISTS = new ErrorCode(600003004, "海报元素不存在");




    ErrorCode MATERIAL_CATEGORY_NOT_EXISTS = new ErrorCode(600003001, "当前分类不存在，请重新选择");
    ErrorCode CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(600003002, "上级分类不存在");
    ErrorCode CATEGORY_EXISTS_CHILDREN = new ErrorCode(600003003, "存在子分类，无法删除");
    ErrorCode CATEGORY_HAVE_BIND_SPU = new ErrorCode(600003004, "类别下存在商品，无法删除");
    ErrorCode CATEGORY_DISABLED = new ErrorCode(600003005, "素材分类({})已禁用，无法使用");



    ErrorCode MATERIAL_GROUP_NOT_EXISTS = new ErrorCode(600003101, "当前分组不存在，请重新选择");
    ErrorCode MATERIAL_PUBLISH_FAIL_EMPTY = new ErrorCode(600003102, "发布失败，数据为空，无法发布");



    ErrorCode SPU_MATERIAL_FAIL_CATEGORY_LEVEL_ERROR = new ErrorCode(600004001, "素材分类不正确，原因：必须使用第三级的素材分类及以下");


}