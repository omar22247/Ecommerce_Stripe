package com.omar.ecommerce.entity;

public enum OrderStatus {

    PENDING,
    PAID,
    CANCELLED,
    FAILED;

    public boolean canTransitionTo(OrderStatus target) {

        if (target == null) {
            return false;
        }

        return switch (this) {
            case PENDING ->
                    target == PAID
                            || target == CANCELLED
                            || target == FAILED;

            case PAID, CANCELLED, FAILED ->
                    false;
        };
    }

    public boolean isFinal() {
        return this == PAID
                || this == CANCELLED
                || this == FAILED;
    }
}