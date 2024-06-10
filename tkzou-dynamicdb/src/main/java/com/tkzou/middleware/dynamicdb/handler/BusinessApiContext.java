package com.tkzou.middleware.dynamicdb.handler;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程上下文
 *
 * @author zoutongkun
 */
public class BusinessApiContext {

    private static final TransmittableThreadLocal<Map<String, String>> POS_DATA_SOURCE_HOLDER =
            new TransmittableThreadLocal<>();

    private static final BusinessApiContext API_CONTEXT = new BusinessApiContext();

    private BusinessApiContext() {

    }

    public static BusinessApiContext getInstance() {
        return API_CONTEXT;
    }

    public String getCurrentHotelId() {
        return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_HOTEL_CD);
    }

    public String getCurrentEnvironment() {
        return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_DB_TYPE);
    }

    public String getCurrentRezenSessionId() {
        return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_REZEN_SESSION_ID);
    }

    public String getRawUrl() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_RAW_URI)) {
            return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_RAW_URI);
        }

        return "unknown";
    }

    public Integer getWsNum() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_WS_NUM)) {
            return Integer.parseInt(POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_WS_NUM));
        }

        return Integer.valueOf(-1);
    }

    public String getWsNm() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_WS_NM)) {
            return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_WS_NM);
        }

        return "unknown";
    }

    public String getTerminal() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_TERMINAL)) {
            return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_TERMINAL);
        }

        return "unknown";
    }

    public String getOperCd() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_OPER_CD)) {
            return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_OPER_CD);
        }

        return "unknown";
    }

    public String getOperNm() {
        if (POS_DATA_SOURCE_HOLDER.get().containsKey(BusinessApiContextKey.HTTP_HEADER_OPER_NM)) {
            return POS_DATA_SOURCE_HOLDER.get().get(BusinessApiContextKey.HTTP_HEADER_OPER_NM);
        }

        return "unknown";
    }

    public void setCurrentHotelId(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null) {
            this.initContext();
        }
        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_HOTEL_CD, value);
    }

    public void setCurrentEnvironment(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null) {
            this.initContext();
        }
        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_DB_TYPE, value);
    }

    public void setCurrentRezenSessionId(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();
        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_REZEN_SESSION_ID, value);
    }

    public void setRawUrl(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_RAW_URI, value);
    }

    public void setWsNum(Integer value) {
        if (value == null)
            return;

        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_WS_NUM, value.toString());
    }

    public void setWsNm(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_WS_NM, value);
    }

    public void setTerminal(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_TERMINAL, value);
    }

    public void setOperCd(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_OPER_CD, value);
    }

    public void setOperNm(String value) {
        if (POS_DATA_SOURCE_HOLDER.get() == null)
            this.initContext();

        POS_DATA_SOURCE_HOLDER.get().put(BusinessApiContextKey.HTTP_HEADER_OPER_NM, value);
    }

    private void initContext() {
        POS_DATA_SOURCE_HOLDER.set(new HashMap<>());
    }

    public void clear() {
        POS_DATA_SOURCE_HOLDER.remove();
    }
}
