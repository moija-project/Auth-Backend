package com.example.loginservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.EncodeException;
import feign.form.multipart.AbstractWriter;
import feign.form.multipart.Output;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.context.annotation.Bean;

import static feign.form.ContentProcessor.CRLF;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MyPojoWriter extends AbstractWriter {

    private ObjectMapper objectMapper;

    public MyPojoWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isApplicable(Object object) {
        return isUserPojo(object);
    }

    @Override
    protected void write(Output output, String key, Object value) throws EncodeException {
        var data = "";
        try {
            data = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
        }
        String string = new StringBuilder()
                .append("Content-Disposition: form-data; name=\"").append(key).append('"').append(CRLF)
                .append("Content-Type: application/json; charset=").append(output.getCharset().name()).append(CRLF)
                .append(CRLF)
                .append(data)
                .toString();

        output.write(string);
    }

    private boolean isUserPojo(@NonNull Object object) {
        val type = object.getClass();
        val typePackage = type.getPackage();
        return typePackage != null && typePackage.getName().startsWith("com.example.loginservice");
    }
}