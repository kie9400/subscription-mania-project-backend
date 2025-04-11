package com.springboot.redis;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    // 이메일 인증 코드 전송 메서드
    public void setCode(String email, String authCode){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        // 유효 시간은 300초로 설정
        // 동일한 이메일로 재요청한다면 덮어쓰기 됨!
        valueOperations.set(email, authCode, 300, TimeUnit.SECONDS);
    }

    // 이메일 인증 코드 반환 메서드
    public String getCode(String email) throws BusinessLogicException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String authCode = (String) valueOperations.get(email);
        if(authCode == null)
            throw new BusinessLogicException(ExceptionCode.INVALID_CODE);
        return authCode;
    }

    //인증 성공시 해당 이메일을 레디스에서 삭제한다.
    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }
}
