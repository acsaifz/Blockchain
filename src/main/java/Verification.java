import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;

public class Verification {
    public static boolean validProof(ArrayList<Transaction> transactions, String lastHash, int proof){
        String guess = transactions.toString() + lastHash + proof;
        String guess_hash = DigestUtils.sha256Hex(guess);
        return guess_hash.startsWith("00");
    }

    public static boolean verifyTransaction(Transaction transaction, float senderBalance){
        return transaction.getAmount() <= senderBalance;
    }

    public static boolean verifyChain(ArrayList<Block> chain){
        for(Block block: chain){
            if (block.getIndex() == 0){
                continue;
            }
            if (!block.getPreviousHash().equals(HashUtil.hashBlock(chain.get(block.getIndex()-1).toString()))){
                return false;
            }
            ArrayList<Transaction> transactionsWithoutMiningReward = new ArrayList<>(block.getTransactions());
            transactionsWithoutMiningReward.remove(transactionsWithoutMiningReward.size()-1);
            if (!validProof(transactionsWithoutMiningReward,block.getPreviousHash(),block.getProof())){
                System.out.println("Proof of work invalid");
                return false;
            }
        }
        return true;
    }
}
