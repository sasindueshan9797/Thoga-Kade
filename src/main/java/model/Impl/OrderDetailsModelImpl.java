package model.Impl;

import db.DBConnection;
import dto.ItemDto;
import dto.OrderDetailsDto;
import model.OrderDetailsModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsModelImpl implements OrderDetailsModel {
    @Override
    public boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException {

        boolean isDetailsSaved = true;

        for (OrderDetailsDto dto:list) {
            String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1,dto.getOrderId());
            pstm.setString(2,dto.getItemCode());
            pstm.setInt(3,dto.getQty());
            pstm.setDouble(4,dto.getUnitPrice());

            if(!(pstm.executeUpdate()>0)){
                isDetailsSaved = false;
            }
        }
        return isDetailsSaved;
    }

    @Override
    public boolean deleteOrderDetail(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE from orderdetail WHERE orderid=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        return pstm.executeUpdate()>0;
    }

    @Override
    public OrderDetailsDto getOrderDetail(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orderdetail WHERE orderid=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()){
            return new OrderDetailsDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getDouble(4)
            );
        }
        return null;
    }


    @Override
    public List<OrderDetailsDto> allOrderDetails() throws SQLException, ClassNotFoundException {
        List<OrderDetailsDto> list = new ArrayList<>();

        String sql = "SELECT * FROM orderdetail";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()){
            list.add(new OrderDetailsDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getDouble(4)

            ));
        }
        return list;
    }
}
