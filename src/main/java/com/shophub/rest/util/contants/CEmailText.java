package com.shophub.rest.util.contants;

import com.shophub.rest.entity.Invoice;
import com.shophub.rest.entity.Order;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CEmailText {

    public static class OrderMsg {

        public static String CREATION_TITLE(Long orderId) {
            return "[ShobHub] Order #" + orderId + " Placed Successfully!";
        }

        public static String ADMIN_CREATION_TITLE(Long orderId, String clientEmail) {
            return "[ALERT] New Order #" + orderId + " submitted by " + clientEmail;
        }

        public static String USER_CREATION_HTML(Order order) {
            String itemsRows = buildItemsTableRows(order);
            BigDecimal total = calculateTotal(order);

            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                        <div style="background-color: #00B14F; color: white; padding: 20px; text-align: center;">
                            <h2 style="margin: 0;">Thank You for Your Order!</h2>
                            <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                        </div>
                        <div style="padding: 20px; color: #333333; line-height: 1.6;">
                            <p>Hi <strong>%s</strong>,</p>
                            <p>We've received your order and our team is currently reviewing it. Here is your order summary:</p>
                    
                            <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                                <thead>
                                    <tr style="background-color: #f8f9fa; border-bottom: 2px solid #eeeeee;">
                                        <th style="text-align: left; padding: 10px;">Item</th>
                                        <th style="text-align: center; padding: 10px;">Qty</th>
                                        <th style="text-align: right; padding: 10px;">Price</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    %s
                                    <tr style="border-top: 2px solid #eeeeee; font-weight: bold;">
                                        <td colspan="2" style="padding: 10px; text-align: right;">Total Paid:</td>
                                        <td style="padding: 10px; text-align: right; color: #00B14F;">$%s</td>
                                    </tr>
                                </tbody>
                            </table>
                    
                            <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-top: 15px;">
                                <strong style="color: #555555;">Delivery Address:</strong><br/>
                                <span style="color: #333;">%s</span>
                            </div>
                    
                            <p style="margin-top: 25px;">We will notify you as soon as your order status changes.</p>
                            <hr style="border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;"/>
                            <p style="font-size: 12px; color: #777777; text-align: center;">Track your deliveries directly in the ShobHub app.</p>
                        </div>
                    </div>
                    """,
                order.getId(),
                order.getUserCreated().getAccount().getEmail(),
                itemsRows,
                total.toString(),
                order.getShippingAddress()
            );
        }

        public static String ADMIN_CREATION_HTML(Order order) {
            String itemsRows = buildItemsTableRows(order);
            BigDecimal total = calculateTotal(order);

            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <div style="background-color: #333333; color: white; padding: 15px; text-align: center;">
                            <h3 style="margin: 0;">[SYSTEM ALERT] New Order Received</h3>
                            <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                        </div>
                        <div style="padding: 20px; color: #333333; line-height: 1.5;">
                            <p><strong>Attention Admin,</strong></p>
                            <p>A new purchase has been processed. Stock levels have been successfully modified via Optimistic Locking.</p>
                    
                            <p><strong>Customer:</strong> %s<br/>
                            <strong>Timestamp:</strong> %s</p>
                    
                            <table style="width: 100%%; border-collapse: collapse; margin: 15px 0;">
                                <tr style="background-color: #f2f2f2;">
                                    <th style="text-align: left; padding: 8px;">Product</th>
                                    <th style="text-align: center; padding: 8px;">Qty</th>
                                </tr>
                                %s
                            </table>
                            <p><strong>Total Transaction Value:</strong> $%s</p>
                            <p>Please log into the management console to begin processing the packaging requirement.</p>
                        </div>
                    </div>
                    """,
                order.getId(),
                order.getUserCreated().getAccount().getEmail(),
                order.getCreatedAt().toString(),
                itemsRows,
                total.toString()
            );
        }

        public static String CANCEL_TITLE(Long orderId) {
            return "[ShobHub] Order #" + orderId + " Has Been Cancelled";
        }

        public static String CANCEL_HTML(Order order, String cancelledByRole) {
            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                        <div style="background-color: #F44336; color: white; padding: 20px; text-align: center;">
                            <h2 style="margin: 0;">Order Cancelled</h2>
                            <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                        </div>
                        <div style="padding: 20px; color: #333333; line-height: 1.6;">
                            <p>Hi there,</p>
                            <p>Your Order <strong>#%d</strong> has been officially cancelled by the <strong>%s</strong>.</p>
                            <p>If any payment was authorized, refunds will be routed automatically back into your account balance according to standard banking windows (usually 2-5 business days).</p>
                            <p>We look forward to serving you better next time around.</p>
                            <hr style="border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;"/>
                            <p style="font-size: 12px; color: #777777; text-align: center;">ShobHub Support Team</p>
                        </div>
                    </div>
                    """,
                order.getId(),
                order.getId(),
                cancelledByRole
            );
        }

        public static String PREPARE_TITLE(Long orderId) {
            return "[ShobHub] Your order #" + orderId + " is being prepared!";
        }

        public static String PREPARE_HTML(Order order) {
            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                        <div style="background-color: #FF9800; color: white; padding: 20px; text-align: center;">
                            <h2 style="margin: 0;">The Store Is Preparing Your Order</h2>
                            <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                        </div>
                        <div style="padding: 20px; color: #333333; line-height: 1.6;">
                            <p>Great news! Your order has been confirmed by our merchant administration.</p>
                            <p>Our team is carefully packing your items right now to guarantee fresh and secure fulfillment.</p>
                            <p>Hang tight! We will send you another update once the package is handed over to our driver.</p>
                            <hr style="border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;"/>
                            <p style="font-size: 12px; color: #777777; text-align: center;">ShobHub Operations</p>
                        </div>
                    </div>
                    """,
                order.getId()
            );
        }

        public static String DELIVERY_TITLE(Long orderId) {
            return "[ShobHub] Out for Delivery! Order #" + orderId + " is on its way";
        }

        public static String DELIVERY_HTML(Order order) {
            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                        <div style="background-color: #2196F3; color: white; padding: 20px; text-align: center;">
                            <h2 style="margin: 0;">Driver is on the way!</h2>
                            <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                        </div>
                        <div style="padding: 20px; color: #333333; line-height: 1.6;">
                            <p>Your package is moving!</p>
                            <p>We've successfully checked in your order with our third-party logistics partner. A courier has picked up the box and is currently routing towards your destination.</p>
                    
                            <div style="background-color: #f5f5f5; border-left: 4px solid #2196F3; padding: 12px; margin: 15px 0;">
                                <strong>Delivery Address:</strong><br/>
                                %s
                            </div>
                    
                            <p>Please make sure you are available or have designated someone at the site to accept your package delivery.</p>
                        </div>
                    </div>
                    """,
                order.getId(),
                order.getShippingAddress()
            );
        }

        public static String USER_CLOSE_TITLE(Long orderId) {
            return "[ShobHub] Delivered! Your Order #" + orderId + " is complete";
        }

        public static String CLOSE_USER_TITLE(Long orderId) {
            return "[ShobHub] Delivered & Invoiced! Order #" + orderId + " is complete";
        }

        public static String CLOSE_ADMIN_TITLE(Long orderId) {
            return "[SYSTEM] Order #" + orderId + " has been successfully CLOSED";
        }

        public static String CLOSE_USER_HTML(Invoice invoice) {
            String itemsRows = buildInvoiceTableRows(invoice);
            Order order = invoice.getOrder();

            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; border: 1px solid #e0e0e0; padding: 25px; border-radius: 8px; background-color: #ffffff; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">
                        <div style="text-align: center; border-bottom: 2px dashed #cccccc; padding-bottom: 15px;">
                            <div style="background-color: #00B14F; color: white; display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; margin-bottom: 10px;">DELIVERED</div>
                            <h2 style="color: #333333; margin: 0; font-size: 22px; font-weight: bold;">ShobHub E-Invoice</h2>
                            <p style="color: #777777; margin: 5px 0 0 0; font-size: 13px;">Invoice ID: #%d</p>
                        </div>
                    
                        <div style="margin: 20px 0; font-size: 13px; color: #555555; line-height: 1.6;">
                            <table style="width: 100%%;">
                                <tr><td><strong>Order ID:</strong> #%d</td><td style="text-align: right;"><strong>Date Closed:</strong> %s</td></tr>
                                <tr><td><strong>Customer:</strong> %s</td><td style="text-align: right;"><strong>Payment:</strong> Electronic Allocation</td></tr>
                            </table>
                        </div>
                    
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 10px; font-size: 14px;">
                            <thead>
                                <tr style="border-bottom: 1px solid #eeeeee; color: #777777; font-size: 12px;">
                                    <th style="text-align: left; padding: 8px 0;">Item Snapshot</th>
                                    <th style="text-align: center; padding: 8px 0;">Qty</th>
                                    <th style="text-align: right; padding: 8px 0;">Price</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                                <tr style="font-weight: bold; font-size: 16px; border-top: 2px solid #00B14F;">
                                    <td colspan="2" style="padding: 15px 0; color: #333333;">Total Amount Charged</td>
                                    <td style="text-align: right; padding: 15px 0; color: #00B14F;">$%s</td>
                                </tr>
                            </tbody>
                        </table>
                    
                        <div style="background-color: #f9f9f9; padding: 12px; border-radius: 5px; margin-top: 10px; font-size: 12px; color: #666666;">
                            <strong>Shipped To:</strong><br/>
                            %s
                        </div>
                    
                        <div style="margin-top: 25px; padding-top: 15px; border-top: 1px solid #eeeeee; font-size: 11px; color: #999999; text-align: center;">
                            This document serves as an official financial proof of fulfillment. Thank you for shopping with ShobHub!
                        </div>
                    </div>
                    """,
                invoice.getId(),
                order.getId(),
                invoice.getCreatedAt().toString(),
                order.getUserCreated().getAccount().getEmail(),
                itemsRows,
                invoice.getTotalAmount().toString(),
                order.getShippingAddress()
            );
        }

        static String CLOSE_ADMIN_HTML(Long orderId, Long invoiceId, String clientEmail, BigDecimal total) {
            return String.format("""
                    <div style="font-family: Arial, sans-serif; max-width: 550px; margin: auto; border: 1px solid #dcdcdc; border-radius: 6px; overflow: hidden;">
                        <div style="background-color: #333333; color: #ffffff; padding: 12px 20px; font-size: 14px; font-weight: bold;">
                            SYSTEM TASK REPORT: ORDER CLOSED
                        </div>
                        <div style="padding: 20px; color: #444444; font-size: 14px; line-height: 1.5;">
                            <p>Hello Admin,</p>
                            <p>Order <strong>#%d</strong> has been marked as successfully delivered and status is now closed.</p>
                    
                            <div style="background-color: #f5f5f5; border-left: 4px solid #777777; padding: 12px; margin: 15px 0;">
                                <strong>Financial Execution Details:</strong><br/>
                                • <strong>Invoice Database ID:</strong> %d<br/>
                                • <strong>Customer Account:</strong> %s<br/>
                                • <strong>Total Volume Settled:</strong> $%s
                            </div>
                            <p>No further manual shipping or fulfillment operations are required for this transaction chain.</p>
                        </div>
                    </div>
                    """,
                orderId,
                invoiceId,
                clientEmail,
                total.toString()
            );
        }

        // ==========================================
        // PRIVATE UTILITIES
        // ==========================================
        private static String buildInvoiceTableRows(Invoice invoice) {
            if (invoice.getInvoiceItems() == null) return "";
            return invoice.getInvoiceItems().stream()
                .map(item -> String.format(
                    "<tr>" +
                        "<td style='padding: 8px 0; border-bottom: 1px solid #f9f9f9; color: #333;'>%s</td>" +
                        "<td style='padding: 8px 0; text-align: center; border-bottom: 1px solid #f9f9f9; color: #555;'>%d</td>" +
                        "<td style='padding: 8px 0; text-align: right; border-bottom: 1px solid #f9f9f9; color: #333;'>$%s</td>" +
                        "</tr>",
                    item.getProduct() != null ? item.getProduct().getName() : "Archived Product",
                    item.getQuantity(),
                    item.getPriceAtPurchase().toString()
                ))
                .collect(Collectors.joining());
        }

        private static String buildItemsTableRows(Order order) {
            if (order.getOrderItems() == null) return "";
            return order.getOrderItems().stream()
                .map(item -> String.format(
                    "<tr>" +
                        "<td style='padding: 8px; border-bottom: 1px solid #eee;'>%s</td>" +
                        "<td style='padding: 8px; text-align: center; border-bottom: 1px solid #eee;'>%d</td>" +
                        "<td style='padding: 8px; text-align: right; border-bottom: 1px solid #eee;'>$%s</td>" +
                        "</tr>",
                    item.getProduct() != null ? item.getProduct().getName() : "Unknown Product",
                    item.getQuantity(),
                    item.getPriceAtPurchase().toString()
                ))
                .collect(Collectors.joining());
        }

        private static BigDecimal calculateTotal(Order order) {
            if (order.getOrderItems() == null) return BigDecimal.ZERO;
            return order.getOrderItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}