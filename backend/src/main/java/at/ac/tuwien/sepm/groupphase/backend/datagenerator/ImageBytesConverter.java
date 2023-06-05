package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Profile("generateData")
@Component
public class ImageBytesConverter {

    public static byte[] getImageBytes(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream stream = url.openStream()) {
            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = stream.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                output.write(buffer, 0, bytesRead);
            }
        }
        return output.toByteArray();
    }
}
