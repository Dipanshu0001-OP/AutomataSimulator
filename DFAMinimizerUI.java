package PBL;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DFAMinimizerUI extends JFrame {

    private JTextField statesField, alphabetField, initialField, finalField, inputStringField;
    private JTextField fromField, symbolField, toField;
    private JTextArea outputArea;

    // Transitions: give as state->symbol->state
    private Map<String, Map<String, String>> transitions = new HashMap<>();

    //simulation work
    private List<Set<String>> minimizedPartitions = new ArrayList<>();
    private String minInitialGroupName = "";
    private Set<Integer> minFinalGroupIndices = new HashSet<>();

    public DFAMinimizerUI() {
        setTitle("DFA Minimizer & Simulator");
        setSize(1000, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Top Panel: Configuration
        JPanel configPanel = new JPanel(new GridLayout(2, 1));

        JPanel row1 = new JPanel(new FlowLayout());
        statesField = new JTextField("1,2,3", 5);
        alphabetField = new JTextField("a,b", 5);
        initialField = new JTextField("1", 3);
        finalField = new JTextField("3", 3);

        row1.add(new JLabel("States:")); row1.add(statesField);
        row1.add(new JLabel("Alphabet:")); row1.add(alphabetField);
        row1.add(new JLabel("Initial:")); row1.add(initialField);
        row1.add(new JLabel("Finals:")); row1.add(finalField);

        JPanel row2 = new JPanel(new FlowLayout());
        fromField = new JTextField(3);
        symbolField = new JTextField(3);
        toField = new JTextField(3);
        JButton addBtn = new JButton("Add Transition");
        JButton minimizeBtn = new JButton("Minimize DFA");

        row2.add(new JLabel("From:")); row2.add(fromField);
        row2.add(new JLabel("In:")); row2.add(symbolField);
        row2.add(new JLabel("To:")); row2.add(toField);
        row2.add(addBtn);
        row2.add(minimizeBtn);

        configPanel.add(row1);
        configPanel.add(row2);
        add(configPanel, BorderLayout.NORTH);

        // Output field
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        //Simulator panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        inputStringField = new JTextField(15);
        JButton simulateBtn = new JButton("Simulate String");
        JButton clearBtn = new JButton("Clear All");

        bottomPanel.add(new JLabel("Test String:"));
        bottomPanel.add(inputStringField);
        bottomPanel.add(simulateBtn);
        bottomPanel.add(clearBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        //action listeners for the JButtons
        addBtn.addActionListener(e -> addTransition());
        minimizeBtn.addActionListener(e -> minimize());
        simulateBtn.addActionListener(e -> simulateString());
        clearBtn.addActionListener(e -> {
            transitions.clear();
            minimizedPartitions.clear();
            outputArea.setText("");
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    //transiton adding functions
    private void addTransition()
    {
        String from = fromField.getText().trim();
        String sym = symbolField.getText().trim();
        String to = toField.getText().trim();
        if (from.isEmpty() || sym.isEmpty() || to.isEmpty()) return;

        transitions.putIfAbsent(from, new HashMap<>());
        transitions.get(from).put(sym, to);
        outputArea.append("Added: (" + from + ") --" + sym + "--> (" + to + ")\n");
        fromField.setText(""); symbolField.setText(""); toField.setText("");
        fromField.requestFocus();
    }

    private void minimize()
    {
        try {
            List<String> allStates = Arrays.asList(statesField.getText().split(","));
            List<String> alphabet = Arrays.asList(alphabetField.getText().split(","));
            List<String> finalStates = Arrays.asList(finalField.getText().split(","));

            // Initial Partition (Non-Final vs Final) states
            Set<String> P1 = new HashSet<>();
            Set<String> P2 = new HashSet<>();
            for (String s : allStates) {
                String trimmed = s.trim();
                if (finalStates.contains(trimmed)) P2.add(trimmed);
                else P1.add(trimmed);
            }

            minimizedPartitions = new ArrayList<>();
            if (!P1.isEmpty()) minimizedPartitions.add(P1);
            if (!P2.isEmpty()) minimizedPartitions.add(P2);

            boolean changed = true;
            while (changed)
            {
                changed = false;
                List<Set<String>> nextPartitions = new ArrayList<>();
                for (Set<String> group : minimizedPartitions)
                {
                    Map<String, Set<String>> splittingMap = new HashMap<>();
                    for (String state : group)
                    {
                        StringBuilder key = new StringBuilder();
                        for (String sym : alphabet)
                        {
                            String target = transitions.getOrDefault(state, new HashMap<>()).get(sym.trim());
                            key.append(findGroupIndex(minimizedPartitions, target)).append("|");
                        }
                        splittingMap.putIfAbsent(key.toString(), new HashSet<>());
                        splittingMap.get(key.toString()).add(state);
                    }
                    if (splittingMap.size() > 1) changed = true;
                    nextPartitions.addAll(splittingMap.values());
                }
                minimizedPartitions = nextPartitions;
            }

            // Identify Initial and Final Groups for Simulation
            minFinalGroupIndices.clear();
            String startState = initialField.getText().trim();
            for (int i = 0; i < minimizedPartitions.size(); i++)
            {
                Set<String> group = minimizedPartitions.get(i);
                if (group.contains(startState)) minInitialGroupName = "G" + i;
                for (String s : group)
                {
                    if (finalStates.contains(s))
                    {
                        minFinalGroupIndices.add(i);
                        break;
                    }
                }
            }

            displayResult(alphabet);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Check inputs! Error: " + e.getMessage());
        }
    }

    //function to find the group index of states
    private int findGroupIndex(List<Set<String>> parts, String state)
    {
        if (state == null) return -1;
        for (int i = 0; i < parts.size(); i++)
        {
            if (parts.get(i).contains(state)) return i;
        }
        return -1;
    }

    private void displayResult(List<String> alphabet)
    {
        outputArea.append("\n--- MINIMIZATION COMPLETE ---\n");
        for (int i = 0; i < minimizedPartitions.size(); i++)
        {
            String rep = minimizedPartitions.get(i).iterator().next();
            outputArea.append("G" + i + " " + minimizedPartitions.get(i) +
                    (minFinalGroupIndices.contains(i) ? " [FINAL]" : "") +
                    (minInitialGroupName.equals("G" + i) ? " [START]" : "") + "\n");

            for (String sym : alphabet)
            {
                String target = transitions.getOrDefault(rep, new HashMap<>()).get(sym.trim());
                int targetIdx = findGroupIndex(minimizedPartitions, target);
                outputArea.append("   --" + sym + "--> " + (targetIdx == -1 ? "TRAP" : "G" + targetIdx) + "\n");
            }
        }
        outputArea.append("----------------------------\n");
    }

    //stimulation and checking of strings
    private void simulateString()
    {
        if (minimizedPartitions.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Minimize the DFA first!");
            return;
        }

        String input = inputStringField.getText().trim();
        outputArea.append("\nSimulating string: \"" + input + "\"\n");

        int currentGroupIdx = Integer.parseInt(minInitialGroupName.substring(1));
        boolean failed = false;

        for (char c : input.toCharArray())
        {
            String sym = String.valueOf(c);
            String rep = minimizedPartitions.get(currentGroupIdx).iterator().next();
            String targetState = transitions.getOrDefault(rep, new HashMap<>()).get(sym);

            int nextGroupIdx = findGroupIndex(minimizedPartitions, targetState);

            if (nextGroupIdx == -1)
            {
                outputArea.append("Step: G" + currentGroupIdx + " --" + sym + "--> TRAP\n");
                failed = true;
                break;
            }

            outputArea.append("Step: G" + currentGroupIdx + " --" + sym + "--> G" + nextGroupIdx + "\n");
            currentGroupIdx = nextGroupIdx;
        }

        if (!failed && minFinalGroupIndices.contains(currentGroupIdx))
        {
            outputArea.append("RESULT: String ACCEPTED\n");
        } else {
            outputArea.append("RESULT: String REJECTED\n");
        }
    }
}
