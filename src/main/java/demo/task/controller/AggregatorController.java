package demo.task.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.task.model.Response;
import demo.task.model.VideoData;
import demo.task.service.Parser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AggregatorController {
    private Logger logger = LoggerFactory.getLogger(AggregatorController.class);
    private final String url = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5";
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Parser parser;
    private final ExecutorService executor;

    @GetMapping("/aggregate")
    public String aggregate() throws JsonProcessingException {
        List<Response> responseList = new ArrayList<>();
        VideoData[] array = parser.readJsonFromUrl(url, VideoData[].class);
        List<Future<Optional<Response>>> futureList = new ArrayList<>();
        Arrays.stream(array).forEach(videoData -> {
            Future<Optional<Response>> future = executor.submit(() -> parser.parse(videoData));
            futureList.add(future);
        });
        futureList.forEach(f -> {
            try {
                f.get().ifPresent(responseList::add);
            } catch (Exception e) {
                logger.warn(e.toString());
            }
        });
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseList);
    }
}
