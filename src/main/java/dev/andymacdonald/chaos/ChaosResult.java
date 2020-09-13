package dev.andymacdonald.chaos;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ChaosResult
{
    private final int chaosStatusCode;

    private final ResponseEntity<byte[]> chaosResponseEntity;

    private final Long delayedBy;
}
