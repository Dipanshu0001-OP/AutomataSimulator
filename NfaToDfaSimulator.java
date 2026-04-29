package PBL;
import javax.swing.*;
import java.awt.*;
import java.util.*;

//Nfa to Dfa convertor and simulator for string checking
public class NfaToDfaSimulator extends JFrame
{

    HashSet<String> states = new HashSet<>();
    HashSet<String> finalStates = new HashSet<>();
    String initialState = "";
    HashSet<String> alphabet = new HashSet<>();

    HashMap<String, HashMap<String, HashSet<String>>> nfa = new HashMap<>();

    HashMap<Set<String>, HashMap<String, Set<String>>> dfa = new HashMap<>();
    ArrayList<Set<String>> dfaStates = new ArrayList<>();
    HashSet<Set<String>> dfaFinalStates = new HashSet<>();

    JTextField statesField, initialField, finalField, alphabetField;
    JTextField fromField, inputField, toField, inputStringField;
    JTextArea outputArea;

    DrawPanel drawPanel;

    Set<String> currentState;

    public NfaToDfaSimulator()
    {

        setTitle("NFA → DFA Simulator");
        setSize(900, 650);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("States:"));
        statesField = new JTextField(12); add(statesField);

        add(new JLabel("Alphabet:"));
        alphabetField = new JTextField(10); add(alphabetField);

        add(new JLabel("Initial:"));
        initialField = new JTextField(6); add(initialField);

        add(new JLabel("Final:"));
        finalField = new JTextField(10); add(finalField);

        add(new JLabel("From:"));
        fromField = new JTextField(5); add(fromField);

        add(new JLabel("Input:"));
        inputField = new JTextField(5); add(inputField);

        add(new JLabel("To:"));
        toField = new JTextField(5); add(toField);

        JButton addBtn = new JButton("Add NFA Transition");
        add(addBtn);

        JButton convertBtn = new JButton("Convert to DFA");
        add(convertBtn);

        add(new JLabel("Input String:"));
        inputStringField = new JTextField(12); add(inputStringField);

        JButton simulateBtn = new JButton("Simulate DFA");
        add(simulateBtn);

        outputArea = new JTextArea(8, 50);
        add(new JScrollPane(outputArea));

        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(800, 300));
        add(drawPanel);

        addBtn.addActionListener(e -> addTransition());
        convertBtn.addActionListener(e -> convertToDFA());
        simulateBtn.addActionListener(e -> simulate());

        setVisible(true);
    }

    //addition of states and value fixing
    void setup()
    {

        states.clear();
        finalStates.clear();
        alphabet.clear();

        for (String s : statesField.getText().split(",")) states.add(s.trim());
        for (String s : finalField.getText().split(",")) finalStates.add(s.trim());
        for (String s : alphabetField.getText().split(",")) alphabet.add(s.trim());

        initialState = initialField.getText().trim();

        for (String s : states) {
            nfa.putIfAbsent(s, new HashMap<>());
        }
    }

    void addTransition()
    {

        setup();

        String from = fromField.getText().trim();
        String input = inputField.getText().trim();
        String to = toField.getText().trim();

        nfa.get(from)
                .computeIfAbsent(input, k -> new HashSet<>())
                .add(to);

        outputArea.append("NFA: " + from + " --" + input + "--> " + to + "\n");
    }

    Set<String> move(Set<String> states, String symbol)
    {

        Set<String> result = new HashSet<>();

        for (String st : states) {
            if (nfa.containsKey(st) && nfa.get(st).containsKey(symbol)) {
                result.addAll(nfa.get(st).get(symbol));
            }
        }
        return result;
    }

    void convertToDFA()
    {

        setup();

        dfa.clear();
        dfaStates.clear();
        dfaFinalStates.clear();

        Set<String> start = new HashSet<>();
        start.add(initialState);

        Queue<Set<String>> queue = new LinkedList<>();
        queue.add(start);

        dfaStates.add(start);

        while (!queue.isEmpty())
        {

            Set<String> current = queue.poll();
            dfa.putIfAbsent(current, new HashMap<>());

            for (String sym : alphabet) {

                Set<String> next = move(current, sym);

                if (!dfaStates.contains(next)) {
                    dfaStates.add(next);
                    queue.add(next);
                }

                dfa.get(current).put(sym, next);
            }
        }

        // Final states
        for (Set<String> s : dfaStates)
        {
            for (String f : finalStates)
            {
                if (s.contains(f)) {
                    dfaFinalStates.add(s);
                    break;
                }
            }
        }

        outputArea.append("\n--- DFA TRANSITIONS ---\n");

        for (Set<String> from : dfa.keySet())
        {
            for (String sym : dfa.get(from).keySet())
            {
                Set<String> to = dfa.get(from).get(sym);
                outputArea.append(from + " --" + sym + "--> " + to + "\n");
            }
        }

        drawPanel.repaint();
    }

    void simulate()
    {

        if (dfaStates.isEmpty())
        {
            outputArea.append("Convert to DFA first!\n");
            return;
        }

        String input = inputStringField.getText();

        new Thread(() -> {

            currentState = dfaStates.get(0);
            update();

            for (char ch : input.toCharArray()) {

                String sym = ch + "";

                if (!dfa.get(currentState).containsKey(sym)) {
                    append("Rejected ❌");
                    return;
                }

                currentState = dfa.get(currentState).get(sym);
                append("→ " + currentState);

                update();

                try { Thread.sleep(1000); } catch (Exception e) {}
            }

            if (dfaFinalStates.contains(currentState))
                append("Accepted ✅");
            else
                append("Rejected ❌");

        }).start();
    }

    void update()
    {
        SwingUtilities.invokeLater(() -> drawPanel.repaint());
    }

    void append(String s)
    {
        SwingUtilities.invokeLater(() -> outputArea.append(s + "\n"));
    }

    //draw panel for the components of graph
    class DrawPanel extends JPanel {

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int centerX = width / 2;
            int centerY = height / 2;

            int totalStates = dfaStates.size();

            Map<Set<String>, Point> pos = new HashMap<>();

            int maxPerLayer = 8;
            int layerGap = 120;

            int index = 0;
            int layer = 0;

            while (index < totalStates) {

                int statesInLayer = Math.min(maxPerLayer, totalStates - index);

                if (statesInLayer == 1) statesInLayer = 3;
                if (statesInLayer == 2) statesInLayer = 4;

                int radius = 80 + layer * layerGap;

                for (int i = 0; i < statesInLayer && index < totalStates; i++)
                {

                    Set<String> state = dfaStates.get(index);

                    double angle = 2 * Math.PI * i / statesInLayer
                            - Math.PI / 2
                            + (layer * 0.3);

                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));

                    pos.put(state, new Point(x, y));
                    index++;
                }

                layer++;
            }

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

            for (Point p : pos.values()) {
                minX = Math.min(minX, p.x);
                minY = Math.min(minY, p.y);
                maxX = Math.max(maxX, p.x);
                maxY = Math.max(maxY, p.y);
            }

            int graphWidth = maxX - minX;
            int graphHeight = maxY - minY;

            int shiftX = (getWidth() - graphWidth) / 2 - minX;
            int shiftY = (getHeight() - graphHeight) / 2 - minY;

            for (Point p : pos.values()) {
                p.x += shiftX;
                p.y += shiftY;
            }

            Map<String, Integer> edgeCount = new HashMap<>();

            for (Set<String> from : dfa.keySet())
            {
                for (String sym : dfa.get(from).keySet())
                {

                    Set<String> to = dfa.get(from).get(sym);

                    Point p1 = pos.get(from);
                    Point p2 = pos.get(to);

                    if (p1 == null || p2 == null) continue;

                    g2.setColor(Color.BLUE);

                    String edgeKey = from.toString() + "->" + to.toString();

                    int count = edgeCount.getOrDefault(edgeKey, 0);
                    edgeCount.put(edgeKey, count + 1);

                    if (from.equals(to)) {

                        int loopX = p1.x;
                        int loopY = p1.y - 45 - (count * 15);

                        g2.drawOval(loopX - 18, loopY - 18, 36, 36);
                        g2.drawString(sym, loopX - 6, loopY - 25);

                        drawArrowHead(g2, loopX, loopY - 18, loopX + 1, loopY - 18);

                    } else {

                        double dx = p2.x - p1.x;
                        double dy = p2.y - p1.y;

                        double len = Math.sqrt(dx * dx + dy * dy);
                        if (len == 0) len = 1;

                        double baseOffset = 40 + (count * 20);

                        double offsetX = -dy / len * baseOffset;
                        double offsetY = dx / len * baseOffset;

                        int ctrlX = (int) ((p1.x + p2.x) / 2 + offsetX);
                        int ctrlY = (int) ((p1.y + p2.y) / 2 + offsetY);

                        java.awt.geom.QuadCurve2D curve =
                                new java.awt.geom.QuadCurve2D.Float(
                                        p1.x, p1.y, ctrlX, ctrlY, p2.x, p2.y);

                        g2.draw(curve);

                        int labelX = (int) ((p1.x + ctrlX + p2.x) / 3);
                        int labelY = (int) ((p1.y + ctrlY + p2.y) / 3);

                        labelX += count * 10;
                        labelY -= count * 5;

                        g2.drawString(sym, labelX, labelY);

                        drawArrowHead(g2, ctrlX, ctrlY, p2.x, p2.y);
                    }
                }
            }

            for (Set<String> s : dfaStates)
            {

                Point p = pos.get(s);

                if (s.equals(currentState))
                    g2.setColor(Color.RED);
                else
                    g2.setColor(Color.BLACK);

                g2.drawOval(p.x - 30, p.y - 30, 60, 60);

                if (dfaFinalStates.contains(s)) {
                    g2.drawOval(p.x - 35, p.y - 35, 70, 70);
                }

                String label = s.toString();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(label);

                g2.drawString(label, p.x - textWidth / 2, p.y + 5);
            }
        }

        //adding arrowhead to the the graph transitions
        void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {

            double phi = Math.toRadians(25);
            int barb = 12;

            double dy = y2 - y1;
            double dx = x2 - x1;
            double theta = Math.atan2(dy, dx);

            for (int i = 0; i < 2; i++) {
                double rho = theta + (i == 0 ? phi : -phi);
                int x = (int) (x2 - barb * Math.cos(rho));
                int y = (int) (y2 - barb * Math.sin(rho));
                g2.drawLine(x2, y2, x, y);
            }
        }
    }

    public static void main(String[] args) {
        new NfaToDfaSimulator();
    }
}

