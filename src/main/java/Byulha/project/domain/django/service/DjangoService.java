package Byulha.project.domain.django.service;

import Byulha.project.domain.django.exception.CannotConnectToPythonServerException;
import Byulha.project.domain.django.model.dto.request.RequestSendToDjangoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DjangoService {

    private final ObjectMapper objectMapper;

    @Transactional
    public List<Map.Entry<String, String>> sendToDjango(RequestSendToDjangoDto dto) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String param = objectMapper.writeValueAsString(dto);

        HttpEntity<String> entity = new HttpEntity<String>(param , headers);

//      Django 서버에서 올 데이터 형식
//        response =
//        {
//            "nickname": nickname,
//            "fileId" : fileId,
//            "category_name" : category_name
//        }

        String url = "http://127.0.0.1:8082/receive_string/";

        if (restTemplate.postForObject(url, entity, String.class) == null) {
            throw new CannotConnectToPythonServerException();
        }
        String response = restTemplate.postForObject(url, entity, String.class);

        HashMap<String, String> resultMap = convertData(response);

        List<Map.Entry<String, String>> resultList = new ArrayList<>(resultMap.entrySet());

        return resultList;
    }

    private HashMap<String, String> convertData(String response) {
        HashMap<String, String> resultMap = new HashMap<>();

        String[] responseArray = response.split(",");
        for(String data : responseArray) {
            String[] keyValue = data.split(": ");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("[{}\"\n']", "").trim();
                String value = keyValue[1].trim().replaceAll("[{}\"\n']", "").trim();
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }
}
