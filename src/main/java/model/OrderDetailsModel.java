package model;

import db.DBConnection;
import dto.ItemDto;
import dto.OrderDetailsDto;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsModel {
    boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException;

    boolean deleteOrderDetail(String id) throws SQLException, ClassNotFoundException;

    OrderDetailsDto getOrderDetail(String id) throws SQLException, ClassNotFoundException;

    List<OrderDetailsDto> allOrderDetails() throws SQLException, ClassNotFoundException;
}
