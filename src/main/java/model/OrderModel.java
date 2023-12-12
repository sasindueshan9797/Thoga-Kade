package model;

import db.DBConnection;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface OrderModel {
    boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException;
    OrderDto lastOrder() throws SQLException, ClassNotFoundException;

    OrderDto getOrder(String id) throws SQLException, ClassNotFoundException;

    List<OrderDto> allOrders() throws SQLException, ClassNotFoundException;
}
