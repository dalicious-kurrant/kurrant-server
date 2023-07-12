package co.kurrant.app.admin_api.util;

import co.kurrant.app.admin_api.dto.Code;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryCodeUtil {
    private final ResourceLoader resourceLoader;
    public List<Code> getEntireDeliveryCodes() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:accessCode/accessCode.json");
        File file = resource.getFile();
        return Arrays.asList(objectMapper.readValue(file, Code[].class));
    }
}
