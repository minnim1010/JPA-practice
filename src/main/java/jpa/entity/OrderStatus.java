package jpa.entity;

public enum OrderStatus {
    ORDER {
        @Override
        public String toString(){
            return "주문 완료";
        }
    }, CANCEL{
        @Override
        public String toString(){
            return "주문 취소";
        }
    }
}
