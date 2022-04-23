import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.tuweni.bytes.Bytes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;


public class Blockchain {
    private final static int MINING_REWARD = 10;

    private ArrayList<Block> chain = new ArrayList<>();
    private ArrayList<Transaction> openTransactions = new ArrayList<>();
    private String hostingNode;

    public Blockchain(String nodeId){
        hostingNode = nodeId;
        Block genesisBlock = new Block(0,"",new ArrayList<Transaction>(),100);
        chain.add(genesisBlock);
        loadData();
    }

    public float getBalance(){
        float amountSent = 0;
        float amountReceived = 0;

        for (Block block: chain){
            if (block.getTransactions() == null){
                continue;
            }
            for (Transaction transaction: block.getTransactions()){
                if (transaction.getSender().equals(hostingNode)){
                    amountSent += transaction.getAmount();
                }
                if (transaction.getRecipient().equals(hostingNode)){
                    amountReceived += transaction.getAmount();
                }
            }
        }

        for(Transaction transaction: openTransactions){
            if (transaction.getSender().equals(hostingNode)){
                amountSent += transaction.getAmount();
            }
        }
        return amountReceived - amountSent;
    }

    public ArrayList<Block> getChain(){
        return chain;
    }

    public ArrayList<Transaction> getOpenTransactions(){
        return openTransactions;
    }


    public boolean addTransaction(Wallet wallet, String recipient, float amount, String signature){
        Transaction transaction = new Transaction(wallet.getAddress(),recipient,amount, signature);
        if (Verification.verifyTransaction(transaction, this.getBalance()) && Wallet.verifyTransaction(transaction)){
            openTransactions.add(transaction);
            saveData();
            return true;
        }
        return false;
    }

    public Block getLastBlock(){
        if (chain.size() < 1){
            return null;
        }else{
            return chain.get(chain.size()-1);
        }
    }

    public void mineBlock(){
        int proof = proofOfWork();
        Transaction rewardTransaction = new Transaction("MINING",hostingNode,MINING_REWARD,"");
        openTransactions.add(rewardTransaction);
        Block block = new Block(chain.size(), HashUtil.hashBlock(getLastBlock().toString()),openTransactions,proof);
        chain.add(block);
        openTransactions = new ArrayList<>();
        saveData();
        System.out.println("Block found!");
    }

    public int proofOfWork(){
        int proof = 0;
        Block lastBlock = chain.get(chain.size()-1);
        String lastHash = HashUtil.hashBlock(lastBlock.toString());
        while(!Verification.validProof(openTransactions,lastHash,proof)){
            proof++;
        }

        return proof;
    }

    public void loadData(){
        try{
            File file = new File("blockchain.dat");
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedReader reader = new BufferedReader( new FileReader(file));
            String chainJson = reader.readLine();
            String openTransactionsJson = reader.readLine();
            reader.close();

            Gson gson = new Gson();

            ArrayList<Block>loadedChain = gson.fromJson(chainJson, new TypeToken<ArrayList<Block>>() {}.getType());
            if (loadedChain != null){
                chain = loadedChain;
            }
            //System.out.println(chain);

            ArrayList<Transaction> loadedOpenTransactions = gson.fromJson(openTransactionsJson, new TypeToken<ArrayList<Transaction>>() {}.getType());
            if (loadedOpenTransactions != null){
                openTransactions = loadedOpenTransactions;
            }
            //System.out.println(openTransactions);
        }catch(IOException e){
            System.out.printf("An error occured");
            e.printStackTrace();
        }
    }

    public void saveData(){
        try{
            File file = new File("blockchain.dat");
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(chain.toString());
            fileWriter.write("\n");
            fileWriter.write(openTransactions.toString());
            fileWriter.close();
        }catch(IOException e){
            System.out.println("An error occured.");
            e.printStackTrace();
        }

    }

}
