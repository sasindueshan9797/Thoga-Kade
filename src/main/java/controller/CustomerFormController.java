package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.tm.CustomerTm;
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

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(TreeItem<CustomerTm> newValue) {
        if (newValue != null) {
            txtCustId.setEditable(false);
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
        txtCustId.clear();
        txtCustId.setEditable(true);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        try {
            boolean isSaved = customerModel.saveCustomer(new CustomerDto(txtCustId.getText(),
                    txtCustName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText())
            ));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
                loadCustomerTable();
                clearFields();
            }

        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event){
        try {
            boolean isUpdated = customerModel.updateCustomer(new CustomerDto(txtCustId.getText(),
                    txtCustName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText())
            ));
            if (isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadCustomerTable();
                clearFields();
            }
        } catch (ClassNotFoundException | SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {

    }

}
