import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {

    public static String hashBlock(String blockJson) {
        return DigestUtils.sha256Hex(blockJson);
    }
}
