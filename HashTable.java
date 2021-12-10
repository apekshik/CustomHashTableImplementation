import java.security.GeneralSecurityException;
import java.util.Scanner;
import java.io.*;

public class HashTable<T> {
    // 
    private NGen<T> table[]; // the hash table. An array of linked lists basically.
    private int chainLength[]; // array same length as hash table that stores length of linked list at each index.
    private String type = "general"; // initialized to general. But can also take on "special" value.
    private int nUniqueTokens = 0;
    private int maxChain = 0;
    private int emptyIndices = 100;

    // Constants 
    private final int GENERAL_DEFAULT = 100;
    private final int SPECIFIC_DEFAULT = 300;

    public HashTable() {
        table = new NGen[GENERAL_DEFAULT];
        chainLength = new int[GENERAL_DEFAULT];
        emptyIndices = GENERAL_DEFAULT;
        for (int i = 0; i < chainLength.length; ++i) 
            chainLength[i] = 0;
    }

    public HashTable(String type) {

        if (type == "general") {
            table = new NGen[GENERAL_DEFAULT];
            chainLength = new int[GENERAL_DEFAULT];
            emptyIndices = GENERAL_DEFAULT;
            for (int i = 0; i < chainLength.length; ++i) 
                chainLength[i] = 0;
        } else if (type == "specific") {
            table = new NGen[SPECIFIC_DEFAULT];
            chainLength = new int[SPECIFIC_DEFAULT];
            emptyIndices = SPECIFIC_DEFAULT;
            for (int i = 0; i < chainLength.length; ++i) 
                chainLength[i] = 0;
        }
    }
    
    // Three custom hash functions to implement hashing. They input a key, and output an index for the table[]
    // member variable in this class. 
    // really simple hashing function that maps the words to only twenty six indices. Very bad hashing algorithm.
    public int h1(T key) { 
        String s = key.toString();
        return (Character.toLowerCase(s.charAt(0)) - 'a');
    }
    // This hash simply sums the ASCII values characters and mods 100 to index it into the general hash table. 
    public int h2(T key, int tableSize) {
        String s = key.toString();
        int sum = 0;
        for (int i = 0; i < s.length(); ++i) 
            sum += (int) s.charAt(i);
        return (sum % tableSize);
    }
    // This hash is a modification of the h2() in that there's a salt value (a random string) that we add to the end 
    // of every token and then compute the sum. 
    public int h3(T key, int tableSize) {
        String salt = "&jAJJasvdD&&@)@()@#$";
        String s = key.toString();
        int sum = 0, saltSum = 0;
        for (int i = 0; i < salt.length(); ++i) 
            saltSum += (int) salt.charAt(i);
        
        for (int i = 0; i < s.length(); ++i) 
            sum += (int) s.charAt(i);

        return (sum + saltSum) % tableSize;
    } 

    // Adding an item to the hash table.
    // Along with providing the item to add, we also mention which hashing algorithm to use to compute the index  
    // at which we store the element.  
    public void add(T item, int hashAlg) {
        int index;
        // we compute index based on the hashAlg we want. We achieve this using switch statements. 
        switch(hashAlg) {
            case 1:
                index = h1(item);
                break;
            case 2: 
                index = h2(item, table.length);
                break;
            case 3:
                index = h3(item, table.length);
                break;
            default:
                index = -1;
                break;
        }

        if (index == -1) 
            throw new RuntimeException("switch statement in add() method failed for some reason");

        // once we've determined the index, the index can either have 0 elements in the linked list or more than 0.
        // if 0 (so table[index] == null), then we add the first element to this index. Non-headed linked lists used. 
        if (table[index] == null) {
            table[index] = new NGen<T>(item, null);
            chainLength[index]++; // increment chainLength because we added a new element.
            emptyIndices--; // since we filled an index with no elements, we decrement emptyIndices by 1. 
            if (maxChain == 0) 
                maxChain = 1; 
            nUniqueTokens++;
        }
        else { // if there are elements already present in the linked list at the desired index.
            // first we iterate through the linked list at desired index and check to see if item 
            // already exists in the bucket.
            NGen<T> tail = table[index];
            while (tail != null) {
                if (tail.getData().toString() == item.toString()) 
                    return; // don't add element since item already exists in the bucket. 
                tail = tail.getNext();
            }

            // if it doesn't exist, then we can add it to the linked list at the given index.
            NGen<T> temp1 = new NGen<T>(item, null);
            NGen<T> temp2 = table[index];
            table[index] = temp1;
            temp1.setNext(temp2);
            chainLength[index]++; // increment chainLength because we added a new element.
            maxChain = Math.max(maxChain, chainLength[index]); // update longest chain length member variable.
            nUniqueTokens++; // update number of unique tokens. 
        }
    } 


    // display the entire hash table with the index and number of collisions at each index.
    public void display() {
        for (int i = 0; i < table.length; ++i) {
            System.out.println(i + ": " + chainLength[i]);
        }

        int avg = -1;
        System.out.println("longest chain : " + maxChain);
        System.out.println("unique tokens : " + nUniqueTokens);
        System.out.println("empty-indices : " + emptyIndices);
        if (type == "general") {
            System.out.println("non-empty-indices : " + (GENERAL_DEFAULT - emptyIndices));
            avg = nUniqueTokens / (GENERAL_DEFAULT - emptyIndices);
        }
        else if (type == "specific") {
            System.out.println("non-empty-indices : " + (SPECIFIC_DEFAULT - emptyIndices));
            avg = nUniqueTokens / (GENERAL_DEFAULT - emptyIndices);
        }
        
        if (avg == -1) throw new RuntimeException("type entered was obviously bogus");
        
        System.out.println("average collision length : " + avg);
    }


    public static String[] fileToArray(String filePath) {
        String ar[] = new String[100];
        Scanner readFile = null;
        String s;
        int count = 0;

        System.out.println();
        System.out.println("Attempting to read from file: " + filePath);
        try {
            readFile = new Scanner(new File(filePath));
        }
        catch (FileNotFoundException e) {
            System.out.println("File: " + filePath + " not found");
            System.exit(1);  
        }

        System.out.println("Connection to file: " + filePath + " successful");
        System.out.println("------------------------------------------------------");
        while (readFile.hasNext()) {
            s = readFile.next();
            // System.out.println("Token found: " + s);
            ar[count] = s;
            count++;
            // resizing ar if count exceeds the index range for ar 
            if (count >= ar.length) {
                String[] temp = ar;
                ar = new String[ar.length * 2];
                System.arraycopy(temp, 0, ar, 0, temp.length);
            }
        }

        // we finally resize ar to the size that count defines. 
        String[] temp = ar;
        ar = new String[count];
        System.arraycopy(temp, 0, ar, 0, ar.length);
        return ar;
    }

    public static void main(String[] args) {
        String tokens1[], tokens2[];
        tokens1 = HashTable.fileToArray("gettysburg.txt");
        tokens2 = HashTable.fileToArray("keywords.txt");
        
        HashTable<String> gen1 = new HashTable<>(); // for h1()
        HashTable<String> gen2 = new HashTable<>(); // for h2()
        HashTable<String> gen3 = new HashTable<>(); // for h3()
        HashTable<String> spc = new HashTable<>("specific");
        for (int i = 0; i < tokens1.length; ++i) 
            gen1.add(tokens1[i], 1); // hash with h1() 
        for (int i = 0; i < tokens1.length; ++i) 
            gen2.add(tokens1[i], 2); // hash with h2()
        for (int i = 0; i < tokens1.length; ++i) 
            gen3.add(tokens1[i], 3); // hash with h3()
        for (int i = 0; i < tokens2.length; ++i)
            spc.add(tokens2[i], 3); // hash with h3()
        // gen1.display();
        // gen2.display();
        // gen3.display();
        spc.display();
        return;
    }
}
