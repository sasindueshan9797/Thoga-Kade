package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.ItemTm;
import dto.tm.OrderDetailTm;
import dto.tm.OrderTm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CustomerModel;
import model.Impl.CustomerModelImpl;
import model.Impl.ItemModelImpl;
import model.Impl.OrderDetailsModelImpl;
import model.Impl.OrderModelImpl;
import model.ItemModel;
import model.OrderDetailsModel;
import model.OrderModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public class OrderDetailFormController {

    public JFXTextField txtItemDetails;
    public JFXTextField txtCustomerDetails;
    @FXML
    private JFXTreeTableView<OrderDetailTm> tblOrderDetail;

    @FXML
    private TreeTableColumn<?, ?> colOrderId;

    @FXML
    private TreeTableColumn<?, ?> colItemCode;

    @FXML
    private TreeTableColumn<?, ?> colCustId;

    @FXML
    private TreeTableColumn<?, ?> colUnitPrice;

    @FXML
    private TreeTableColumn<?, ?> colQty;

    @FXML
    private TreeTableColumn<?, ?> colTotal;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private JFXTextField txtSearch;



    private OrderDetailsModel orderDetailsModel = new OrderDetailsModelImpl();
    private OrderModel orderModel = new OrderModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
    private CustomerModel customerModel = new CustomerModelImpl();

    public void initialize(){
        colOrderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderId"));
        colItemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemCode"));
        colCustId.setCellValueFactory(new TreeItemPropertyValueFactory<>("custId"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadOrderDetailTable();

        txtItemDetails.setEditable(false);
        txtCustomerDetails.setEditable(false);

        tblOrderDetail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<OrderDetailTm>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<OrderDetailTm>> observable,
                                TreeItem<OrderDetailTm> oldValue, TreeItem<OrderDetailTm> newValue) {

                    if (newValue != null) {
                        try {

                            txtItemDetails.setText("Selected: " + (itemModel.getItem(newValue.getValue().getItemCode()).getDesc()));
                            txtCustomerDetails.setText("Selected: " + (customerModel.getCustomer(newValue.getValue().getCustId()).getName()));

                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        txtItemDetails.clear();
                        txtCustomerDetails.clear();
                    }
            }
        });

        txtSearch.textProperty().addListener((observableValue, s, newValue) -> {
            tblOrderDetail.setPredicate(treeItem -> {
                String orderId = treeItem.getValue().getOrderId().toLowerCase();
                String custId = treeItem.getValue().getCustId().toLowerCase();

                return orderId.contains(newValue.toLowerCase()) ||
                        custId.contains(newValue.toLowerCase());
            });
        });
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) tblOrderDetail.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadOrderDetailTable();
        tblOrderDetail.refresh();
        txtCustomerDetails.clear();
        txtItemDetails.clear();
        txtSearch.clear();
    }


    private void loadOrderDetailTable() {
        ObservableList<OrderDetailTm> tmList = FXCollections.observableArrayList();

        try {
            List<OrderDetailsDto> detailDtoList = orderDetailsModel.allOrderDetails();

            for (OrderDetailsDto dto :detailDtoList) {

                JFXButton btn = new JFXButton("Delete");
                OrderDto order = orderModel.getOrder(dto.getOrderId());
                Double total = dto.getQty()*dto.getUnitPrice();

                OrderDetailTm orderDeatail = new OrderDetailTm(
                        dto.getOrderId(),
                        dto.getItemCode(),
                        order.getCustId(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        total,
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteOrderDetail(dto.getOrderId());
                });

                tmList.add(orderDeatail);
            }

            TreeItem<OrderDetailTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrderDetail.setRoot(treeItem);
            tblOrderDetail.setShowRoot(false);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrderDetail(String id) {
        try {
            boolean isDeleted = orderDetailsModel.deleteOrderDetail(id);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadOrderDetailTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
