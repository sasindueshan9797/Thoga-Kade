package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.tm.CustomerTm;
import dto.tm.ItemTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import model.CustomerModel;
import model.Impl.ItemModelImpl;
import model.ItemModel;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ItemFormController {

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtItemDescription;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private TreeTableColumn<?, ?> colCode;

    @FXML
    private TreeTableColumn<?, ?> colDesc;

    @FXML
    private TreeTableColumn<?, ?> colUnitPrice;

    @FXML
    private TreeTableColumn<?, ?> colQty;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private JFXTextField txtSearch;

    private ItemModel itemModel = new ItemModelImpl();

    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(TreeItem<ItemTm> newValue) {
        if (newValue != null) {
            txtItemCode.setEditable(false);
            txtItemCode.setText(newValue.getValue().getCode());
            txtItemDescription.setText(newValue.getValue().getDesc());
            txtUnitPrice.setText(String.valueOf(newValue.getValue().getUnitPrice()));
            txtQty.setText(String.valueOf(newValue.getValue().getQty()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();

        try {
            List<ItemDto> dtoList = itemModel.allItems();

            for (ItemDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");

                ItemTm item = new ItemTm(
                        dto.getCode(),
                        dto.getDesc(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteItem(item.getCode());
                });

                tmList.add(item);
            }

            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(String code) {
        try {
            boolean isDeleted = itemModel.deleteItem(code);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadItemTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) tblItem.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadItemTable();
        tblItem.refresh();
        clearFields();
    }

    private void clearFields() {
        tblItem.refresh();
        txtItemCode.clear();
        txtQty.clear();
        txtItemDescription.clear();
        txtSearch.clear();
        txtItemCode.setEditable(true);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        try {
            boolean isSaved = itemModel.saveItem(new ItemDto(txtItemCode.getText(),
                    txtItemDescription.getText(),
                    Double.parseDouble(txtUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText())
            ));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
                loadItemTable();
                clearFields();
            }

        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            boolean isUpdated = itemModel.updateItem(new ItemDto(txtItemCode.getText(),
                    txtItemDescription.getText(),
                    Double.parseDouble(txtUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText())
            ));
            if (isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
                loadItemTable();
                clearFields();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {

    }

}
