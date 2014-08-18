package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mapvis.models.TreeModel;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.models.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.graphic.HexagonalTilingView;
import utils.RandomTreeGenerator;

import java.net.URL;
import java.util.*;

public class SettingController implements Initializable {
    @FXML
    public TextField weightField;
    @FXML
    public TextField depthField;
    @FXML
    public TextField spanField;
    @FXML
    public TextField seedField;
    @FXML
    public TextArea infoArea;

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Integer>> method1 = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @FXML
    public void begin(ActionEvent event) {
        method1.get().Begin();
        chart.updateHexagons();
    }

    @FXML
    public void generateTree(ActionEvent event) {
        int span = 10, weight = 100, depth = 3, seed = 1;
        try {
            span = Integer.parseInt(spanField.getText());
        }
        catch (NumberFormatException ignored) { }
        try {
            weight = Integer.parseInt(weightField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            depth = Integer.parseInt(depthField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            seed = Integer.parseInt(seedField.getText());
        }
        catch (NumberFormatException ignored) {  }

        RandomTreeGenerator gen = new RandomTreeGenerator(seed);
        tree.set(gen.getTree(depth, span, weight));
        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Integer> leaves = tree.get().getLeaves();
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

    }


}
