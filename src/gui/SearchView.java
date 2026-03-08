package src.gui;

import javax.swing.*;
import java.awt.*;

public class SearchView implements View {

    private JPanel mainPanel;

    private JTextField searchField;
    private JButton searchButton;

    private JList<SearchResultItem> resultList;
    private DefaultListModel<SearchResultItem> resultListModel;

    private JTextArea detailsArea;

    public SearchView() {
        initComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(8, 8));

        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        searchField = new JTextField();
        searchButton = new JButton("Buscar");

        topPanel.add(new JLabel("Texto de búsqueda:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(resultList);
        listScroll.setPreferredSize(new Dimension(300, 400));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JScrollPane detailsScroll = new JScrollPane(detailsArea);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listScroll,
                detailsScroll
        );
        splitPane.setResizeWeight(0.4);

        mainPanel.add(splitPane, BorderLayout.CENTER);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JList<SearchResultItem> getResultList() {
        return resultList;
    }

    public DefaultListModel<SearchResultItem> getResultListModel() {
        return resultListModel;
    }

    public JTextArea getDetailsArea() {
        return detailsArea;
    }

    public void clearResults() {
        resultListModel.clear();
        detailsArea.setText("");
    }

    public void setDetailsText(String text) {
        detailsArea.setText(text != null ? text : "");
        detailsArea.setCaretPosition(0);
    }

    public static class SearchResultItem {
        private final String type;
        private final Object value;

        public SearchResultItem(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "[" + type + "] " + value;
        }
    }
}
