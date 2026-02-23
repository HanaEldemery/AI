package battle;

public class Node {

	public int value;
    public int[] maxHealth;
    public int[] maxDamage;
    public int[] minHealth;
    public int[] minDamage;
    public Node parent;
    public String action; 
    public Node bestChild; 

    public Node(int[] maxHealth, int[] maxDamage, int[] minHealth, int[] minDamage, Node parent, String action) {
        this.maxHealth = maxHealth;
        this.maxDamage = maxDamage;
        this.minHealth = minHealth;
        this.minDamage = minDamage;
        this.parent = parent;
        this.action = action;
        this.bestChild = null;
	}

    public int getValue() {
        return value;
    }
}