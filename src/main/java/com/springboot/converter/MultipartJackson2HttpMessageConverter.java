package com.springboot.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

//스웨거에서는 application/octet-stream 이라는 데이터 형식을 지원하지 않는다.
//application/octet-stream은 서버가 요청보낸 데이터의 MIME(알 수 없는 이진타입)타입을 알지 못할때 분류하는 값이다.
//즉, Content-Type을 알 수 없는 경우에 application/octet-stream으로 세팅된다.
//다음으로, requestBody를 컨버터 객체를 이용해 변환하는 로직 실행 -> 모든 컨버터를 순회하며 현재 요청을 반환할 수 있는 컨버터 탐색
//만약 requestBody의 Content-Type을 변환할 수 있는 컨버터가 없다면 body 변수가 NO_VALUE로 남게되어 HttpMediaTypeNotSupportedException가 발생 (application/octet-stream' is not supported 예외)

//AbstractJackson2HttpMessageConverter클래스를 상속받아 application/octet-stream 타입을 담당할 컨버터를 추가해주면 해결된다.
//해당 컨버터가 해당 타입이 들어오다로 JSON body로 변환할 수 있기에 예외가 발생하지 않음!
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    //생성자에 MediaType.APPLICATION_OCTET_STREAM를 전달
    //readWithMessageConverters 메서드에서 해당 타입을 찾을때 이 커스텀 컨버터가 선택된다.
    //이후 내부에 read 메서드를 통해 바디를 변환 가능!
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}
