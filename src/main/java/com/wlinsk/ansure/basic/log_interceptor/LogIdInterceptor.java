package com.wlinsk.ansure.basic.log_interceptor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
@Slf4j
public class LogIdInterceptor implements HandlerInterceptor, Ordered {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String reqUlr = httpServletRequest.getRequestURI();
        MDC.put("reqUlr", reqUlr);
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        Map<String, Object> header = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            header.put(key, httpServletRequest.getHeader(key));
        }
        Request request = new Request();
        request.setLanguage(header.getOrDefault("accept-language", "zh-CN").toString());
        Context.initContext(request);

        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> logMap = new HashMap<>();
        Object reqParameter = Objects.nonNull(parameterMap) && parameterMap.size() > 0 ?
                parameterMap : getReqParameterString(httpServletRequest);
        logMap.put("request", getContent(reqParameter));
        logMap.put("headers", header);
        log.info("request:{}", JSON.toJSONString(logMap, JSONWriter.Feature.WriteMapNullValue));
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) throws Exception {
        try {
            //1.记录响应报文
            if (response instanceof ServletResponseWrapper && (((ServletResponseWrapper) response)
                    .getResponse() instanceof BodyCacheHttpServletResponseWrapper)) {
                ServletResponseWrapper servletResponseWrapper = (ServletResponseWrapper) response;
                BodyCacheHttpServletResponseWrapper responseWrapper = (BodyCacheHttpServletResponseWrapper) servletResponseWrapper
                        .getResponse();
                String responseBody = new String(responseWrapper.getBody(), StandardCharsets.UTF_8);
                Context.setResponse(responseBody);
            }

            if (response instanceof BodyCacheHttpServletResponseWrapper) {
                BodyCacheHttpServletResponseWrapper responseWrapper = (BodyCacheHttpServletResponseWrapper) response;
                String responseBody = new String(responseWrapper.getBody(), StandardCharsets.UTF_8);
                Context.setResponse(responseBody);
            }
            resultLog(request);

        } catch (Exception e) {
            log.error("[CommonInterceptor afterCompletion] error:", e);
        } finally {
            MDC.remove("traceId");
            MDC.remove("reqUlr");
            Context.clearContext();
        }

    }
    private Object getContent(Object context) {
        if (!(context instanceof String)) {
            return context;
        } else {
            try {
                String text = (String) context;
                return text.length() > 3000 ? "Response message length is too long to hide" : JSONObject.parse(text);
            } catch (Exception var3) {
                return context;
            }
        }
    }

    public static String getReqParameterString(HttpServletRequest httpServletRequest) {
        ServletInputStream inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = httpServletRequest.getInputStream();
            bytes = readMessageAndClose(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error("unsupportedEncodingException", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        } catch (IOException e) {
            log.error("io exception", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public static byte[] readMessageAndClose(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[4096];
            int count = -1;
            while ((count = in.read(data, 0, 4096)) != -1)
                outStream.write(data, 0, count);
            data = null;
            return outStream.toByteArray();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("关闭流失败",e);
                }
                in = null;
            }
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    log.error("关闭流失败",e);
                }
                outStream = null;
            }
        }

    }


    private void resultLog(HttpServletRequest request) {
        Request req = Context.getCurrentRequest();
        //forward 下因为finally会clear，所以这里可能为null
        if (req == null) {
            return;
        }
        Object data = Context.getResponse();
        Map<String, Object> logMap = new HashMap<>(4);
        if (needPrintResponse(request)) {
            logMap.put("response", getContent(data));
        } else {
            logMap.put("response", "response don't need print");
        }
        // 记录最后的输出日志
        log.info("{}", JSON.toJSONString(logMap, JSONWriter.Feature.WriteMapNullValue));
    }

    private boolean needPrintResponse(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept == null) {
            return true;
        }
        return !accept.contains("html") && !accept.contains("xml");
    }

    @Override
    public int getOrder() {
        return 19999;
    }
}
