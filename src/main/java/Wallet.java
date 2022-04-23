import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.crypto.*;
import org.hyperledger.besu.datatypes.Address;
import static org.hyperledger.besu.crypto.Hash.keccak256;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;


public class Wallet{
    private static final SECP256K1 secp256K1 = new SECP256K1();
    private transient KeyPair keyPair;

    public boolean createWallet(){
        keyPair = secp256K1.generateKeyPair();
        return true;
    }

    public SECPPublicKey getPublicKey(){
        return keyPair.getPublicKey();
    }

    public String getAddress(){
        Address address = Address.extract(getPublicKey());
        return address.toString();
    }

    public void saveWallet() {
        KeyPairUtil.storeKeyFile(keyPair, Paths.get("./"));
    }

    public void loadWallet(){
        File file = new File("key");
        keyPair = KeyPairUtil.load(file);
    }

    public String signTransaction(String sender, String recipient, float amount){
        String signContext = sender + recipient + amount;
        Bytes signData = Bytes.wrap(signContext.getBytes(StandardCharsets.UTF_8));
        Bytes32 signHash = keccak256(signData);
        SECPSignature signature = secp256K1.sign(signHash,keyPair);
        return signature.encodedBytes().toHexString();
    }

    public static boolean verifyTransaction(Transaction transaction){
        String signContext = transaction.getSender() + transaction.getRecipient() + transaction.getAmount();
        Bytes signData = Bytes.wrap(signContext.getBytes(StandardCharsets.UTF_8));
        Bytes32 signHash = keccak256(signData);

        Bytes signBytes = Bytes.fromHexString(transaction.getSignature());

        SECPSignature signature =secp256K1.decodeSignature(signBytes);
        SECPPublicKey pubKey = secp256K1.recoverPublicKeyFromSignature(signHash,signature).get();
        return secp256K1.verify(signData,signature,pubKey,Hash::keccak256);
    }


}
