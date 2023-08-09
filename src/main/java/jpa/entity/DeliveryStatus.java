package jpa.entity;

public enum DeliveryStatus {
    READY {
        @Override
        public String toString(){
            return "준비";
        }
    }, COMP{
        @Override
        public String toString(){
            return "배송";
        }
    }
}
