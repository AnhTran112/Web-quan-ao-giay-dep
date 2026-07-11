package com.shop.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trang thai don hang + luat chuyen trang thai.
 *
 * Luong chuan:  PENDING -> CONFIRMED -> SHIPPING -> DELIVERED
 * Huy don:      PENDING / CONFIRMED / SHIPPING -> CANCELLED (hoan lai ton kho)
 * DELIVERED va CANCELLED la trang thai cuoi, khong doi duoc nua.
 */
public final class OrderStatus {

    public static final String PENDING   = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String SHIPPING  = "SHIPPING";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";

    public static final List<String> ALL =
            Arrays.asList(PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED);

    private OrderStatus() {}

    /** Cac trang thai duoc phep chuyen den tu trang thai hien tai. */
    public static List<String> allowedNext(String current) {
        List<String> next = new ArrayList<>();
        if (current == null) return next;
        switch (current) {
            case PENDING:
                next.add(CONFIRMED);
                next.add(CANCELLED);
                break;
            case CONFIRMED:
                next.add(SHIPPING);
                next.add(CANCELLED);
                break;
            case SHIPPING:
                next.add(DELIVERED);
                next.add(CANCELLED);
                break;
            default: // DELIVERED / CANCELLED: trang thai cuoi
                break;
        }
        return next;
    }

    /** Kiem tra buoc chuyen co hop le khong. */
    public static boolean canTransition(String from, String to) {
        return allowedNext(from).contains(to);
    }

    /** Nhan tieng Viet de hien thi. */
    public static String label(String status) {
        if (status == null) return "";
        switch (status) {
            case PENDING:   return "Chờ xử lý";
            case CONFIRMED: return "Đã xác nhận";
            case SHIPPING:  return "Đang giao";
            case DELIVERED: return "Đã giao";
            case CANCELLED: return "Đã hủy";
            default:        return status;
        }
    }
}
