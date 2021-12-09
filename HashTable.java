import java.security.GeneralSecurityException;

public class HashTable<T> {
    // 
    private NGen<T> table[]; // the hash table. An array of linked lists basically.
    private int chainlength[]; // array same length as hash table that stores length of linked list at each index.
    private String type = "general"; // initialized to general. But can also take on "special" value.

    // Constants 
    private final int GENERAL_DEFAULT = 100;
    private final int SPECIFIC_DEFAULT = 300;


    public HashTable(String type) {

        if (type == "general") {
            table = (NGen<T>[]) new Object[GENERAL_DEFAULT];
            chainlength = new int[GENERAL_DEFAULT];
            for (int i = 0; i < chainlength.length; ++i) 
                chainlength[i] = 0;
        } else if (type == "specific") {
            table = (NGen<T>[]) new Object[SPECIFIC_DEFAULT];
            chainlength = new int[SPECIFIC_DEFAULT];
            for (int i = 0; i < chainlength.length; ++i) 
                chainlength[i] = 0;
        }
    }
    
    // Three custom hash functions to implement hashing. They input a key, and output an index for the table[]
    // member variable in this class. 
    public int h1(T key) {}
    public int h2(T key) {}
    public int h3(T key) {} 

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
                index = h2(item);
                break;
            case 3:
                index = h3(item);
                break;
            default:
                index = -1;
                break;
        }

        if (index == -1) 
            throw new RuntimeException("switch statement in add() method failed for some reason");

        // once we've determined the index, the index can either have 0 elements in the linked list or more than 0.
        // if 0 (so table[index] == null), then we add the first element to this index. Each index houses a dummy head
        // for a linked list (the chain of hashes mapped to that index)
        if (table[index].getNext() == null) {
            table[index].setNext(new NGen<T>(item, null));
            chainlength[index]++; // increment chainlength because we added a new element.
        }
        else { // if there are elements already present in the linked list at the desired index.
            NGen<T> temp1 = new NGen<T>(item, null);
            NGen<T> temp2 = table[index].getNext();
            table[index].setNext(temp1);
            temp1.setNext(temp2);
            chainlength[index]++; // increment chainlength because we added a new element.
        }
    } 

    // display the entire hash table with the index and number of collisions at each index.
    public void display() {}

    public static void main(String[] args) {

        return;
    }
}
