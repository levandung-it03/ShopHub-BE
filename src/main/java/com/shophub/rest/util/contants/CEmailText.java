package com.shophub.rest.util.contants;

import com.shophub.rest.entity.Order;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CEmailText {

    public static class OrderMsg {

        public static String CREATION_TITLE(Long orderId) {
            return "[GrabStore] Order #" + orderId + " Placed Successfully!";
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
            return "[GrabStore] Order #" + orderId + " Has Been Cancelled";
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
            return "[GrabStore] Your order #" + orderId + " is being prepared!";
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
            return "[GrabStore] Out for Delivery! Order #" + orderId + " is on its way";
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

        public static String CLOSE_TITLE(Long orderId) {
            return "[GrabStore] Delivered! Your Order #" + orderId + " is complete";
        }

        public static String CLOSE_HTML(Order order) {
            return String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                    <div style="background-color: #00B14F; color: white; padding: 20px; text-align: center;">
                        <h2 style="margin: 0;">Order Completed successfully!</h2>
                        <p style="margin: 5px 0 0 0;">Order ID: #%d</p>
                    </div>
                    <div style="padding: 20px; color: #333333; line-height: 1.6;">
                        <p>Hi Customer,</p>
                        <p>Third-party courier confirms successful dropship delivery to your location! Your order status has been finalized and closed.</p>
                        <p>We hope you love your purchase. If you have a quick moment, please rate us on your app dashboard to let us know how we did!</p>
                        <br/>
                        <p>Cheers,<br/><strong>The ShobHub Delivery Team</strong></p>
                    </div>
                </div>
                """,
                order.getId()
            );
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