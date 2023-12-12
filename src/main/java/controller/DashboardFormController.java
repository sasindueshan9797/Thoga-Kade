package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.io.IOException;

public class DashboardFormController {
    public AnchorPane dashboardPane;


    public void btnCustomerOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"))));
            stage.setResizable(false);
            stage.setTitle("Customer Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnItemOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ItemForm.fxml"))));
            stage.setResizable(false);
            stage.setTitle("Item Form");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnOrderOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PlaceOrderForm.fxml"))));
            stage.setResizable(false);
            stage.setTitle("Place Order");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnOrderDetailOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) dashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderDetailForm.fxml"))));
            stage.setResizable(false);
            stage.setTitle("Order Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
