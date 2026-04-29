package PBL;
import javax.swing.*;
import java.awt.*;
import java.util.*;

//NFA Simulator Graph Plotter and Design
public class NfaSimulator extends JFrame
{

    //States defined as states
    HashSet<String> states = new HashSet<>();
    HashSet<String> finalStates = new HashSet<>();
    String initialState = "";
    HashSet<String> alphabet = new HashSet<>();
    HashMap<String, HashMap<String, HashSet<String>>> nfa = new HashMap<>();

    JTextField statesField, initialField, finalField, alphabetField;
    JTextField fromField, inputField, toField, inputStringField;
    JTextArea outputArea;

    DrawPanel drawPanel;

    String currentState = null;

    public NfaSimulator()
    {

        setTitle("NFA Simulator");
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

        add(new JLabel("Input String:"));
        inputStringField = new JTextField(12); add(inputStringField);

        JButton simulateBtn = new JButton("Simulate NFA");
        add(simulateBtn);

        outputArea = new JTextArea(8, 50);
        add(new JScrollPane(outputArea));

        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(800, 300));
        add(drawPanel);

        addBtn.addActionListener(e -> addTransition());
        simulateBtn.addActionListener(e -> simulate());

        setVisible(true);
    }

    void setup() {

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

    void addTransition() {

        setup();

        String from = fromField.getText().trim();
        String input = inputField.getText().trim();
        String to = toField.getText().trim();

        nfa.get(from)
                .computeIfAbsent(input, k -> new HashSet<>())
                .add(to);

        outputArea.append("NFA: " + from + " --" + input + "--> " + to + "\n");

        drawPanel.repaint();
    }

    //simulation of Nfa and checking of string
    void simulate()
    {

        String input = inputStringField.getText();

        Set<String> currentStates = new HashSet<>();
        currentStates.add(initialState);

        outputArea.append("\nStart: " + currentStates + "\n");

        for (char ch : input.toCharArray())
        {

            String sym = ch + "";
            Set<String> nextStates = new HashSet<>();

            for (String st : currentStates) {
                if (nfa.containsKey(st) && nfa.get(st).containsKey(sym)) {
                    nextStates.addAll(nfa.get(st).get(sym));
                }
            }

            currentStates = nextStates;
            outputArea.append("→ " + currentStates + "\n");

            currentState = currentStates.isEmpty() ? null : currentStates.iterator().next();
            drawPanel.repaint();

            try { Thread.sleep(800); } catch (Exception e) {}
        }

        boolean accepted = false;
        for (String s : currentStates) {
            if (finalStates.contains(s)) {
                accepted = true;
                break;
            }
        }

        outputArea.append(accepted ? "Accepted ✅\n" : "Rejected ❌\n");
    }

    class DrawPanel extends JPanel
    {

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

            ArrayList<String> stateList = new ArrayList<>(states);

            Map<String, Point> pos = new HashMap<>();

            int maxPerLayer = 8;
            int layerGap = 120;

            int index = 0;
            int layer = 0;

            while (index < stateList.size())
            {

                int count = Math.min(maxPerLayer, stateList.size() - index);

                if (count == 1) count = 3;
                if (count == 2) count = 4;

                int radius = 80 + layer * layerGap;

                for (int i = 0; i < count && index < stateList.size(); i++) {

                    double angle = 2 * Math.PI * i / count - Math.PI / 2 + (layer * 0.3);

                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));

                    pos.put(stateList.get(index), new Point(x, y));
                    index++;
                }

                layer++;
            }

            // CENTER ALIGN GRAPH
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

            for (Point p : pos.values()) {
                minX = Math.min(minX, p.x);
                minY = Math.min(minY, p.y);
                maxX = Math.max(maxX, p.x);
                maxY = Math.max(maxY, p.y);
            }

            int shiftX = (getWidth() - (maxX - minX)) / 2 - minX;
            int shiftY = (getHeight() - (maxY - minY)) / 2 - minY;

            for (Point p : pos.values()) {
                p.x += shiftX;
                p.y += shiftY;
            }

            Map<String, Integer> edgeCount = new HashMap<>();

            for (String from : nfa.keySet()) {
                for (String sym : nfa.get(from).keySet()) {

                    for (String to : nfa.get(from).get(sym)) {

                        Point p1 = pos.get(from);
                        Point p2 = pos.get(to);

                        if (p1 == null || p2 == null) continue;

                        g2.setColor(Color.BLUE);

                        String key = from + "->" + to;
                        int count = edgeCount.getOrDefault(key, 0);
                        edgeCount.put(key, count + 1);

                        // Self loop checker
                        if (from.equals(to)) {

                            int loopY = p1.y - 45 - (count * 15);

                            g2.drawOval(p1.x - 18, loopY - 18, 36, 36);
                            g2.drawString(sym, p1.x - 6, loopY - 25);

                            drawArrow(g2, p1.x, loopY - 18, p1.x + 1, loopY - 18);

                        } else {

                            double dx = p2.x - p1.x;
                            double dy = p2.y - p1.y;

                            double len = Math.sqrt(dx * dx + dy * dy);
                            if (len == 0) len = 1;

                            double offset = 40 + (count * 20);

                            double offsetX = -dy / len * offset;
                            double offsetY = dx / len * offset;

                            int ctrlX = (int) ((p1.x + p2.x) / 2 + offsetX);
                            int ctrlY = (int) ((p1.y + p2.y) / 2 + offsetY);

                            java.awt.geom.QuadCurve2D curve =
                                    new java.awt.geom.QuadCurve2D.Float(
                                            p1.x, p1.y, ctrlX, ctrlY, p2.x, p2.y);

                            g2.draw(curve);

                            int labelX = (p1.x + ctrlX + p2.x) / 3;
                            int labelY = (p1.y + ctrlY + p2.y) / 3;

                            labelX += count * 10;
                            labelY -= count * 5;

                            g2.drawString(sym, labelX, labelY);

                            drawArrow(g2, ctrlX, ctrlY, p2.x, p2.y);
                        }
                    }
                }
            }

            // Draw the states
            for (String s : stateList) {

                Point p = pos.get(s);

                if (s.equals(currentState))
                    g2.setColor(Color.RED);
                else
                    g2.setColor(Color.BLACK);

                g2.drawOval(p.x - 30, p.y - 30, 60, 60);

                if (finalStates.contains(s)) {
                    g2.drawOval(p.x - 35, p.y - 35, 70, 70);
                }

                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(s);

                g2.drawString(s, p.x - w / 2, p.y + 5);
            }
        }

        void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {

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
        new NfaSimulator();
    }
}
