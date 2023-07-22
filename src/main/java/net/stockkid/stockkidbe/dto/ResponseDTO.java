package net.stockkid.stockkidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDTO {

    private ResponseStatus apiStatus;
    private String apiMsg;
    private Object apiObj;
}
