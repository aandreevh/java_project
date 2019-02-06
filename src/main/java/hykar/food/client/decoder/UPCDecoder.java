package hykar.food.client.decoder;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

/**
 * Used to decode upc from a image
 */
public class UPCDecoder {


    /**
     * Decodes image upc
     *
     * @param image to decode
     * @return upc or empty if not found
     */
    public Optional<String> decodeImageUPC(String image) {
        try {
            InputStream stream = new FileInputStream(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(stream))));
            if (bitmap.getWidth() < bitmap.getHeight()) {
                if (bitmap.isRotateSupported()) {
                    bitmap = bitmap.rotateCounterClockwise();
                }
            }
            return decode(bitmap);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<String> decode(BinaryBitmap bitmap) {

        Map<DecodeHintType, Object> tmpHintsMap = new EnumMap<>(
                DecodeHintType.class);
        tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        tmpHintsMap.put(DecodeHintType.POSSIBLE_FORMATS,
                EnumSet.allOf(BarcodeFormat.class));
        tmpHintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap, tmpHintsMap);
            return Optional.of(result.getText());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
