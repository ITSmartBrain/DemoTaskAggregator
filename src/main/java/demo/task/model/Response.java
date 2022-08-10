package demo.task.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Response {
    private int id;
    private String urlType;
    private String videoUrl;
    private String value;
    private int ttl;
}
