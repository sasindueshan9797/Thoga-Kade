package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailTm extends RecursiveTreeObject<OrderDetailTm> {
    private String orderId;
    private String itemCode;
    private String custId;
    private double unitPrice;
    private int qty;
    private double total;
    private JFXButton btn;
}
