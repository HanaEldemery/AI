package battle;

import java.util.ArrayList;

public class BattleSolver {

    public Node initialNode;
    private int nodesExpanded;

    public String solve(String initialStateString, boolean ab, boolean visualize) {
        parseInitialState(initialStateString);
        nodesExpanded = 0;
        int score;

        //minimax w/ ab pruning
        if (ab) { 
            score = minimaxAB(initialNode, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } 

        //minimax w/o ab pruning
        else { 
            score = minimax(initialNode, true);
        }

        String plan = reconstructPlan(initialNode);
        return plan + ";" + score + ";" + nodesExpanded + ";";
    }

    private void parseInitialState(String initialStateString) {
        String[] parts = initialStateString.split(";");

        //team A
        String[] teamAStr = parts[0].split(",");
        int numUnitsA = teamAStr.length / 2;
        int[] healthA = new int[numUnitsA];
        int[] damageA = new int[numUnitsA];

        for (int i = 0; i < numUnitsA; i++) {
            healthA[i] = Integer.parseInt(teamAStr[i * 2]);
            damageA[i] = Integer.parseInt(teamAStr[i * 2 + 1]);
        }

        //team B
        String[] teamBStr = parts[1].split(",");
        int numUnitsB = teamBStr.length / 2;
        int[] healthB = new int[numUnitsB];
        int[] damageB = new int[numUnitsB];

        for (int i = 0; i < numUnitsB; i++) {
            healthB[i] = Integer.parseInt(teamBStr[i * 2]);
            damageB[i] = Integer.parseInt(teamBStr[i * 2 + 1]);
        }

        initialNode = new Node(healthA, damageA, healthB, damageB, null, null);
    }

    private int minimax(Node node, boolean isMaxTurn) {
        nodesExpanded++;

        //if terminal state -> return score from max's perspective
        if (isTerminal(node)) {
            return calculateUtility(node);
        }

        //max's turn
        if (isMaxTurn) {
            int maxValue = Integer.MIN_VALUE;
            Node bestChild = null;
            ArrayList<Node> children = generateChildren(node, true);

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);   
                //recursion on children (DFS)
                int value = minimax(child, false);

                if (value > maxValue) {
                    maxValue = value;
                    bestChild = child;
                }
            }

            //store best child in node
            if (bestChild != null) {
                node.bestChild = bestChild;
            }

            return maxValue;
        } 

        //min's turn
        else {
            int minValue = Integer.MAX_VALUE;
            Node bestChild = null;
            ArrayList<Node> children = generateChildren(node, false);

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);         
                int value = minimax(child, true);     

                if (value < minValue) {
                	minValue = value;
                    bestChild = child;
                }
            }

            if (bestChild != null) {
                node.bestChild = bestChild;
            }

            return minValue;
        }
    }

    private int minimaxAB(Node node, boolean isMaxTurn, int alpha, int beta) {
        nodesExpanded++;

        if (isTerminal(node)) {
            return calculateUtility(node);
        }

        if (isMaxTurn) {
            int maxValue = Integer.MIN_VALUE;
            Node bestChild = null;
            ArrayList<Node> children = generateChildren(node, true);      

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);   
                int value = minimaxAB(child, false, alpha, beta);

                if (value > maxValue) {
                    maxValue = value;
                    bestChild = child;
                }

                alpha = Math.max(alpha, value);

                if (beta <= alpha) {
                	//prune
                    break; 
                }
            }

            if (bestChild != null) {
                node.bestChild = bestChild;
            }

            return maxValue;
        } 

        else {
            int minValue = Integer.MAX_VALUE;
            Node bestChild = null;       
            ArrayList<Node> children = generateChildren(node, false);

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);   
                int value = minimaxAB(child, true, alpha, beta);

                if (value < minValue) {
                    minValue = value;
                    bestChild = child;
                }

                beta = Math.min(beta, value);

                if (beta <= alpha) {
                    break;
                }
            }

            if (bestChild != null) {
                node.bestChild = bestChild;
            }

            return minValue;
        }
    }

    private ArrayList<Node> generateChildren(Node node, boolean isMaxTurn) {
        ArrayList<Node> children = new ArrayList<>();

        //max's turn
        if (isMaxTurn) {

            for (int i = 0; i < node.maxHealth.length; i++) {

            	//if max unit is not dead 
                if (node.maxHealth[i] > 0) {

                    for (int j = 0; j < node.minHealth.length; j++) {

                    	//if min unit is not dead
                        if (node.minHealth[j] > 0) {
                            Node child = createChildState(node, i, j, true);
                            children.add(child);
                        }
                    }
                }
            }
        } 

        else {

            for (int i = 0; i < node.minHealth.length; i++) {

                if (node.minHealth[i] > 0) {

                    for (int j = 0; j < node.maxHealth.length; j++) {

                        if (node.maxHealth[j] > 0) {
                            Node child = createChildState(node, i, j, false);
                            children.add(child);
                        }
                    }
                }
            }
        }

        return children;
    }

    private Node createChildState(Node parent, int attackerIdx, int targetIdx, boolean maxAttacks) {
        int[] newMaxHealth = parent.maxHealth.clone();
        int[] newMinHealth = parent.minHealth.clone();
        String action;

        if (maxAttacks) {
            int damage = parent.maxDamage[attackerIdx];
            newMinHealth[targetIdx] = Math.max(0, newMinHealth[targetIdx] - damage);
            action = "A" + "(" + attackerIdx + "," + targetIdx + ")";
        } 

        else {
            int damage = parent.minDamage[attackerIdx];
            newMaxHealth[targetIdx] = Math.max(0, newMaxHealth[targetIdx] - damage);
            action = "B" + "(" + attackerIdx + "," + targetIdx + ")";
        }

        return new Node(newMaxHealth, parent.maxDamage.clone(), newMinHealth, parent.minDamage.clone(), parent, action);
    }

    private boolean isTerminal(Node node) {
        int maxTotal = 0;

        for (int i=0;i<node.maxHealth.length;i++) {
        	maxTotal+=node.maxHealth[i];
        }

        int minTotal = 0;

        for (int i=0;i<node.minHealth.length;i++) {
        	minTotal+=node.minHealth[i];
        }

        if (maxTotal == 0 || minTotal == 0) {
        	return true;
        }

        return false;
    }

    private int calculateUtility(Node node) {

    	int maxTotal = 0;

        for (int i=0;i<node.maxHealth.length;i++) {
        	maxTotal+=node.maxHealth[i];
        }

        int minTotal = 0;

        for (int i=0;i<node.minHealth.length;i++) {
        	minTotal+=node.minHealth[i];
        }

        //max wins
        if (maxTotal > 0) { 
            return maxTotal; 
        } 

        //max loses
        else {
            return -minTotal;
        }
    }

    private String reconstructPlan(Node root) {
        StringBuilder plan = new StringBuilder();
        Node current = root;

        while (current.bestChild != null) {

            if (plan.length() > 0) {
                plan.append(",");
            }

            plan.append(current.bestChild.action);
            current = current.bestChild;
        }

        return plan.toString();
    }

    public static void main(String[] args) {
    	BattleSolver obj = new BattleSolver();
        obj.solve("3,1,4,2;3,5;A;", false, true);
    }
}