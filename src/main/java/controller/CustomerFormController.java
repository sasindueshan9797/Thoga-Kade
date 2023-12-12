package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.OrderDto;
import dto.tm.CustomerTm;
import dto.tm.OrderDetailTm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.CustomerModel;
import model.Impl.CustomerModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CustomerFormController {

    @FXML
    private JFXTextField txtCustId;

    @FXML
    private JFXTextField txtCustName;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    private JFXTreeTableView<CustomerTm> tblCustomer;

    @FXML
    private TreeTableColumn<?, ?> colCustID;

    @FXML
    private TreeTableColumn<?, ?> colCustName;

    @FXML
    private TreeTableColumn<?, ?> colAddress;

    @FXML
    private TreeTableColumn<?, ?> colSalary;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private JFXTextField txtSearch;

    private CustomerModel customerModel = new CustomerModelImpl();

    public void initialize(){
        colCustID.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colCustName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new TreeItemPropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadCustomerTable();

        txtCustId.setEditable(false);
        generateId();

        txtCustName.setPromptText("Enter only letters");
        txtAddress.setPromptText("Enter only letters");
        txtSalary.setPromptText("Enter only numbers greater than 15,000");

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        txtSalary.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String text = txtSalary.getText() + event.getCharacter();

            if (!text.isEmpty() && !text.matches("[0-9]+")) {
                txtSalary.setStyle("-fx-border-color: red;");
                new Alert(Alert.AlertType.ERROR,"Please enter numerical values").show();
                event.consume();
            } else {
                txtSalary.setStyle("-fx-border-color: default;");
            }
        });

        txtSearch.textProperty().addListener((observableValue, s, newValue) -> {
            tblCustomer.setPredicate(treeItem -> {
                String custId = treeItem.getValue().getId().toLowerCase();
                String custName = treeItem.getValue().getName().toLowerCase();

                return custId.contains(newValue.toLowerCase()) ||
                        custName.contains(newValue.toLowerCase());
            });
        });

        addValidationListener(txtAddress);
        addValidationListener(txtCustName);
    }

    private void addValidationListener(JFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = textField.getText() + event.getCharacter();
            if (!isValidInput(input)) {
                new Alert(Alert.AlertType.ERROR,"Please enter only letters, starting with a capital letter.").show();
                textField.setStyle("-fx-border-color: red;");
                event.consume();
            } else {
                textField.setStyle("-fx-border-color: default;");
            }
        });
    }

    private boolean isValidInput(String input) {
        return input.matches("[A-Z][a-zA-Z]*");
    }


    public void generateId(){
        try {
            CustomerDto dto = customerModel.lastCustomer();
            if (dto!=null){
                String id = dto.getId();
                int num = Integer.parseInt(id.split("[C]")[1]);
                num++;
                txtCustId.setText(String.format("C%03d",num));
            }else{
                txtCustId.setText("C001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setData(TreeItem<CustomerTm> newValue) {
        if (newValue != null) {
            txtCustId.setText(newValue.getValue().getId());
            txtCustName.setText(newValue.getValue().getName());
            txtAddress.setText(newValue.getValue().getAddress());
            txtSalary.setText(String.valueOf(newValue.getValue().getSalary()));
        }
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerModel.allCustomers();

            for (CustomerDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");

                CustomerTm c = new CustomerTm(
                        dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });

                tmList.add(c);
            }

            TreeItem<CustomerTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblCustomer.setRoot(treeItem);
            tblCustomer.setShowRoot(false);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(String id) {
        try {
            boolean isDeleted = customerModel.deleteCustomer(id);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                loadCustomerTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) tblCustomer.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadCustomerTable();
        tblCustomer.refresh();
        clearFields();
    }

    private void clearFields() {
        tblCustomer.refresh();
        txtSalary.clear();
        txtAddress.clear();
        txtCustName.clear();
        generateId();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String name = txtCustName.getText();
        String address = txtAddress.getText();
        String salary = txtSalary.getText();

        boolean hasEmptyFields = false;

        if (name.isEmpty()) {
            setErrorStyle(txtCustName);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtCustName);
        }

        if (address.isEmpty()) {
            setErrorStyle(txtAddress);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtAddress);
        }

        if (salary.isEmpty() || Integer.parseInt(salary)<15000) {
            setErrorStyle(txtSalary);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtSalary);
        }

        try {
            if (!hasEmptyFields) {
                boolean isSaved = customerModel.saveCustomer(new CustomerDto(txtCustId.getText(),
                        name,
                        address,
                        Double.parseDouble(salary)
                ));

                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Saved!").show();
                    loadCustomerTable();
                    clearFields();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Fields are not completely filled").show();
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
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


    @FXML
    void btnUpdateOnAction(ActionEvent event){
        String name = txtCustName.getText();
        String address = txtAddress.getText();
        String salary = txtSalary.getText();

        boolean hasEmptyFields = false;

        if (name.isEmpty()) {
            setErrorStyle(txtCustName);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtCustName);
        }

        if (address.isEmpty()) {
            setErrorStyle(txtAddress);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtAddress);
        }

        if (salary.isEmpty()) {
            setErrorStyle(txtSalary);
            hasEmptyFields = true;
        } else {
            resetTextFieldStyle(txtSalary);
        }

        try {
            if (!hasEmptyFields) {
                boolean isUpdated = customerModel.updateCustomer(new CustomerDto(txtCustId.getText(),
                        name,
                        address,
                        Double.parseDouble(salary)
                ));

                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Updated!").show();
                    loadCustomerTable();
                    clearFields();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Fields are not completely filled").show();
            }
        } catch (ClassNotFoundException | SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

}
