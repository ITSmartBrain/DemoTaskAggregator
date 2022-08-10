package demo.task.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.task.model.Response;
import demo.task.model.SourceData;
import demo.task.model.TokenData;
import demo.task.model.VideoData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class ParserTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    @SpyBean
    private Parser parser;
    @Test
    void testParseReturnsCorrectResponse() throws JsonProcessingException {
        VideoData videoData = new VideoData(1, "sourceUrl1", "tokenUrl1");
        SourceData sourceData = new SourceData("urlType1", "videoUrl1");
        TokenData tokenData = new TokenData("value1", 100);

        doReturn(sourceData).when(parser).readJsonFromUrl("sourceUrl1", SourceData.class);
        doReturn(tokenData).when(parser).readJsonFromUrl("tokenUrl1", TokenData.class);

        Optional<Response> expected = Optional.of(new Response(
                videoData.getId(),
                sourceData.getUrlType(),
                sourceData.getVideoUrl(),
                tokenData.getValue(),
                tokenData.getTtl()));
        Optional<Response> actual = parser.parse(videoData);

        assertEquals(expected, actual);
    }

    @Test
    void testParseReturnsEmptyOptionIfDataIsNull() throws JsonProcessingException {
        doReturn(null).when(parser).readJsonFromUrl("sourceUrl1", SourceData.class);
        doReturn(null).when(parser).readJsonFromUrl("tokenUrl1", TokenData.class);
        Optional<Response> actual = parser.parse(null);
        Optional<Response> expected = Optional.empty();
        assertEquals(expected, actual);
    }

    @Test
    void testReadJsonFromUrlWithSourceData() throws JsonProcessingException {
        SourceData expected = new SourceData("urlType1", "videoUrl1");
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
        ResponseEntity<String> response = ResponseEntity.of(Optional.of(json));
        doReturn(response).when(parser).send(any());
        SourceData actual = parser.readJsonFromUrl("someurl", SourceData.class);
        assertEquals(expected, actual);
    }

    @Test
    void testReadJsonFromUrlWithTokenData() throws JsonProcessingException {
        TokenData expected = new TokenData("value1", 100);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
        ResponseEntity<String> response = ResponseEntity.of(Optional.of(json));
        doReturn(response).when(parser).send(any());
        TokenData actual = parser.readJsonFromUrl("someurl", TokenData.class);
        assertEquals(expected, actual);
    }

    @Test
    void testReadJsonFromUrlWithVideoData() throws JsonProcessingException {
        VideoData expected = new VideoData(1, "sourceUrl1", "tokenUrl1");
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
        ResponseEntity<String> response = ResponseEntity.of(Optional.of(json));
        doReturn(response).when(parser).send(any());
        VideoData actual = parser.readJsonFromUrl("someurl", VideoData.class);
        assertEquals(expected, actual);
    }
}