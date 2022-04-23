import com.google.gson.Gson;
import java.time.LocalDateTime;
//import java.time.ZonedDateTime;

public class Transaction {
    private String sender;
    private String recipient;
    private float amount;
    private String signature;
    private String timestamp;


    public Transaction(String sender, String recipient, float amount, String signature){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.signature = signature;
        //this.timestamp = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getSender(){
        return sender;
    }

    public String getRecipient(){
        return recipient;
    }

    public float getAmount(){
        return amount;
    }

    public String getSignature(){
        return signature;
    }

    public String toString(){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }
}
