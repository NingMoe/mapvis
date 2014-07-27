package mapvis.gui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.RandomColorStyler;
import mapvis.Impl.TreeModel;
import mapvis.graphic.TileStyler;
import mapvis.grid.Grid;
import mapvis.tree.MPTree;

import java.net.URL;
import java.util.ResourceBundle;


public class AppController implements Initializable {

    @FXML
    public TreeTableView<TreeTableViewModelAdapter.Node> treeTableView;

    @FXML
    public SettingController settingController;

    @FXML
    public ChartController chartController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tree.bindBidirectional(settingController.tree);
        grid.bindBidirectional(settingController.grid);
        tree.bindBidirectional(chartController.tree);
        grid.bindBidirectional(chartController.grid);

        ObjectBinding db = new ObjectBinding() {
            {
                super.bind(settingController.colorscheme, tree, grid);
            }
            @Override
            protected Object computeValue() {
                if (tree.get() == null)
                    tree.set(new MPTree<>());
                if (grid.get() == null)
                    grid.set(new HashMapGrid<>());

                String colorscheme = settingController.colorscheme.get();
                if (colorscheme == null)
                    return new RandomColorStyler<>(tree.get(), grid.get(), 1000, 1);
                switch (colorscheme){
                    case "level1":
                        return new RandomColorStyler<>(tree.get(), grid.get(), 1, 1);
                    case "level2":
                        return new RandomColorStyler<>(tree.get(), grid.get(), 2, 1);
                    case "level3":
                        return new RandomColorStyler<>(tree.get(), grid.get(), 3, 1);
                    case "level4":
                        return new RandomColorStyler<>(tree.get(), grid.get(), 4, 1);
                    case "random" :
                    default:
                        return new RandomColorStyler<>(tree.get(), grid.get(), 1000, 1);
                }
            }
        };
        //tileStyler.bind(db);

        chartController.chart.stylerProperty().bind(db);

        settingController.chart = chartController.chart;

        tree.addListener((v, o, n)-> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<TreeTableViewModelAdapter.Node> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });


    }

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<TileStyler<Integer>> tileStyler = new SimpleObjectProperty<>();



}
