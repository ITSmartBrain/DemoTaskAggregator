package demo.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.task.model.Response;
import demo.task.model.VideoData;
import demo.task.service.Parser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AggregatorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Parser parser;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAggregateReturnsStatusOkAndJsonArray() throws Exception {
        Response[] data = {
                new Response(1, "LIVE", "rtsp://127.0.0.1/1", "fa4b588e-249b-11e9-ab14-d663bd873d93", 120),
                new Response(20, "ARCHIVE", "rtsp://127.0.0.1/2", "fa4b5b22-249b-11e9-ab14-d663bd873d93", 60),
                new Response(3, "ARCHIVE", "rtsp://127.0.0.1/3", "fa4b5d52-249b-11e9-ab14-d663bd873d93", 120),
                new Response(2, "LIVE", "rtsp://127.0.0.1/20", "fa4b5f64-249b-11e9-ab14-d663bd873d93", 180)
        };
        VideoData vd1 = new VideoData(1, "url1", "url2");
        VideoData vd2 = new VideoData(20, "url3", "url4");
        VideoData vd3 = new VideoData(3, "url5", "url6");
        VideoData vd4 = new VideoData(2, "url7", "url8");
        VideoData[] videoData = {vd1,vd2,vd3,vd4};
        String expected = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        when(parser.readJsonFromUrl(anyString(), any())).thenReturn(videoData);
        when(parser.parse(vd1)).thenReturn(Optional.of(data[0]));
        when(parser.parse(vd2)).thenReturn(Optional.of(data[1]));
        when(parser.parse(vd3)).thenReturn(Optional.of(data[2]));
        when(parser.parse(vd4)).thenReturn(Optional.of(data[3]));
        this.mockMvc.perform(get("/api/aggregate")).andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    public void testAggregateSkipNullObjects() throws Exception {
        Response[] data = {
                new Response(1, "LIVE", "rtsp://127.0.0.1/1", "fa4b588e-249b-11e9-ab14-d663bd873d93", 120),
                new Response(3, "ARCHIVE", "rtsp://127.0.0.1/3", "fa4b5d52-249b-11e9-ab14-d663bd873d93", 120),
                new Response(2, "LIVE", "rtsp://127.0.0.1/20", "fa4b5f64-249b-11e9-ab14-d663bd873d93", 180)
        };
        VideoData vd1 = new VideoData(1, "url1", "url2");
        VideoData vd2 = null;
        VideoData vd3 = new VideoData(3, "url5", "url6");
        VideoData vd4 = new VideoData(2, "url7", "url8");
        VideoData[] videoData = {vd1,vd2,vd3,vd4};
        String expected = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        when(parser.readJsonFromUrl(anyString(), any())).thenReturn(videoData);
        when(parser.parse(vd1)).thenReturn(Optional.of(data[0]));
        when(parser.parse(vd2)).thenReturn(Optional.empty());
        when(parser.parse(vd3)).thenReturn(Optional.of(data[1]));
        when(parser.parse(vd4)).thenReturn(Optional.of(data[2]));
        this.mockMvc.perform(get("/api/aggregate")).andExpect(status().isOk())
                .andExpect(content().string(expected));
    }



}