package demo.task.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VideoData{
    private int id;
    private String sourceDataUrl;
    private String tokenDataUrl;
}