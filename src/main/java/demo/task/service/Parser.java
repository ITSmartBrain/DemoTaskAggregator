package demo.task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.task.model.Response;
import demo.task.model.SourceData;
import demo.task.model.TokenData;
import demo.task.model.VideoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

@Service
public class Parser {
    private Logger logger = LoggerFactory.getLogger(Parser.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Response> parse(VideoData videoData) {

        try {
            SourceData sourceData = readJsonFromUrl(videoData.getSourceDataUrl(), SourceData.class);
            TokenData tokenData = readJsonFromUrl(videoData.getTokenDataUrl(), TokenData.class);
            Response response = new Response(
                    videoData.getId(),
                    sourceData.getUrlType(),
                    sourceData.getVideoUrl(),
                    tokenData.getValue(),
                    tokenData.getTtl()
            );
            return Optional.of(response);
        }catch (Exception e){
            logger.warn("Couldn't parse videoData object because of "+e);
        }
        return Optional.empty();

    }

    public <T> T readJsonFromUrl(String url, Class<T> clazz) throws JsonProcessingException {
        ResponseEntity<String> sourceResponse = send(url);
        return objectMapper.readValue(sourceResponse.getBody(), clazz);

    }

    public ResponseEntity<String> send(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

}
