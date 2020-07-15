package com.trade.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author georgy
 * @Date 2020-04-17 上午 11:32
 * @DESC 通用工具类
 */
public class CommonUtil {

    /**
     * 获取对象属性为null的集合
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * @param list 数据集合
     * @param localDate
     * @param limit 限制次数
     * @param direction 方向： -1: 左边  1: 右边
     * @return
     */
    public static int getValidIndexToList(List list, LocalDate localDate, int limit, int direction ){
        for (int i = 0; i < limit; i++) {
            int index = list.indexOf(localDate.minusDays(i * direction).format(TimeUtil.SHORT_DATE_FORMATTER));
            if(index != -1){
                return index;
            }
        }
        return -1;
    }

}
