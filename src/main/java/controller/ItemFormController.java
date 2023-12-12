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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

        txtItemCode.setEditable(false);
        generateId();

        txtItemDescription.setPromptText("Enter only letters");
        txtUnitPrice.setPromptText("Enter only numbers");
        txtQty.setPromptText("Enter only numbers");

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        txtSearch.textProperty().addListener((observableValue, s, newValue) -> {
            tblItem.setPredicate(treeItem -> {
                String itemCode = treeItem.getValue().getCode().toLowerCase();
                String itemDesc = treeItem.getValue().getDesc().toLowerCase();

                return itemCode.contains(newValue.toLowerCase()) ||
                        itemDesc.contains(newValue.toLowerCase());
            });
        });

        txtItemDescription.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            KeyCode code = event.getCode();

            if (character.equals("\b") || code == KeyCode.BACK_SPACE || code == KeyCode.SPACE) {
                // Ignore backspace, space, and shift keys
                return;
            }

            String text = txtItemDescription.getText() + character;

            if (!text.isEmpty() && !text.matches("[A-Z][a-zA-Z0-9\\s]*")) {
                txtItemDescription.setStyle("-fx-border-color: red;");
                new Alert(Alert.AlertType.ERROR, "Please enter only backspace, spaces, letters (starting with a capital letter), and numbers.").show();
                event.consume();
            } else {
                txtItemDescription.setStyle("-fx-border-color: default;");
            }
        });

        addValidationListener(txtUnitPrice);
        addValidationListener(txtQty);
    }

    private void addValidationListener(JFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            KeyCode code = event.getCode();

            if (character.equals("\b") || code == KeyCode.BACK_SPACE) {
                return;
            }

            String input = textField.getText() + character;
            if (!isValidInput(input)) {
                new Alert(Alert.AlertType.ERROR, "Please enter numerical values").show();
                textField.setStyle("-fx-border-color: red;");
                event.consume();
            } else {
                textField.setStyle("-fx-border-color: default;");
            }
        });
    }

    private boolean isValidInput(String input) {
        return input.matches("[0-9]+");
    }


    private void generateId() {
        try {
            ItemDto dto = itemModel.lastItem();
            if (dto!=null){
                String code = dto.getCode();
                int num = Integer.parseInt(code.split("[P]")[1]);
                num++;
                txtItemCode.setText(String.format("P%03d",num));
            }else{
                txtItemCode.setText("P001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setData(TreeItem<ItemTm> newValue) {
        if (newValue != null) {
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
        txtQty.clear();
        txtItemDescription.clear();
        txtUnitPrice.clear();
        txtSearch.clear();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String desc = txtItemDescription.getText();
        String unitPrice = txtUnitPrice.getText();
        String qty = txtQty.getText();

        boolean hasEmptyFields = false;

        if (desc.isEmpty()) {
            setErrorStyle(txtItemDescription);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtItemDescription);
        }

        if (unitPrice.isEmpty()) {
            setErrorStyle(txtUnitPrice);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtUnitPrice);
        }

        if (qty.isEmpty()) {
            setErrorStyle(txtQty);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtQty);
        }

        try {
            if (!hasEmptyFields) {
                boolean isSaved = itemModel.saveItem(new ItemDto(txtItemCode.getText(),
                        desc,
                        Double.parseDouble(unitPrice),
                        Integer.parseInt(qty)
                ));

                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "Item Saved!").show();
                    loadItemTable();
                    clearFields();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Fields are not completely filled").show();
            }

        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String desc = txtItemDescription.getText();
        String unitPrice = txtUnitPrice.getText();
        String qty = txtQty.getText();

        boolean hasEmptyFields = false;

        if (desc.isEmpty()) {
            setErrorStyle(txtItemDescription);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtItemDescription);
        }

        if (unitPrice.isEmpty()) {
            setErrorStyle(txtUnitPrice);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtUnitPrice);
        }

        if (qty.isEmpty()) {
            setErrorStyle(txtQty);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtQty);
        }

        try {
            if (!hasEmptyFields) {
                boolean isUpdated = itemModel.updateItem(new ItemDto(txtItemCode.getText(),
                        desc,
                        Double.parseDouble(unitPrice),
                        Integer.parseInt(qty)
                ));
                if (isUpdated){
                    new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
                    loadItemTable();
                    clearFields();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Fields are not completely filled").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void setErrorStyle(JFXTextField textField) {
        textField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        textField.requestFocus();
    }

    private void resetTextFieldStyle(JFXTextField textField) {
        textField.setBorder(Border.EMPTY);
    }

}
