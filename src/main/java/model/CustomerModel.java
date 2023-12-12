package model;

import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDto;

import java.sql.SQLException;
import java.util.List;

public interface CustomerModel {
    boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean updateCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException;
    CustomerDto lastCustomer() throws SQLException, ClassNotFoundException;
    List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException;
    CustomerDto getCustomer(String id) throws SQLException, ClassNotFoundException;
}
