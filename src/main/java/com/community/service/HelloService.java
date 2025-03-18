package com.community.service;

import org.springframework.stereotype.Service;

@Service 
public class HelloService {

    public String getHelloMessage() {
        return "안녕하세요! 이것은 Service 계층에서 제공하는 메시지입니다.";
    }
}