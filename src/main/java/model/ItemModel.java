package model;

import dto.CustomerDto;
import dto.ItemDto;

import java.sql.SQLException;
import java.util.List;

public interface ItemModel {
    boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException;
    boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException;
    boolean deleteItem(String code) throws SQLException, ClassNotFoundException;
    ItemDto getItem(String code) throws SQLException, ClassNotFoundException;
    ItemDto lastItem() throws SQLException, ClassNotFoundException;
    List<ItemDto> allItems() throws SQLException, ClassNotFoundException;
}
