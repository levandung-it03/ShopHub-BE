package com.shophub.rest.mapper;

import com.shophub.rest.entity.Invoice;
import com.shophub.rest.entity.InvoiceItem;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceMapper {

    public Invoice createInvoice(Order order) {
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.valueOf(0);
        var output = Invoice.builder()
            .createdAt(Instant.now())
            .build();
        for (OrderItem orderItem: order.getOrderItems()) {
            invoiceItems.add(InvoiceItem.builder()
                .invoice(output)
                .product(orderItem.getProduct())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .build());
            totalAmount = totalAmount.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        output.setTotalAmount(totalAmount);
        output.setInvoiceItems(invoiceItems);
        return output;
    }
}
