import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Block {
    private int index;
    private String previousHash;
    private ArrayList<Transaction> transactions;
    private String time;
    private int proof;

    public Block(int index, String previousHash, ArrayList<Transaction> transactions,int proof){
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.proof = proof;
        this.time = LocalDateTime.now().toString();
    }
    public int getIndex(){
        return index;
    }

    public String getPreviousHash(){
        return previousHash;
    }

    public int getProof(){
        return proof;
    }

    public ArrayList<Transaction> getTransactions(){
        return  transactions;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
