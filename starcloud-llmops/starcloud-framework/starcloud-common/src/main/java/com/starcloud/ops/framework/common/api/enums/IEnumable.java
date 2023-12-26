package com.starcloud.ops.framework.common.api.enums;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 枚举接口，为实现该接口的枚举类提供统一的方法
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
@SuppressWarnings("unused")
public interface IEnumable<T> {

    /**
     * 判断 name 是否在枚举类的某个枚举中
     *
     * @param name  枚举的 name。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类 。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 false。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> Boolean contains(String name, Class<E> clazz) {
        if (StrUtil.isBlank(name) || Objects.isNull(clazz)) {
            return Boolean.FALSE;
        }
        return Arrays.stream(clazz.getEnumConstants()).anyMatch(item -> item.name().equals(name));
    }

    /**
     * 判断 code 是否在枚举类的某个枚举中
     *
     * @param code  枚举的 code。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类 。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 false。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> Boolean containsOfCode(T code, Class<E> clazz) {
        if (Objects.isNull(code) || Objects.isNull(clazz)) {
            return Boolean.FALSE;
        }
        return Arrays.stream(clazz.getEnumConstants()).anyMatch(item -> item.getCode().equals(code));
    }

    /**
     * 根据 code 获取枚举的 name
     *
     * @param code  枚举的 code。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类 。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> String codeOfName(T code, Class<E> clazz) {
        Optional<E> optional = codeOf(code, clazz, Boolean.FALSE);
        return optional.map(Enum::name).orElse(null);
    }

    /**
     * 根据 code 获取枚举对象
     *
     * @param code  枚举的 code。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> String codeOfLabel(T code, Class<E> clazz) {
        Optional<E> optional = codeOf(code, clazz, Boolean.FALSE);
        return optional.map(IEnumable::getLabel).orElse(null);
    }

    /**
     * 根据 code 获取枚举对象
     *
     * @param code  枚举的 code。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> E codeOf(T code, Class<E> clazz) {
        return codeOf(code, clazz, Boolean.FALSE).orElse(null);
    }

    /**
     * 根据 code 获取枚举对象
     *
     * @param code  枚举的 code。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类。<br>
     * @param first 是否获取第一个匹配的枚举对象，如果为 null 则返回 false。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> Optional<E> codeOf(T code, Class<E> clazz, Boolean first) {
        if (Objects.isNull(code) || Objects.isNull(clazz)) {
            return Optional.empty();
        }
        Stream<E> stream = values(clazz).stream().filter(item -> Objects.equals(item.getCode(), code));
        return Optional.ofNullable(first).orElse(Boolean.FALSE) ? stream.findFirst() : stream.findAny();
    }

    /**
     * 根据 code 获取枚举对象
     *
     * @param name  枚举的 name。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> E nameOf(String name, Class<E> clazz) {
        return nameOf(name, clazz, Boolean.FALSE).orElse(null);
    }

    /**
     * 根据 name 获取枚举对象
     *
     * @param name  枚举的 name。<br>
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类。<br>
     * @param first 是否获取第一个匹配的枚举对象，如果为 null 则默认为 false。<br>
     * @param <E>   枚举类
     * @param <T>   枚举的 code 类型
     * @return 如果 code 为 null，则返回 null。<br>
     */
    static <E extends Enum<E> & IEnumable<T>, T> Optional<E> nameOf(String name, Class<E> clazz, Boolean first) {
        if (Objects.isNull(name) || Objects.isNull(clazz)) {
            return Optional.empty();
        }
        Stream<E> stream = values(clazz).stream().filter(item -> Objects.equals(item.name(), name));
        return Optional.ofNullable(first).orElse(Boolean.FALSE) ? stream.findFirst() : stream.findAny();
    }

    /**
     * 获取枚举类的所有枚举对象集合 <br>
     *
     * @param clazz 枚举类，必须实现 {@link IEnumable} 接口且为枚举类，如果为 null 则返回空集合。
     * @param <E>   枚举类型。
     * @param <T>   枚举 code 类型。
     * @return clazz 的枚举对象集合。
     */
    static <E extends Enum<E> & IEnumable<T>, T> List<E> values(Class<E> clazz) {
        return values(clazz, (E) null);
    }

    /**
     * 将指定枚举类元素转换为枚举对象集合 <br>
     *
     * @param items 枚举类元素，如果为 null 则返回空集合。
     * @param <E>   枚举类型。
     * @param <T>   枚举 code 类型。
     * @return 枚举对象集合。
     */
    @SafeVarargs
    static <E extends Enum<E> & IEnumable<T>, T> List<E> values(E... items) {
        if (Objects.isNull(items) || items.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(items).collect(Collectors.toList());
    }

    /**
     * 获取排除掉 exclude 的所有枚举类对象 <br>
     *
     * @param clazz   枚举类，必须实现 {@link IEnumable} 接口且为枚举类，如果为 null 则返回空集合。
     * @param exclude 排除的枚举对象，如果为 null 则返回所有枚举对象。
     * @param <E>     枚举类型。
     * @param <T>     枚举 code 类型。
     * @return clazz 的枚举对象集合。
     */
    @SafeVarargs
    static <E extends Enum<E> & IEnumable<T>, T> List<E> values(Class<E> clazz, E... exclude) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        Stream<E> stream = Arrays.stream(clazz.getEnumConstants());
        if (Objects.isNull(exclude) || exclude.length == 0) {
            return stream.collect(Collectors.toList());
        }
        List<E> excludeList = Arrays.asList(exclude);
        return stream.filter(item -> !excludeList.contains(item)).collect(Collectors.toList());
    }

    /**
     * 获取枚举编码
     *
     * @return 枚举值
     */
    T getCode();

    /**
     * 获取枚举标签
     *
     * @return 枚举标签
     */
    String getLabel();

    /**
     * 获取描述 <br>
     *
     * @return 描述
     */
    default String getDescription() {
        return getLabel();
    }

}
