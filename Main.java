import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
 
 
public class Main {
    public static void main(String[] args) {
        FileReader input;
        try {
            input = new FileReader("input.txt");
            Scanner scanner = new Scanner(input);
            String type = "";
            ArrayList<State> states = new ArrayList<>();
            ArrayList<String> alphabet = new ArrayList<>();
            int indInitial = -1;
            ArrayList<Integer> acceptingStates = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                String[] command = scanner.nextLine().split("=");
                command[1] = command[1].substring(1, command[1].length() - 1);
                if (i == 0) {
                    if (command[0].equals("type")) {
                        type = command[1];
                    } else {
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                } else if (i == 1) {
                    if (command[0].equals("states")) {
                        if(command[1].isEmpty()){
                            System.out.println("E1: Input file is malformed");
                            return;
                        }
                        String[] statesNames = command[1].split(",");
                        for (String name : statesNames) {
                            boolean isPresent = false;
                            for (State state:states) {
                                if(state.name.equals(name)){
                                    isPresent = true;
                                }
                            }
                            if(!isPresent)
                                states.add(new State(name));
                        }
                    } else {
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                } else if (i == 2) {
                    if (command[0].equals("alphabet")) {
                        if(command[1].isEmpty()){
                            System.out.println("E1: Input file is malformed");
                            return;
                        }
                        if(command[1].endsWith(",")){
                            System.out.println("E1: Input file is malformed");
                            return;
                        }
                        String[] transitions = command[1].split(",");
                        alphabet.addAll(Arrays.asList(transitions));
                    } else {
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                } else if (i == 3) {
                    if (command[0].equals("initial")) {
                        if (command[1].isEmpty()) {
                            System.out.println("E2: Initial state is not defined");
                            return;
                        } else {
                            for (int j = 0; j < states.size(); ++j) {
                                if (states.get(j).name.equals(command[1])) {
                                    indInitial = j;
                                }
                            }
                            if (indInitial == -1){
                                System.out.println("E4: A state '" + command[1] + "' is not in the set of states");
                                return;
                            }
                        }
                    } else {
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                } else if (i == 4) {
                    if (command[0].equals("accepting")) {
                        if (command[1].isEmpty()) {
                            System.out.println("E3: Set of accepting states is empty");
                            return;
                        } else {
                            String[] accepting = command[1].split(",");
                            Arrays.sort(accepting);
                            for (String acceptingName : accepting) {
                                boolean isAdded = false;
                                for (int j = 0; j < states.size(); ++j) {
                                    if (states.get(j).name.equals(acceptingName)) {
                                        if(!acceptingStates.contains(j)){
                                            acceptingStates.add(j);
                                        }
                                        isAdded = true;
                                    }
                                }
                                if (!isAdded) {
                                    System.out.println("E4: A state '" + acceptingName + "' is not in the set of states");
                                    return;
                                }
                            }
                        }
                    } else {
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                } else if (i == 5) {
                    if(command[0].equals("transitions")){
                        String[] fullTrans = command[1].split(",");
                        for (int j = 0; j < fullTrans.length-1; j++) {
                            for (int k = j+1; k < fullTrans.length; k++) {
                                if(fullTrans[j].equals(fullTrans[k])){
                                    System.out.println("E1: Input file is malformed");
                                    return;
                                }
                            }
                        }
                        for (String transToSplit: fullTrans) {
                            String stateFrom, stateTo, transition;
                            String[] splittedTrans = transToSplit.split(">");
                            stateFrom = splittedTrans[0];
                            stateTo = splittedTrans[2];
                            transition = splittedTrans[1];
                            if(transition.isEmpty()){
                                System.out.println("E1: Input file is malformed");
                                return;
                            }
                            int indFrom = -1, indTo = -1;
                            boolean flag1 = false;
                            boolean flag2 = false;
                            for (int j = 0; j < states.size(); ++j) {
                                if(states.get(j).name.equals(stateFrom)){
                                    flag1 = true;
                                    indFrom = j;
                                }
                                if (states.get(j).name.equals(stateTo)) {
                                    flag2 = true;
                                    indTo = j;
                                }
                            }
                            if (flag1){
                                if (flag2){
                                    if(alphabet.contains(transition)){
                                        states.get(indFrom).addTransTo(states.get(indTo), transition);
                                    }else{
                                        System.out.println("E5: A transition '"+ transition+"' is not represented in the alphabet ");
                                        return;
                                    }
                                }else {
                                    System.out.println("E4: A state '" + stateTo+"' is not in the set of states");
                                    return;
                                }
                            }else{
                                System.out.println("E4: A state '" + stateFrom+"' is not in the set of states");
                                return;
                            }
                        }
                    }else{
                        System.out.println("E1: Input file is malformed");
                        return;
                    }
                }
            }
 
            FSA fsa = new FSA(states, indInitial, acceptingStates);
 
            for (State state : states) {
                if(!fsa.bfs(state)){
                    System.out.println("E6: Some states are disjoint");
                    return;
                }
            }
 
            for (State state : states) {
                for (int i = 0; i < state.to.size()-1; ++i) {
                    for (int j = i+1; j < state.to.size(); j++) {
                        if(state.to.get(i).symbol.equals(state.to.get(j).symbol) && type.equals("deterministic")){
                            System.out.println("E7: FSA is non-deterministic");
                            return;
                        }
                    }
                }
            }
 
            fsa.RegExpTranslator();
 
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found!");
        }
    }
}
 
class FSA {
    ArrayList<State> states = new ArrayList<>();
    int indOfInitial;
    ArrayList<Integer> indOfAccepting;
 
    String[][][] R;
    String[][] initialR;
 
    public FSA(ArrayList<State> states, int indOfInitial, ArrayList<Integer> indOfAccepting) {
        this.states = states;
        this.indOfInitial = indOfInitial;
        this.indOfAccepting = indOfAccepting;
        R = new String[states.size()][states.size()][states.size()];
        initialR = new String[states.size()][states.size()];
    }
 
    public void RegExpTranslator() {
        initializeR();
        String answer = "";
        for (int ind : indOfAccepting) {
            answer += findRegular(indOfInitial, ind, states.size() - 1) + "|";
        }
        answer = answer.substring(0, answer.length() - 1);
        System.out.println(answer);
    }
 
    private String findRegular(int i, int j, int k) {
        if (k == -1) {
            return initialR[i][j];
        } else if (R[i][j][k] == null) {
            R[i][j][k] = "(" + findRegular(i, k, k - 1) + findRegular(k, k, k - 1) + "*" + findRegular(k, j, k - 1) + "|" + findRegular(i, j, k - 1) + ")";
        }
        return R[i][j][k];
    }
 
    private void initializeR() {
        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < states.size(); j++) {
                State iState = states.get(i);
                State jState = states.get(j);
                initialR[i][j] = "";
                for (Transition transition : iState.to) {
                    if (transition.to == jState) {
                        initialR[i][j] += transition.symbol + "|";
                    }
                }
                if (i == j) {
                    initialR[i][j] += "eps|";
                }
                if (initialR[i][j].isEmpty())
                    initialR[i][j] = "{}";
                else
                    initialR[i][j] = initialR[i][j].substring(0, initialR[i][j].length() - 1);
                initialR[i][j] = "(" + initialR[i][j] + ")";
            }
        }
    }
 
    public boolean bfs(State state){
        state.forBfs = progress.precessed;
//        vertex.distance = 0;
        ArrayList<State> queue = new ArrayList<>();
        queue.add(0, state);
        while(!queue.isEmpty()){
            State vtmp = queue.get(queue.size()-1);
            queue.remove(queue.size()-1);
            for (Transition edge: vtmp.to){
                State vOfEdge = edge.to;
                if(vOfEdge.forBfs == progress.notVisited){
                    vOfEdge.forBfs = progress.precessed;
//                    vOfEdge.distance ++;
                    queue.add(0, edge.to);
                }
            }
            vtmp.forBfs = progress.visited;
        }
        return output();
    }
 
    private boolean output(){
        for (State v: states){
            if(v.forBfs != progress.visited)
                return false;
        }
        return true;
    }
}
 
enum progress{
    visited,
    precessed,
    notVisited
}
 
 
class State {
    String name;
    ArrayList<Transition> to;
    progress forBfs = progress.notVisited;
 
    State(String name) {
        this.name = name;
        to = new ArrayList<>();
    }
 
    public void addTransTo(State to, String symbol) {
        this.to.add(new Transition(to, symbol));
    }
}
 
class Transition {
    State to;
    String symbol;
 
    public Transition(State to, String symbol) {
        this.to = to;
        this.symbol = symbol;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
 
        Transition that = (Transition) o;
 
        return Objects.equals(symbol, that.symbol);
    }
 
    @Override
    public int hashCode() {
        return symbol != null ? symbol.hashCode() : 0;
    }
}
