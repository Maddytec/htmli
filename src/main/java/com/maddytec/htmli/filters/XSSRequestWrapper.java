package com.maddytec.htmli.filters;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private static final PolicyFactory POLICY_FACTORY = new HtmlPolicyBuilder().toFactory();

    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        var values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        var count = values.length;
        var encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = clearXss(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return value != null ? clearXss(value) : null;
    }

    @Override
    public String getHeader(String name) {
        var value = super.getHeader(name);
        return value != null ? clearXss(value) : null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> result = new ArrayList<>();
        var headers = super.getHeaders(name);
        while (headers.hasMoreElements()) {
            var header = headers.nextElement();
            var tokens = header.split(",");
            Arrays.stream(tokens).map(XSSRequestWrapper::clearXss).forEach(result::add);
        }

        return Collections.enumeration(result);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        final var byteArrayInputStream = new ByteArrayInputStream(
                sanitizerBodyInputStream(super.getInputStream()).getBytes());

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    private String sanitizerBodyInputStream(InputStream inputStream) {
        var body = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clearXss(body.toString());
    }


    public static String clearXss(String value) {
        var s = POLICY_FACTORY.sanitize(value);

//    if we only allow basic html tags
//        return Jsoup.clean(s, Safelist.basic());

//    allow selected tags and attributes
//        return Jsoup.clean(s, Safelist.relaxed().addAttributes("p", "b"));

//    if we do not want any html tag
        return Jsoup.clean(s, Safelist.none());
    }

}
