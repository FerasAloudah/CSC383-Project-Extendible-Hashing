package controllers;

import core.Bucket;
import core.ExtendibleHashing;
import core.HashFunctions;
import core.Person;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExtendibleGUIController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button insertButton;

    @FXML
    private TextField nameText;

    @FXML
    private TextField keyText;

    @FXML
    private TableView<String> addressTable;

    @FXML
    private Button changeButton;

    @FXML
    private TextField changeText;

    @FXML
    private TextField findText;

    @FXML
    private TableView<String> bucketTable;

    @FXML
    private Label local;

    @FXML
    private Label global;

    @FXML
    private Button findButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<String, String> addressColumn;

    @FXML
    private TableColumn<String, String> bucketColumn;

    private ExtendibleHashing extendibleHashing;
    private int g;

    @FXML
    void change(ActionEvent event) {
        if (changeText.getText().isEmpty()) {
            return;
        }
        extendibleHashing = new ExtendibleHashing(Integer.parseInt(changeText.getText()));
        populateTable(HashFunctions.getGlobalArray(1), true);
        updateTables(1);
        bucketTable.getItems().clear();
        bucketTable.getItems().clear();
        changeText.clear();
    }

    @FXML
    void delete(ActionEvent event) {
        String selected = bucketTable.getSelectionModel().getSelectedItem();
        int key = Integer.parseInt(selected.split(" ")[0]);
        updateTables(extendibleHashing.getBucket(key).getLocal());
        extendibleHashing.remove(key);
        bucketTable.getItems().removeAll(selected);
        populateTable(HashFunctions.getGlobalArray(extendibleHashing.getGlobal()), true);
    }

    @FXML
    void find(ActionEvent event) {
        int key = Integer.parseInt(findText.getText());
        updateBucketTable(key);
    }

    private void updateBucketTable(int key) {
        Bucket bucket = extendibleHashing.getBucket(key);
        if (bucket == null) {
            return;
        }

        List<String> arr = new ArrayList<>();
        List<Person> people = bucket.getPeople();
        int index = 0;

        for (int i = 0; i < people.size(); i++) {
            arr.add(people.get(i).toString());

            if (people.get(i).getKey() == key) {
                index = i;
            }
        }

        updateTables(bucket.getLocal());
        populateTable(arr, false);
        extendibleHashing.printTable();
        bucketTable.getSelectionModel().select(index);
    }

    @FXML
    void insert(ActionEvent event) {
        if (!nameText.getText().isEmpty() && !keyText.getText().isEmpty()) {
            int key = Integer.parseInt(keyText.getText());

            extendibleHashing.insert(new Person(nameText.getText(), key));
            updateBucketTable(key);
            updateTables(extendibleHashing.getBucket(key).getLocal());
            populateTable(extendibleHashing.getBucket(key).toStringList(), false);

            nameText.clear();
            keyText.clear();
        }
    }

    private void updateTables(int local) {
        int global = extendibleHashing.getGlobal();

        if (global != g) {
            this.global.setText("Global: " + global);
            g = global;
            populateTable(HashFunctions.getGlobalArray(global), true);
        }

        this.local.setText("Local: " + local);
    }

    @FXML
    void updateBucket(MouseEvent event) {
        String selected = addressTable.getSelectionModel().getSelectedItem();
        updateTables(extendibleHashing.getBucketLocal(selected));
        populateTable(extendibleHashing.getBucket(selected), false);
    }

    @FXML
    void initialize() {
        extendibleHashing = new ExtendibleHashing(4);
        g = 1;
        extendibleHashing = new ExtendibleHashing(2);
        populateTable(HashFunctions.getGlobalArray(1), true);
    }

    private void populateTable(List<String> arr, boolean address) {
        ObservableList<String> rows = FXCollections.observableArrayList(arr);

        if (address) {
            addressColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
            addressTable.setItems(rows);
        } else {
            bucketColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
            bucketTable.setItems(rows);
        }
    }

}
