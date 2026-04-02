package org.example.emailsendproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

    // <메시지의 Key 타입, 메시지의 Value 타입>
    // Kafka에 넣는 메시지는 <String, String> 를< Key-Value> 형태로 넣을 수도 있고,
    // Key는 생략한 채로 Value만 넣을 수도 있다고 얘기했다.
    // 실습에서는 메시지를 만들 때 key는 생략한 채로 value만 넣을 예정이다.
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EmailService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(SendEmailRequestDto request) {// SendEmailRequestDto -> EmailSendMessage
        // 위에서 메시지의 valueEmailSendMessage 타입을 String으로 설정을 했다.
        // 그래서  객체를 String(json 형태)으로 변환해서 넣어주어야 한다.
        // yml 파일에서 메시지의 key 직렬화 방식 : 자바 객체를 문자열(String)로 변환해서 Kafka에 전송 한다고 정의 내렸다.

        EmailSendMessage emailSendMessage = new EmailSendMessage(
                request.getFrom(),
                request.getTo(),
                request.getSubject(),
                request.getBody()
        );
        //EmailSendMessage는 string이 아니라 java 객채라서 stirng으로 변환 해야함 'toJsonString' 이걸로
        this.kafkaTemplate.send("email.send", toJsonString(emailSendMessage));// 카프카에 메시지 보내기(send)
    }

    // 객체를 Json 형태의 String으로 만들어주는 메서드
    // (클래스로 분리하면 더 좋지만 편의를 위해 메서드로만 분리)
    private String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // 또는 기본값 리턴
        }
    }
}