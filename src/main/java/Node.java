import java.util.InputMismatchException;
import java.util.Scanner;

public class Node {
    private final static Scanner scanner = new Scanner(System.in);
    private final Wallet wallet = new Wallet();
    private Blockchain blockchain;

    public void run(){
        if (walletMenu()) {
            mainMenu();
        }
    }

    private String getRecipient(){
        System.out.print("Enter the recipient of the transaction: ");
        return scanner.nextLine();
    }

    private float getTxAmount(){
        float txAmount = 0f;
        boolean isTxAmountValid = false;
        do{
            try{
                System.out.print("Your transaction amount please: ");
                txAmount = scanner.nextFloat();scanner.nextLine();
                isTxAmountValid = true;
            }catch(InputMismatchException e){
                System.out.println("Error! Invalid number!");
                scanner.nextLine();
            }
        }while(!isTxAmountValid);

        return txAmount;
    }

    public void printBlockchainElements(){
        for(Block block: blockchain.getChain()){
            System.out.println("Outputting block");
            System.out.println(block);
        }
    }
    public boolean walletMenu(){
        while(true){
            System.out.println("Please choose");
            System.out.println("1: Create wallet");
            System.out.println("2: Load wallet");
            System.out.println("q: Quit");

            String getChoice = scanner.nextLine();

            switch(getChoice.toLowerCase()){
                case "1":
                    if(wallet.createWallet()){
                        System.out.println("Wallet created. Your address: " + wallet.getAddress());
                    }else{
                        System.out.println("Error! Wallet creation failed.");
                        return false;
                    }

                    wallet.saveWallet();
                    System.out.println("Saving wallet successful");
                    blockchain = new Blockchain(wallet.getAddress());
                    return true;
                case "2":
                    wallet.loadWallet();
                    blockchain = new Blockchain(wallet.getAddress());
                    System.out.println("Loading wallet successful. Your address: " + wallet.getAddress());
                    return true;
                case "q":
                    return false;
            }
        }
    }

    public void mainMenu(){
        boolean run = true;
        while(run) {
            System.out.println("Please choose");
            System.out.println("1: Add a new transaction value");
            System.out.println("2: Mine new block");
            System.out.println("3: Output the blockchain blocks");
            System.out.println("4: Balance");
            System.out.println("q: Quit");

            String getChoice = scanner.nextLine();

            switch (getChoice.toLowerCase()) {
                case "1":
                    float txAmount=getTxAmount();
                    String recipient= getRecipient();
                    String signature = wallet.signTransaction(wallet.getAddress(),recipient,txAmount);
                    if(blockchain.addTransaction(wallet,recipient,txAmount, signature)){
                        System.out.println("Added transaction");
                    }else{
                        System.out.println("Transaction failed");
                    }
                    System.out.println(blockchain.getOpenTransactions());
                    break;
                case "2":
                    blockchain.mineBlock();
                    break;
                case "3":
                    printBlockchainElements();
                    break;
                case "4":
                    System.out.println(blockchain.getBalance());
                    break;
                case "q":
                    run = false;
            }
            if(!Verification.verifyChain(blockchain.getChain())){
                printBlockchainElements();
                System.out.println("Invalid blockchain");
                return;
            }
            System.out.println("Balance of " + wallet.getAddress() + ": " + blockchain.getBalance());
        }
        System.out.println("User left");
    }

}
