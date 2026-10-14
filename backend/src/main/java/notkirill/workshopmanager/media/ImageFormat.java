package notkirill.workshopmanager.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum ImageFormat {
    PNG("png", "image/png"),
    JPEG("jpg", "image/jpeg"),
    GIF("gif", "image/gif"),
    WEBP("webp", "image/webp"),
    AVIF("avif", "image/avif");

    private static final int HEADER_SIZE = 64;
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};
    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    private final String extension;
    private final String mimeType;

    ImageFormat(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static Optional<ImageFormat> detect(InputStream in) throws IOException {
        return detect(in.readNBytes(HEADER_SIZE));
    }

    public static Optional<ImageFormat> detect(byte[] data) {
        byte[] header = data.length > HEADER_SIZE ? Arrays.copyOf(data, HEADER_SIZE) : data;
        if (startsWith(header, PNG_SIGNATURE)) {
            return Optional.of(PNG);
        }
        if (startsWith(header, JPEG_SIGNATURE)) {
            return Optional.of(JPEG);
        }
        if (ascii(header, 0, 6).equals("GIF87a") || ascii(header, 0, 6).equals("GIF89a")) {
            return Optional.of(GIF);
        }
        if (ascii(header, 0, 4).equals("RIFF") && ascii(header, 8, 4).equals("WEBP")) {
            return Optional.of(WEBP);
        }
        if (isAvif(header)) {
            return Optional.of(AVIF);
        }
        return Optional.empty();
    }

    public static Optional<ImageFormat> fromFileName(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return Optional.empty();
        }
        String extension = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).filter(format -> format.extension.equals(extension)).findFirst();
    }

    private static boolean isAvif(byte[] header) {
        if (!ascii(header, 4, 4).equals("ftyp")) {
            return false;
        }
        int boxSize = Math.min(
                header.length, ((header[0] & 0xFF) << 24) | ((header[1] & 0xFF) << 16)
                        | ((header[2] & 0xFF) << 8) | (header[3] & 0xFF)
        );
        for (int offset = 8; offset + 4 <= boxSize; offset += 4) {
            if (offset == 12) {
                continue;
            }
            String brand = ascii(header, offset, 4);
            if (brand.equals("avif") || brand.equals("avis")) {
                return true;
            }
        }
        return false;
    }

    private static boolean startsWith(byte[] header, byte[] signature) {
        return header.length >= signature.length
                && Arrays.equals(header, 0, signature.length, signature, 0, signature.length);
    }

    private static String ascii(byte[] header, int offset, int length) {
        if (header.length < offset + length) {
            return "";
        }
        return new String(header, offset, length, StandardCharsets.US_ASCII);
    }

    public String extension() {
        return extension;
    }

    public String mimeType() {
        return mimeType;
    }
}
