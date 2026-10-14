package notkirill.workshopmanager.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ImageFormatTest {
    private static byte[] ftyp(String majorBrand, String... compatibleBrands) {
        int size = 16 + 4 * compatibleBrands.length;
        StringBuilder box = new StringBuilder("ftyp").append(majorBrand).append("\0\0\0\0");
        for (String brand : compatibleBrands) {
            box.append(brand);
        }
        byte[] body = box.toString().getBytes(StandardCharsets.US_ASCII);
        byte[] result = new byte[size];
        result[3] = (byte) size;
        System.arraycopy(body, 0, result, 4, body.length);
        return result;
    }

    @Test
    void detectsFormatsFromTheirFirstBytes() {
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0};
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        byte[] gif = "GIF89a....".getBytes(StandardCharsets.US_ASCII);
        byte[] webp = "RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.US_ASCII);

        assertThat(ImageFormat.detect(png)).contains(ImageFormat.PNG);
        assertThat(ImageFormat.detect(jpeg)).contains(ImageFormat.JPEG);
        assertThat(ImageFormat.detect(gif)).contains(ImageFormat.GIF);
        assertThat(ImageFormat.detect(webp)).contains(ImageFormat.WEBP);
    }

    @ParameterizedTest
    @CsvSource({
            "PNG,  png,  image/png",
            "JPEG, jpg,  image/jpeg",
            "GIF,  gif,  image/gif",
            "WEBP, webp, image/webp",
            "AVIF, avif, image/avif",
    })
    void formatsKeepTheirExtensionAndMimeType(ImageFormat format, String extension, String mimeType) {
        assertThat(format.extension()).isEqualTo(extension);
        assertThat(format.mimeType()).isEqualTo(mimeType);
    }

    @Test
    void detectsFromTheStartOfAWholeFile() throws IOException {
        byte[] file = Arrays.copyOf(new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'}, 1024 * 1024);
        ByteArrayInputStream stream = new ByteArrayInputStream(file);

        Optional<ImageFormat> fromStream = ImageFormat.detect(stream);
        Optional<ImageFormat> fromBytes = ImageFormat.detect(file);

        assertThat(fromStream).contains(ImageFormat.PNG);
        assertThat(fromBytes).contains(ImageFormat.PNG);
        assertThat(stream.available()).isEqualTo(file.length - 64);
    }

    @Test
    void detectsAvifListedOnlyAmongCompatibleBrands() {
        byte[] avif = ftyp("mif1", "miaf", "avif");

        Optional<ImageFormat> format = ImageFormat.detect(avif);

        assertThat(format).contains(ImageFormat.AVIF);
    }

    @Test
    void rejectsOtherFiles() {
        byte[] svg = "<svg xmlns=\"http://www.w3.org/2000/svg\"><script/></svg>".getBytes(StandardCharsets.UTF_8);
        byte[] heic = ftyp("heic", "mif1", "heic");
        byte[] zip = {'P', 'K', 3, 4};

        assertThat(ImageFormat.detect(svg)).isEmpty();
        assertThat(ImageFormat.detect(heic)).isEmpty();
        assertThat(ImageFormat.detect(zip)).isEmpty();
        assertThat(ImageFormat.detect(new byte[0])).isEmpty();
    }

    @Test
    void findsFormatByExtension() {
        assertThat(ImageFormat.fromFileName("shot.PNG")).contains(ImageFormat.PNG);
        assertThat(ImageFormat.fromFileName("shot.svg")).isEmpty();
        assertThat(ImageFormat.fromFileName("png")).isEmpty();
    }
}
